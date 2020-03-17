package vn.ehealth.hl7.fhir.term.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ValueSet;
import org.hl7.fhir.r4.model.ValueSet.ConceptReferenceComponent;
import org.hl7.fhir.r4.model.ValueSet.ConceptReferenceDesignationComponent;
import org.hl7.fhir.r4.model.ValueSet.ConceptSetComponent;
import org.hl7.fhir.r4.model.ValueSet.ValueSetComposeComponent;
import org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionComponent;
import org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.term.dao.IValueSet;
import vn.ehealth.hl7.fhir.term.dao.transform.ValueSetEntityToFHIRValueSet;
import vn.ehealth.hl7.fhir.term.entity.ConceptDesignationEntity;
import vn.ehealth.hl7.fhir.term.entity.ConceptEntity;
import vn.ehealth.hl7.fhir.term.entity.ConceptReferenceDesignationEntity;
import vn.ehealth.hl7.fhir.term.entity.ConceptReferenceEntity;
import vn.ehealth.hl7.fhir.term.entity.ConceptSetEntity;
import vn.ehealth.hl7.fhir.term.entity.ValueSetComposeEntity;
import vn.ehealth.hl7.fhir.term.entity.ValueSetContainEntity;
import vn.ehealth.hl7.fhir.term.entity.ValueSetEntity;
import vn.ehealth.hl7.fhir.term.entity.ValueSetExpansionEntity;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Repository
public class ValueSetDao implements IValueSet {
    @Autowired
    MongoOperations mongo;

    @Autowired
    ValueSetEntityToFHIRValueSet valueSetEntityToFHIRValueSet;

    @Override
    public ValueSet create(FhirContext fhirContext, ValueSet object) {
        ValueSetEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            // save ValueSetEntity
            entity = createNewValueSetEntity(object, version, null);
            entity = saveValueSetEntity(object, version, entity, null);
            return valueSetEntityToFHIRValueSet.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "valueSet", key = "#idType")
    public ValueSet update(FhirContext fhirContext, ValueSet object, IdType idType) {
        ValueSetEntity entityOld = null;
        String fhirId = "";
        if (idType != null) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, ValueSetEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove ValueSetEntity old
            removeValueSetEntity(entityOld);
            // save ValueSetEntity
            int version = entityOld.version + 1;
            if (object != null) {
                ValueSetEntity entity = createNewValueSetEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                entity = saveValueSetEntity(object, version, entity, fhirId);
                return valueSetEntityToFHIRValueSet.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "valueSet", key = "#idType")
    public ValueSet read(FhirContext fhirContext, IdType idType) {
        if (idType != null) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ValueSetEntity entity = mongo.findOne(query, ValueSetEntity.class);
            if (entity != null) {
                ObjectId valueSetId = entity.id;
                // compose
                ValueSetComposeEntity compose = readCompose(valueSetId.toString(), null);
                entity.compose = (compose);
                // expansion
                ValueSetExpansionEntity expansion = readExpansion(valueSetId.toString(), null);
                entity.expansion = (expansion);
                return valueSetEntityToFHIRValueSet.transform(entity);
            }
        }
        return null;
    }

    @Override
    public ValueSet readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                ValueSetEntity entity = mongo.findOne(query, ValueSetEntity.class);
                if (entity != null) {
                    ObjectId valueSetId = entity.id;
                    // compose
                    ValueSetComposeEntity compose = readCompose(valueSetId.toString(), version);
                    entity.compose = (compose);
                    // expansion
                    ValueSetExpansionEntity expansion = readExpansion(valueSetId.toString(), version);
                    entity.expansion = (expansion);
                    return valueSetEntityToFHIRValueSet.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "valueSet", key = "#idType")
    public ValueSet remove(FhirContext fhirContext, IdType idType) {
        if (idType != null) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ValueSetEntity entity = mongo.findOne(query, ValueSetEntity.class);
            if (entity != null) {
                // remove ValueSetEntity old
                entity.active = (false);
                entity.resDeleted = (new Date());
                removeValueSetEntity(entity);
                return valueSetEntityToFHIRValueSet.transform(entity);
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, DateRangeParam date,
            StringParam description, UriParam expansion, TokenParam identifier, TokenParam jurisdiction,
            StringParam name, StringParam publisher, UriParam reference, TokenParam status, StringParam title,
            UriParam url, TokenParam version, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
            String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();

        Criteria criteria = null;
        criteria = Criteria.where("active").is(true);
        // default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        // custom
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "date", date);
        }
        if (description != null) {
            criteria.and("description").is(description.getValue());
        }
        if (jurisdiction != null) {
            criteria.and("jurisdiction").regex(jurisdiction.getValue());
        }
        if (name != null) {
            criteria.and("name").regex(name.getValue());
        }

        if (publisher != null) {
            criteria.and("publisher").regex(publisher.getValue());
        }

        if (status != null) {
            criteria.and("status").regex(status.getValue());
        }

        if (title != null) {
            criteria.and("title").regex(title.getValue());
        }
        if (url != null) {
            criteria.and("url").regex(url.getValue());
        }
        if (version != null) {
            criteria.and("version").regex(version.getValue());
        }

