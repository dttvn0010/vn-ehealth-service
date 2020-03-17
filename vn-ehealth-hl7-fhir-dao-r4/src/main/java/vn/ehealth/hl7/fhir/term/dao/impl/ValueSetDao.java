package vn.ehealth.hl7.fhir.term.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ValueSet;
import org.hl7.fhir.r4.model.ValueSet.ConceptReferenceDesignationComponent;
import org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
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
public class ValueSetDao extends BaseDao<ValueSetEntity, ValueSet> {
 
    @SuppressWarnings("deprecation")
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
                                resources.add(transform(valueSetEntity));
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
                                resources.add(transform(valueSetEntity));
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
                                resources.add(transform(valueSetEntity));
                            }
                        }
                    }

                } else {
                    for (ValueSetEntity valueSetEntity : results) {
                        resources.add(transform(valueSetEntity));
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
                            resources.add(transform(valueSetEntity));
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
                            resources.add(transform(valueSetEntity));
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
                            resources.add(transform(valueSetEntity));
                        }
                    }
                }

            } else {
                for (ValueSetEntity valueSetEntity : results) {
                    resources.add(transform(valueSetEntity));
                }
            }
        }
        return resources.size();
    }

 
    @Override
    protected String getProfile() {
        return "CarePlan-v1.0";
    }

    @Override
    protected ValueSetEntity fromFhir(ValueSet obj) {
        return ValueSetEntity.fromValueSet(obj);
    }

    @Override
    protected ValueSet toFhir(ValueSetEntity ent) {
        return ValueSetEntity.toValueSet(ent);
    }

    @Override
    protected Class<? extends BaseResource> getEntityClass() {
        return ValueSetEntity.class;
    }
}