        if (criteria != null) {
            Query qry = Query.query(criteria);
            Pageable pageableRequest;
            pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                    count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
            qry.with(pageableRequest);
            if (sortParam != null && !sortParam.equals("")) {
                qry.with(new Sort(Sort.Direction.ASC, sortParam));
            }
            List<ValueSetEntity> results = mongo.find(qry, ValueSetEntity.class);
            if (results != null && results.size() > 0) {
                if (reference != null || expansion != null) {
                    if (reference != null && expansion != null) {
                        for (ValueSetEntity valueSetEntity : results) {
                            // composes
                            Criteria criteria1 = null;
                            criteria1 = Criteria.where("valueSetId").is(valueSetEntity.id.toString());
                            Query qry1 = Query.query(criteria1);
                            ValueSetComposeEntity valueSetComposeEntity = mongo.findOne(qry1,
                                    ValueSetComposeEntity.class);
                            List<ConceptSetEntity> ConceptSetEntitys = new ArrayList<>();
                            if (valueSetComposeEntity != null) {
                                Criteria criteria2 = null;
                                criteria2 = Criteria.where("valueSetComposeId")
                                        .is(valueSetComposeEntity.id.toString());
                                criteria2.and("type").is("include").and("system").is(reference.getValue());
                                Query qry2 = Query.query(criteria2);
                                ConceptSetEntitys = mongo.find(qry2, ConceptSetEntity.class);
                            }

                            // Expansion
                            Criteria criteria3 = null;
                            criteria3 = Criteria.where("valueSetId").is(valueSetEntity.id.toString());
                            if (expansion != null) {
                                criteria3.and("identifier.myStringValue").regex(expansion.getValue());
                            }
                            Query qry3 = Query.query(criteria3);
                            ValueSetExpansionEntity expansio = mongo.findOne(qry3, ValueSetExpansionEntity.class);
                            if (ConceptSetEntitys != null && ConceptSetEntitys.size() > 0 && expansio != null) {
                                resources.add(valueSetEntityToFHIRValueSet.transform(valueSetEntity));
                            }
                        }
                    } else if (reference != null && expansion == null) {
                        for (ValueSetEntity valueSetEntity : results) {
                            // composes
                            Criteria criteria1 = null;
                            criteria1 = Criteria.where("valueSetId").is(valueSetEntity.id.toString());
                            Query qry1 = Query.query(criteria1);
                            ValueSetComposeEntity valueSetComposeEntity = mongo.findOne(qry1,
                                    ValueSetComposeEntity.class);
                            List<ConceptSetEntity> ConceptSetEntitys = new ArrayList<>();
                            if (valueSetComposeEntity != null) {
                                Criteria criteria2 = null;
                                criteria2 = Criteria.where("valueSetComposeId")
                                        .is(valueSetComposeEntity.id.toString());
                                criteria2.and("type").is("include").and("system").is(reference.getValue());
                                Query qry2 = Query.query(criteria2);
                                ConceptSetEntitys = mongo.find(qry2, ConceptSetEntity.class);
                            }
                            if (ConceptSetEntitys != null && ConceptSetEntitys.size() > 0) {
                                resources.add(valueSetEntityToFHIRValueSet.transform(valueSetEntity));
                            }
                        }
                    } else {
                        for (ValueSetEntity valueSetEntity : results) {
                            // Expansion
                            Criteria criteria3 = null;
                            criteria3 = Criteria.where("valueSetId").is(valueSetEntity.id.toString());
                            if (expansion != null) {
                                criteria3.and("identifier.myStringValue").regex(expansion.getValue());
                            }
                            Query qry3 = Query.query(criteria3);
                            ValueSetExpansionEntity expansio = mongo.findOne(qry3, ValueSetExpansionEntity.class);
                            if (expansio != null) {
                                resources.add(valueSetEntityToFHIRValueSet.transform(valueSetEntity));
                            }
                        }
                    }

                } else {
                    for (ValueSetEntity valueSetEntity : results) {
                        resources.add(valueSetEntityToFHIRValueSet.transform(valueSetEntity));
                    }
                }
            }
        }
        return resources;
    }

    public List<ValueSetContainEntity> checkHasChild(String valueSetExpansionId,
            List<ValueSetExpansionContainsComponent> expansions, String parentId) {
        List<ValueSetContainEntity> valueSetContainEntitys = new ArrayList<>();
        for (ValueSetExpansionContainsComponent v : expansions) {
            ValueSetContainEntity valueSetContainEntity = new ValueSetContainEntity();
            valueSetContainEntity.code = (v.getCode());
            valueSetContainEntity.system = (v.getSystem());
            valueSetContainEntity._abstract = (v.getAbstract());
            valueSetContainEntity.inactive = (v.getInactive());
            valueSetContainEntity.version = (Integer.parseInt(v.getVersion()));
            valueSetContainEntity.display = (v.getDisplay());
            valueSetContainEntity.valueSetExpansionId = (valueSetExpansionId);
            valueSetContainEntity.parentContainId = (parentId);
            // designation
            if (v.hasDesignation()) {
                List<ConceptReferenceDesignationEntity> conceptDesignationEntitys = new ArrayList<>();
                for (ConceptReferenceDesignationComponent conceptReferenceDesignationComponent : v.getDesignation()) {
                    ConceptReferenceDesignationEntity conceptDesignationEntity = new ConceptReferenceDesignationEntity();
                    conceptDesignationEntity.language = (conceptReferenceDesignationComponent.getLanguage());
                    conceptDesignationEntity.use = BaseCoding.fromCoding(conceptReferenceDesignationComponent.getUse());
                    conceptDesignationEntity.value = (conceptReferenceDesignationComponent.getValue());
                    conceptDesignationEntitys.add(conceptDesignationEntity);
                }
                valueSetContainEntity.designation = (conceptDesignationEntitys);
            }
            mongo.save(valueSetContainEntity);
            if (v.hasContains()) {
                List<ValueSetContainEntity> valueSetContainEntitytmps = new ArrayList<>();
                valueSetContainEntitytmps = checkHasChild(valueSetExpansionId, v.getContains(),
                        valueSetContainEntity.id.toString());
                valueSetContainEntity.contains = (valueSetContainEntitytmps);
            }
            valueSetContainEntitys.add(valueSetContainEntity);
        }
        return valueSetContainEntitys;
    }

    public ValueSetComposeEntity composes(ValueSetComposeEntity valueSetComposeEntity) {
        if (valueSetComposeEntity != null) {
            Criteria criteria2 = null;
            criteria2 = Criteria.where("valueSetComposeId").is(valueSetComposeEntity.id.toString());
            Query qry2 = Query.query(criteria2);
            List<ConceptSetEntity> ConceptSetEntitys = mongo.find(qry2, ConceptSetEntity.class);
            for (ConceptSetEntity ConceptSetEntity : ConceptSetEntitys) {
                Criteria criteria3 = null;
                criteria3 = Criteria.where("codeSystemId").is(ConceptSetEntity.id.toString())
                        .and("parentConceptId").is("");
                Query qry3 = Query.query(criteria3);
                List<ConceptEntity> conceptEntitys = mongo.find(qry3, ConceptEntity.class);
                if(conceptEntitys != null) {
                    ConceptSetEntity.concept = new ArrayList<>();
                    conceptEntitys.forEach(x -> {
                        ConceptSetEntity.concept.add(new ConceptReferenceEntity(x.code, x.display));
                    });
                }                           
            }
            valueSetComposeEntity.include = (ConceptSetEntitys);
        }
        return valueSetComposeEntity;
    }

    public ValueSetExpansionEntity expansions(ValueSetExpansionEntity valueSetExpansionEntity) {
        if (valueSetExpansionEntity != null) {
            Criteria criteria2 = null;
            criteria2 = Criteria.where("valueSetExpansionId").is(valueSetExpansionEntity.id.toString())
                    .and("parentContainId").is("");
            Query qry2 = Query.query(criteria2);
            List<ValueSetContainEntity> valueSetContainEntitys = mongo.find(qry2, ValueSetContainEntity.class);
            List<ValueSetContainEntity> valueSetContainTmps = concepts(valueSetContainEntitys,
                    valueSetExpansionEntity.id.toString());
            valueSetExpansionEntity.contains = (valueSetContainTmps);
        }
        return valueSetExpansionEntity;
    }

    public List<ValueSetContainEntity> concepts(List<ValueSetContainEntity> concepts, String valueSetExpansionId) {
        List<ValueSetContainEntity> conceptmps = new ArrayList<>();
        if (concepts.size() > 0) {
            for (ValueSetContainEntity conceptEntity : concepts) {
                Criteria criteria2 = null;
                criteria2 = Criteria.where("valueSetExpansionId").regex(valueSetExpansionId);
                criteria2.and("parentContainId").regex(conceptEntity.id.toString());
                Query qry2 = Query.query(criteria2);
                List<ValueSetContainEntity> conceptresultmps = mongo.find(qry2, ValueSetContainEntity.class);
                if (conceptresultmps.size() > 0) {
                    concepts(conceptresultmps, valueSetExpansionId);
                    conceptEntity.contains = (conceptresultmps);
                }
            }
            conceptmps.addAll(concepts);
        }
        return conceptmps;
    }

    @Override
    public long getTotal(FhirContext ctx, DateRangeParam date, StringParam description, UriParam expansion,
            TokenParam identifier, TokenParam jurisdiction, StringParam name, StringParam publisher, UriParam reference,
            TokenParam status, StringParam title, UriParam url, TokenParam version, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        List<Resource> resources = new ArrayList<>();

        Criteria criteria = null;
        criteria = Criteria.where("active").is(true);
        // default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        // custom
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "date", date);
        }
        if (description != null) {
            criteria.and("description").is(description.getValue());
        }
        if (identifier != null) {
            criteria.and("identifiers.system").is(identifier.getSystem()).and("identifiers.value")
                    .is(identifier.getValue());
        }
        if (jurisdiction != null) {
            criteria.and("jurisdiction").regex(jurisdiction.getValue());
        }
        if (name != null) {
            criteria.and("name").regex(name.getValue());
        }

        if (publisher != null) {
            criteria.and("publisher").regex(publisher.getValue());
        }

        if (status != null) {
            criteria.and("status").regex(status.getValue());
        }

        if (title != null) {
            criteria.and("title").regex(title.getValue());
        }
        if (url != null) {
            criteria.and("url").regex(url.getValue());
        }
        if (version != null) {
            criteria.and("version").regex(version.getValue());
        }
        Query query = Query.query(criteria);
        List<ValueSetEntity> results = mongo.find(query, ValueSetEntity.class);
        if (results != null && results.size() > 0) {
            if (reference != null || expansion != null) {
                if (reference != null && expansion != null) {
                    for (ValueSetEntity valueSetEntity : results) {
                        // composes
                        Criteria criteria1 = null;
                        criteria1 = Criteria.where("valueSetId").is(valueSetEntity.id.toString());
                        Query qry1 = Query.query(criteria1);
                        ValueSetComposeEntity valueSetComposeEntity = mongo.findOne(qry1, ValueSetComposeEntity.class);
                        List<ConceptSetEntity> ConceptSetEntitys = new ArrayList<>();
                        if (valueSetComposeEntity != null) {
                            Criteria criteria2 = null;
                            criteria2 = Criteria.where("valueSetComposeId")
                                    .is(valueSetComposeEntity.id.toString());
                            criteria2.and("type").is("include").and("system").is(reference.getValue());
                            Query qry2 = Query.query(criteria2);
                            ConceptSetEntitys = mongo.find(qry2, ConceptSetEntity.class);
                        }

                        // Expansion
                        Criteria criteria3 = null;
                        criteria3 = Criteria.where("valueSetId").is(valueSetEntity.id.toString());
                        if (expansion != null) {
                            criteria3.and("identifier.myStringValue").regex(expansion.getValue());
                        }
                        Query qry3 = Query.query(criteria3);
                        ValueSetExpansionEntity expansio = mongo.findOne(qry3, ValueSetExpansionEntity.class);
                        if (ConceptSetEntitys != null && ConceptSetEntitys.size() > 0 && expansio != null) {
                            resources.add(valueSetEntityToFHIRValueSet.transform(valueSetEntity));
                        }
                    }
                } else if (reference != null && expansion == null) {
                    for (ValueSetEntity valueSetEntity : results) {
                        // composes
                        Criteria criteria1 = null;
                        criteria1 = Criteria.where("valueSetId").is(valueSetEntity.id.toString());
                        Query qry1 = Query.query(criteria1);
                        ValueSetComposeEntity valueSetComposeEntity = mongo.findOne(qry1, ValueSetComposeEntity.class);
                        List<ConceptSetEntity> ConceptSetEntitys = new ArrayList<>();
                        if (valueSetComposeEntity != null) {
                            Criteria criteria2 = null;
                            criteria2 = Criteria.where("valueSetComposeId")
                                    .is(valueSetComposeEntity.id.toString());
                            criteria2.and("type").is("include").and("system").is(reference.getValue());
                            Query qry2 = Query.query(criteria2);
                            ConceptSetEntitys = mongo.find(qry2, ConceptSetEntity.class);
                        }
                        if (ConceptSetEntitys != null && ConceptSetEntitys.size() > 0) {
                            resources.add(valueSetEntityToFHIRValueSet.transform(valueSetEntity));
                        }
                    }
                } else {
                    for (ValueSetEntity valueSetEntity : results) {
                        // Expansion
                        Criteria criteria3 = null;
                        criteria3 = Criteria.where("valueSetId").is(valueSetEntity.id.toString());
                        if (expansion != null) {
                            criteria3.and("identifier.myStringValue").regex(expansion.getValue());
                        }
                        Query qry3 = Query.query(criteria3);
                        ValueSetExpansionEntity expansio = mongo.findOne(qry3, ValueSetExpansionEntity.class);
                        if (expansio != null) {
                            resources.add(valueSetEntityToFHIRValueSet.transform(valueSetEntity));
                        }
                    }
                }

            } else {
                for (ValueSetEntity valueSetEntity : results) {
                    resources.add(valueSetEntityToFHIRValueSet.transform(valueSetEntity));
                }
            }
        }
        return resources.size();
    }

    private ValueSetExpansionEntity readExpansion(String valueSetId, Integer version) {
        ValueSetExpansionEntity expansion = new ValueSetExpansionEntity();
        Query query = new Query();
        if (version != null) {
            query = Query.query(
                    Criteria.where(ConstantKeys.SP_VALUE_SET_ID).is(valueSetId).and(ConstantKeys.SP_VERSION).is(true));
        } else {
            query = Query.query(
                    Criteria.where(ConstantKeys.SP_VALUE_SET_ID).is(valueSetId).and(ConstantKeys.SP_ACTIVE).is(true));
        }
        expansion = mongo.findOne(query, ValueSetExpansionEntity.class);
        if (expansion != null && expansion.id != null) {
            ObjectId valueSetExpansionId = expansion.id;
            List<ValueSetContainEntity> containss = new ArrayList<>();
            containss = readContainss(valueSetExpansionId.toString(), null, version);
            if (containss != null) {
                expansion.contains = (containss);
            }
        }
        return expansion;
    }

    private List<ValueSetContainEntity> readContainss(String valueSetExpansionId, String parentContainId,
            Integer version) {
        List<ValueSetContainEntity> containsParents = new ArrayList<>();
        Query queryParent = null;
        if (parentContainId != null && !parentContainId.isEmpty()) {
            if (version != null) {
                queryParent = Query.query(Criteria.where(ConstantKeys.SP_VALUE_SET_EXPANSION_ID).is(valueSetExpansionId)
                        .and(ConstantKeys.SP_VERSION).is(version).and(ConstantKeys.SP_PARENT_CONTAIN_ID)
                        .is(parentContainId));
            } else {
                queryParent = Query.query(Criteria.where(ConstantKeys.SP_VALUE_SET_EXPANSION_ID).is(valueSetExpansionId)
                        .and(ConstantKeys.SP_ACTIVE).is(true).and(ConstantKeys.SP_PARENT_CONTAIN_ID)
                        .is(parentContainId));
            }
        } else {
            if (version != null) {
                queryParent = Query.query(Criteria.where(ConstantKeys.SP_VALUE_SET_EXPANSION_ID).is(valueSetExpansionId)
                        .and(ConstantKeys.SP_VERSION).is(version).and(ConstantKeys.SP_PARENT_CONTAIN_ID).is(""));
            } else {
                queryParent = Query.query(Criteria.where(ConstantKeys.SP_VALUE_SET_EXPANSION_ID).is(valueSetExpansionId)
                        .and(ConstantKeys.SP_ACTIVE).is(true).and(ConstantKeys.SP_PARENT_CONTAIN_ID).is(""));
            }

        }

        containsParents = mongo.find(queryParent, ValueSetContainEntity.class);
        if (containsParents != null) {
            for (ValueSetContainEntity item : containsParents) {
                List<ValueSetContainEntity> containss = new ArrayList<>();
                containss = readContainss(valueSetExpansionId, item.id.toString(), version);
                if (containss != null) {
                    item.contains = (containss);
                }
            }
        }
        return containsParents;
    }

    private ValueSetComposeEntity readCompose(String valueSetId, Integer version) {
        ValueSetComposeEntity compose = new ValueSetComposeEntity();
        Query query = new Query();
        if (version != null) {
            query = Query.query(Criteria.where(ConstantKeys.SP_VALUE_SET_ID).is(valueSetId).and(ConstantKeys.SP_VERSION)
                    .is(version));
        } else {
            query = Query.query(
                    Criteria.where(ConstantKeys.SP_VALUE_SET_ID).is(valueSetId).and(ConstantKeys.SP_ACTIVE).is(true));
        }
        compose = mongo.findOne(query, ValueSetComposeEntity.class);
        if (compose != null && compose.id != null) {
            ObjectId valueSetComposeId = compose.id;
            // include - exclude
            List<ConceptSetEntity> includes = readIncludeAndExclude(valueSetComposeId.toString(), version);
            compose.include = (includes);
        }
        return compose;
    }

    private List<ConceptSetEntity> readIncludeAndExclude(String valueSetComposeId, Integer version) {
        List<ConceptSetEntity> includes = new ArrayList<>();
        Query query = new Query();
        if (version != null) {
            query = Query.query(Criteria.where(ConstantKeys.SP_VALUE_SET_COMPOSE_ID).is(valueSetComposeId)
                    .and(ConstantKeys.SP_VERSION).is(version));
        } else {
            query = Query.query(Criteria.where(ConstantKeys.SP_VALUE_SET_COMPOSE_ID).is(valueSetComposeId)
                    .and(ConstantKeys.SP_ACTIVE).is(true));
        }
        includes = mongo.find(query, ConceptSetEntity.class);
        if (includes != null) {
            for (ConceptSetEntity item : includes) {
                List<ConceptEntity> concepts = new ArrayList<>();
                concepts = readConcepts(item.id.toString(), null, version);
                if (concepts != null) {
                    item.concept = new ArrayList<>();
                    concepts.forEach(x -> {
                        item.concept.add(new ConceptReferenceEntity(x.code, x.display));
                    });
                }
            }
        }
        return includes;
    }

    private List<ConceptEntity> readConcepts(String codeSystemId, String parentConceptId, Integer version) {
        List<ConceptEntity> conceptParents = new ArrayList<>();
        Criteria criteria = new Criteria();
        if (codeSystemId != null && !codeSystemId.isEmpty()) {
            if (version != null) {
                criteria.and(ConstantKeys.SP_CODE_SYSTEM_ID).is(codeSystemId).and(ConstantKeys.SP_VERSION).is(version);
            } else {
                criteria.and(ConstantKeys.SP_CODE_SYSTEM_ID).is(codeSystemId).and(ConstantKeys.SP_ACTIVE).is(true);
            }
            if (parentConceptId != null && !parentConceptId.isEmpty()) {
                criteria.and(ConstantKeys.SP_PARENT_CONCEPT_ID).is(parentConceptId);
            } else {
                criteria.orOperator(new Criteria(ConstantKeys.SP_PARENT_CONCEPT_ID).is(null),
                        new Criteria(ConstantKeys.SP_PARENT_CONCEPT_ID).is(""));
            }
        }
        Query query = new Query(criteria);
        if (query != null) {
            conceptParents = mongo.find(query, ConceptEntity.class);
        }
        if (conceptParents != null) {
            for (ConceptEntity item : conceptParents) {
                List<ConceptEntity> concepts = new ArrayList<>();
                concepts = readConcepts(codeSystemId, item.id.toString(), version);
                if (concepts != null) {
                    item.concept = (concepts);
                }
            }
        }
        return conceptParents;
    }

    private ValueSetEntity saveValueSetEntity(ValueSet object, int version, ValueSetEntity entity, String fhirId) {
        mongo.save(entity);

        ObjectId valueSetEntityId = entity.id;
        // save and add compose start
        if (valueSetEntityId != null && object.hasCompose()) {
            ValueSetComposeEntity compose = saveCompose(object.getCompose(), version, valueSetEntityId.toString(),
                    fhirId);
            entity.compose = (compose);
        }
        // save and add compose end

        // save and add expansion start
        if (valueSetEntityId != null && object.hasExpansion()) {
            ValueSetExpansionEntity expansion = createNewValueSetExpansionEntity(object.getExpansion(), version,
                    valueSetEntityId.toString());
            if (fhirId != null && !fhirId.isEmpty()) {
                expansion.resUpdated = (new Date());
            }
            mongo.save(expansion);
            ObjectId valueSetExpansionEntityId = expansion.id;
            // save and add contains for expansion start
            if (valueSetExpansionEntityId != null && object.getExpansion().hasContains()) {
                List<ValueSetContainEntity> containss = new ArrayList<>();
                containss = saveValueSetContainEntity(object.getExpansion().getContains(), version,
                        valueSetExpansionEntityId.toString(), null, fhirId);
                expansion.contains = (containss);
            }
            // save and add contains for expansion end
        }
        // save and add expansion end
        return entity;
    }

    private ValueSetComposeEntity saveCompose(ValueSetComposeComponent object, int version, String valueSetId,
            String fhirId) {
        ValueSetComposeEntity compose = createNewValueSetComposeEntity(object, version, valueSetId);
        if (fhirId != null && !fhirId.isEmpty()) {
            compose.resUpdated = (new Date());
        }
        mongo.save(compose);
        ObjectId valueSetComposeEntityId = compose.id;
        // save include
        if (valueSetComposeEntityId != null && object.hasInclude() && !valueSetComposeEntityId.toString().isEmpty()) {
            String type = ConstantKeys.VALUESETCOMPOSE_TYPE_INCLUDE;
            List<ConceptSetEntity> includes = new ArrayList<>();
            includes = saveInclude(object.getInclude(), version, valueSetComposeEntityId.toString(), fhirId, type);
            compose.include = (includes);
        }
        // save exclude
        if (valueSetComposeEntityId != null && object.hasExclude() && !valueSetComposeEntityId.toString().isEmpty()) {
            String type = ConstantKeys.VALUESETCOMPOSE_TYPE_EXCLUDE;
            List<ConceptSetEntity> excludes = new ArrayList<>();
            excludes = saveInclude(object.getExclude(), version, valueSetComposeEntityId.toString(), fhirId, type);
            compose.include = (excludes);
        }
        return compose;
    }

    private List<ConceptSetEntity> saveInclude(List<ConceptSetComponent> objectIncludes, int version,
            String valueSetComposeId, String fhirId, String type) {
        List<ConceptSetEntity> includes = new ArrayList<>();
        for (ConceptSetComponent item : objectIncludes) {
            ConceptSetEntity include = createNewConceptSetEntity(item, version, type, valueSetComposeId);
            if (fhirId != null && !fhirId.isEmpty()) {
                include.resUpdated = (new Date());
            }
            mongo.save(include);
            if (include.id != null && item.hasConcept() && !include.id.toString().isEmpty()) {
                List<ConceptEntity> concepts = new ArrayList<>();
                concepts = saveConcept(item.getConcept(), version, include.id.toString(), fhirId);
                if(concepts != null) {
                    include.concept = new ArrayList<>();
                    concepts.forEach(x -> include.concept.add(new ConceptReferenceEntity(x.code, x.display)));
                }
            }
            includes.add(include);
        }
        return includes;
    }

    private List<ConceptEntity> saveConcept(List<ConceptReferenceComponent> objectConcepts, int version,
            String ConceptSetEntityId, String fhirId) {
        List<ConceptEntity> concepts = new ArrayList<>();
        for (ConceptReferenceComponent item : objectConcepts) {
            ConceptEntity concept = createNewConceptEntity(item, version, ConceptSetEntityId);
            if (fhirId != null && !fhirId.isEmpty()) {
                concept.resUpdated = (new Date());
            }
            mongo.save(concept);
            concepts.add(concept);
        }
        return concepts;
    }

    private List<ValueSetContainEntity> saveValueSetContainEntity(List<ValueSetExpansionContainsComponent> objects,
            int version, String valueSetExpansionEntityId, String parentId, String fhirId) {
        List<ValueSetContainEntity> containsParents = new ArrayList<>();
        for (ValueSetExpansionContainsComponent itemContains : objects) {
            ValueSetContainEntity containsParent = createNewValueSetContainEntity(itemContains, version,
                    valueSetExpansionEntityId, parentId);
            if (fhirId != null && !fhirId.isEmpty()) {
                containsParent.resUpdated = (new Date());
            }
            mongo.save(containsParent);
            ObjectId containsParentId = containsParent.id;
            // save and add contains for contains start
            if (containsParentId != null && itemContains.hasContains()) {
                List<ValueSetContainEntity> containss = new ArrayList<>();
                containss = saveValueSetContainEntity(itemContains.getContains(), version, valueSetExpansionEntityId,
                        parentId, fhirId);
                containsParent.contains = (containss);
            }
            // save and add contains for contains end
            containsParents.add(containsParent);
        }
        return containsParents;
    }

    private ValueSetEntity createNewValueSetEntity(ValueSet object1, int version, String fhirId) {
        var entity = ValueSetEntity.fromValueSet(object1);
        DataConvertUtil.setMetaExt(object1, entity);
       
        // fhirId
        if (fhirId != null && !fhirId.isEmpty()) {
            entity.fhirId = (fhirId);
        } else {
            entity.fhirId = (StringUtil.generateUID());
        }

        entity.active = (true);
        entity.version = (version);
        entity.resCreated = (new Date());
        return entity;
    }

    private ValueSetComposeEntity createNewValueSetComposeEntity(ValueSetComposeComponent object, int version,
            String valueSetEntityId) {
        ValueSetComposeEntity entity = new ValueSetComposeEntity();
        // valueSetEntityId
        entity.valueSetId = (valueSetEntityId);
        // lockedDate
        if (object.hasLockedDate()) {
            entity.lockedDate = (object.getLockedDate());
        }
        // inactive
        if (object.hasInactive()) {
            entity.inactive = (object.getInactive());
        }
        // active
        entity.active = (true);
        // version
        entity.version = (version);
        // ResCreated
        entity.resCreated = (new Date());
        return entity;

    }

    private ConceptSetEntity createNewConceptSetEntity(ConceptSetComponent object1, int version, String type,
            String valueSetComposeId) {
        var entity = ConceptSetEntity.fromConceptSetComponent(object1);
        entity.type = (type);
        entity.active = (true);
        entity.resCreated = (new Date());
        return entity;
    }

    private ConceptEntity createNewConceptEntity(ConceptReferenceComponent object, int version,
            String ConceptSetEntityId) {
        ConceptEntity entity = new ConceptEntity();
        // ConceptSetEntityId
        entity.codeSystemId = (ConceptSetEntityId);
        // code
        if (object.hasCode()) {
            entity.code = (object.getCode());
        }
        // display
        if (object.hasDisplay()) {
            entity.display = (object.getDisplay());
        }
        // designation
        if (object.hasDesignation()) {
            List<ConceptDesignationEntity> designations = new ArrayList<>();
            for (ConceptReferenceDesignationComponent item : object.getDesignation()) {
                ConceptDesignationEntity designation = convertConceptReferenceDesignationComponentToConceptDesignationEntity(
                        item);
                designations.add(designation);
            }
            entity.designation = (designations);
        }
        // active
        entity.active = (true);
        // version
        entity.version = (version);
        // ResCreated
        entity.resCreated = (new Date());
        return entity;
    }

    private ConceptDesignationEntity convertConceptReferenceDesignationComponentToConceptDesignationEntity(
            ConceptReferenceDesignationComponent object) {
        ConceptDesignationEntity entity = new ConceptDesignationEntity();
        // language
        if (object.hasLanguage()) {
            entity.language =(object.getLanguage());
        }
        // use
        if (object.hasUse()) {
            entity.use = BaseCoding.fromCoding(object.getUse());
        }
        // value
        if (object.hasValue()) {
            entity.value = (object.getValue());
        }
        return entity;
    }

  
    private ValueSetExpansionEntity createNewValueSetExpansionEntity(ValueSetExpansionComponent object, int version,
            String valueSetEntityId) {
        var entity = ValueSetExpansionEntity.fromValueSetExpansionComponent(object);
        entity.active = (true);
        entity.version = (version);
        entity.resCreated = (new Date());
        return entity;
    }

    private ValueSetContainEntity createNewValueSetContainEntity(ValueSetExpansionContainsComponent object, int version,
            String valueSetExpansionEntityId, String parentId) {
        
        var entity = ValueSetContainEntity.fromValueSetExpansionContainsComponent(object);
        // valueSetExpansionEntityId
        if (valueSetExpansionEntityId != null && !valueSetExpansionEntityId.isEmpty()) {
            entity.valueSetExpansionId = (valueSetExpansionEntityId);
        }
        // parentId
        if (parentId != null && !parentId.isEmpty()) {
            entity.parentContainId = (parentId);
        }     
        // active
        entity.active = (true);
        // version
        /* entity.version = (version); */
        // ResCreated
        entity.resCreated = (new Date());
        return entity;
    }

    private void removeValueSetEntity(ValueSetEntity entity) {
        mongo.save(entity);
        ObjectId valueSetEntityId = entity.id;
        if (valueSetEntityId != null) {
            ValueSetComposeEntity compose = new ValueSetComposeEntity();
            ValueSetExpansionEntity expansion = new ValueSetExpansionEntity();
            expansion = removeExpansion(valueSetEntityId.toString());
            compose = removeCompose(valueSetEntityId.toString());
            entity.compose = (compose);
            entity.expansion = (expansion);
        }
    }

    private ValueSetExpansionEntity removeExpansion(String valueSetId) {
        ValueSetExpansionEntity expansion = new ValueSetExpansionEntity();
        Query query = new Query();
        query = Query.query(
                Criteria.where(ConstantKeys.SP_VALUE_SET_ID).is(valueSetId).and(ConstantKeys.SP_ACTIVE).is(true));
        expansion = mongo.findOne(query, ValueSetExpansionEntity.class);
        if (expansion != null && expansion.id != null) {
            // remove
            expansion.active = (false);
            expansion.resDeleted = (new Date());
            mongo.save(expansion);

            ObjectId valueSetExpansionId = expansion.id;
            List<ValueSetContainEntity> containss = new ArrayList<>();
            containss = removeContainss(valueSetExpansionId.toString(), null);
            if (containss != null) {
                expansion.contains = (containss);
            }
        }
        return expansion;
    }

    private List<ValueSetContainEntity> removeContainss(String valueSetExpansionId, String parentContainId) {
        List<ValueSetContainEntity> containsParents = new ArrayList<>();
        Query queryParent = null;
        if (parentContainId != null && !parentContainId.isEmpty()) {
            queryParent = Query.query(Criteria.where(ConstantKeys.SP_VALUE_SET_EXPANSION_ID).is(valueSetExpansionId)
                    .and(ConstantKeys.SP_ACTIVE).is(true).and(ConstantKeys.SP_PARENT_CONTAIN_ID).is(parentContainId));
        } else {
            queryParent = Query.query(Criteria.where(ConstantKeys.SP_VALUE_SET_EXPANSION_ID).is(valueSetExpansionId)
                    .and(ConstantKeys.SP_ACTIVE).is(true).and(ConstantKeys.SP_PARENT_CONTAIN_ID).is(""));

        }

        containsParents = mongo.find(queryParent, ValueSetContainEntity.class);
        if (containsParents != null) {
            for (ValueSetContainEntity item : containsParents) {
                // remove
                item.active = (false);
                item.resDeleted = (new Date());
                mongo.save(item);
                List<ValueSetContainEntity> containss = new ArrayList<>();
                containss = removeContainss(valueSetExpansionId, item.id.toString());
                if (containss != null) {
                    item.contains = (containss);
                }
            }
        }
        return containsParents;
    }

    private ValueSetComposeEntity removeCompose(String valueSetId) {
        ValueSetComposeEntity compose = new ValueSetComposeEntity();
        Query query = new Query();
        query = Query.query(
                Criteria.where(ConstantKeys.SP_VALUE_SET_ID).is(valueSetId).and(ConstantKeys.SP_ACTIVE).is(true));
        compose = mongo.findOne(query, ValueSetComposeEntity.class);
        if (compose != null && compose.id != null) {
            // remove
            compose.active = (false);
            compose.resDeleted = (new Date());
            mongo.save(compose);
            ObjectId valueSetComposeId = compose.id;
            // include - exclude
            List<ConceptSetEntity> includes = removeIncludeAndExclude(valueSetComposeId.toString());
            compose.include = (includes);
        }
        return compose;
    }

    private List<ConceptSetEntity> removeIncludeAndExclude(String valueSetComposeId) {
        List<ConceptSetEntity> includes = new ArrayList<>();
        Query query = new Query();
        query = Query.query(Criteria.where(ConstantKeys.SP_VALUE_SET_COMPOSE_ID).is(valueSetComposeId)
                .and(ConstantKeys.SP_ACTIVE).is(true));
        includes = mongo.find(query, ConceptSetEntity.class);
        if (includes != null) {
            for (ConceptSetEntity item : includes) {
                // remove
                item.active = (false);
                item.resDeleted = (new Date());
                mongo.save(item);
                List<ConceptEntity> concepts = new ArrayList<>();
                concepts = removeConcepts(item.id.toString(), null);
                if (concepts != null) {
                    item.concept = new ArrayList<>();
                    concepts.forEach(x -> item.concept.add(new ConceptReferenceEntity(x.code, x.display)));
                }
            }
        }
        return includes;
    }

    private List<ConceptEntity> removeConcepts(String codeSystemId, String parentConceptId) {
        List<ConceptEntity> conceptParents = new ArrayList<>();
        Criteria criteria = new Criteria();
        if (codeSystemId != null && !codeSystemId.isEmpty()) {
            criteria.and(ConstantKeys.SP_CODE_SYSTEM_ID).is(codeSystemId).and(ConstantKeys.SP_ACTIVE).is(true);
            if (parentConceptId != null && !parentConceptId.isEmpty()) {
                criteria.and(ConstantKeys.SP_PARENT_CONCEPT_ID).is(parentConceptId);
            } else {
                criteria.orOperator(new Criteria(ConstantKeys.SP_PARENT_CONCEPT_ID).is(null),
                        new Criteria(ConstantKeys.SP_PARENT_CONCEPT_ID).is(""));
            }
        }
        Query query = new Query(criteria);
        if (query != null) {
            conceptParents = mongo.find(query, ConceptEntity.class);
        }
        if (conceptParents != null) {
            for (ConceptEntity item : conceptParents) {
                // remove concept
                item.active = (false);
                item.resDeleted = (new Date());
                mongo.save(item);
                List<ConceptEntity> concepts = new ArrayList<>();
                concepts = removeConcepts(codeSystemId, item.id.toString());
                if (concepts != null) {
                    item.concept = (concepts);
                }
            }
        }
        return conceptParents;
    }
}
