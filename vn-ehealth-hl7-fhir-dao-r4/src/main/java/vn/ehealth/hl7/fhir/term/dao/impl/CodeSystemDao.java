package vn.ehealth.hl7.fhir.term.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
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
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.term.dao.ICodeSystem;
import vn.ehealth.hl7.fhir.term.dao.transform.CodeSystemEntityToFHIRCodeSystem;
import vn.ehealth.hl7.fhir.term.entity.CodeSystemEntity;
import vn.ehealth.hl7.fhir.term.entity.ConceptDesignationEntity;
import vn.ehealth.hl7.fhir.term.entity.ConceptEntity;
import vn.ehealth.hl7.fhir.term.entity.ConceptPropertyEntity;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Repository
public class CodeSystemDao implements ICodeSystem {
    @Autowired
    MongoOperations mongo;

    @Autowired
    CodeSystemEntityToFHIRCodeSystem codeSystemEntityToFHIRCodeSystem;

    @Override
    public CodeSystem create(FhirContext fhirContext, CodeSystem object) {
        CodeSystemEntity entity = new CodeSystemEntity();
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            // save CodeSystemEntity
            entity = saveCodeSystemEntity(object, version, entity, null);
            return codeSystemEntityToFHIRCodeSystem.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "codeSystem", key = "#idType")
    public CodeSystem update(FhirContext fhirContext, CodeSystem object, IdType idType) {
        CodeSystemEntity entityOld = null;
        String fhirId = "";
        if (idType != null) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, CodeSystemEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove CodeSystemEntity old
            removeCodeSystemEntity(entityOld);
            // save CodeSystemEntity
            int version = entityOld.version + 1;
            if (object != null) {
                CodeSystemEntity entity = new CodeSystemEntity();
                entity = saveCodeSystemEntity(object, version, entity, fhirId);
                return codeSystemEntityToFHIRCodeSystem.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "codeSystem", key = "#idType")
    public CodeSystem read(FhirContext fhirContext, IdType idType) {
        if (idType != null) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            CodeSystemEntity entity = mongo.findOne(query, CodeSystemEntity.class);
            if (entity != null) {
                ObjectId codeSystemId = entity.id;
                if (codeSystemId != null && !codeSystemId.toString().isEmpty()) {
                    List<ConceptEntity> concepts = new ArrayList<>();
                    concepts = readConcepts(codeSystemId.toString(), null);
                    if (concepts != null) {
                        entity.concept = (concepts);
                    }
                }
                return codeSystemEntityToFHIRCodeSystem.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "codeSystem", key = "#idType")
    public CodeSystem remove(FhirContext fhirContext, IdType idType) {
        if (idType != null) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            CodeSystemEntity entity = mongo.findOne(query, CodeSystemEntity.class);
            if (entity != null) {
                // remove CodeSystemEntity old
                removeCodeSystemEntity(entity);
                return codeSystemEntityToFHIRCodeSystem.transform(entity);
            }
        }
        return null;
    }
    
    @Override
    public CodeSystem readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query
                        .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                CodeSystemEntity entity = mongo.findOne(query, CodeSystemEntity.class);
                if (entity != null) {
                    ObjectId codeSystemId = entity.id;
                    if (codeSystemId != null && !codeSystemId.toString().isEmpty()) {
                        List<ConceptEntity> concepts = new ArrayList<>();
                        concepts = readConcepts(codeSystemId.toString(), null, version);
                        if (concepts != null) {
                            entity.concept = (concepts);
                        }
                    }
                    return codeSystemEntityToFHIRCodeSystem.transform(entity);
                }
            }
        }
        return null;
    }
    
    
    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, DateRangeParam date, TokenParam identifier,
            StringParam name, TokenParam code, TokenParam contentMode, StringParam description, TokenParam jurisdiction,
            TokenParam language, StringParam publisher, TokenParam status, UriParam system, StringParam title,
            UriParam url, TokenParam version, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
            String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = null;
        criteria = Criteria.where("active").is(true);
        // default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, null, _lastUpdated, _tag, _profile, _security,
                identifier);
        // custom
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "date", date);
        }
        if (name != null) {
            criteria.and("name").regex(name.getValue());
        }
        if (contentMode != null) {
            criteria.and("content").regex(contentMode.getValue());
        }
        if (description != null) {
            criteria.and("description").is(description.getValue());
        }
        if (jurisdiction != null) {
            criteria.and("jurisdiction").regex(jurisdiction.getValue());
        }
        if (publisher != null) {
            criteria.and("publisher").regex(publisher.getValue());
        }
        if (status != null) {
            criteria.and("status").regex(status.getValue());
        }
        if (system != null) {
            criteria.and("system").regex(system.getValue());
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
            List<CodeSystemEntity> results = mongo.find(qry, CodeSystemEntity.class);
            if (code != null || language != null) {
                for (CodeSystemEntity codeSystemEntity : results) {
                    Criteria criteria1 = null;
                    criteria1 = Criteria.where("codeSystemId").is(codeSystemEntity.id.toString());
                    if (code != null) {
                        criteria1.and("code").regex(code.getValue());
                    }
                    if (language != null) {
                        criteria1.and("designation.language").regex(language.getValue());
                    }
                    Query qry1 = Query.query(criteria1);
                    List<ConceptEntity> conceptresults = mongo.find(qry1, ConceptEntity.class);
                    if (conceptresults != null && conceptresults.size() > 0) {
                        CodeSystem codeSystem = codeSystemEntityToFHIRCodeSystem.transform(codeSystemEntity);
                        resources.add(codeSystem);
                    }
                }
            } else {
                for (CodeSystemEntity codeSystemEntity : results) {
                    CodeSystem codeSystem = codeSystemEntityToFHIRCodeSystem.transform(codeSystemEntity);
                    resources.add(codeSystem);
                }
            }
        }
        return resources;
    }

    public List<ConceptEntity> checkHasChild(String codeSystemId, List<ConceptDefinitionComponent> concepts,
            String parentId) {
//        if(!StringUtil.isNullOrEmpty(codeSystemId)) {
//            Query results = Query.query(Criteria.where("codeSystemId").regex(codeSystemId));
//            mongo.remove(results, ConceptEntity.class);
//        }
        List<ConceptEntity> conceptEntitys = new ArrayList<>();
        for (ConceptDefinitionComponent concept : concepts) {
            ConceptEntity conceptEntity = new ConceptEntity();
            conceptEntity.codeSystemId = (codeSystemId);
            conceptEntity.code = (concept.getCode());
            conceptEntity.display = (concept.getDisplay());
            conceptEntity.definition = (concept.getDefinition());
            conceptEntity.active = (true);
            conceptEntity.parentConceptId = (parentId);
            // designation
            if (concept.hasDesignation()) {
                List<ConceptDesignationEntity> conceptDesignationEntitys = new ArrayList<>();
                for (ConceptDefinitionDesignationComponent conceptDefinitionDesignationComponent : concept
                        .getDesignation()) {
                    ConceptDesignationEntity conceptDesignationEntity = new ConceptDesignationEntity();
                    conceptDesignationEntity.language = (conceptDefinitionDesignationComponent.getLanguage());
                    conceptDesignationEntity.use = (conceptDefinitionDesignationComponent.getUse());
                    conceptDesignationEntity.value = (conceptDefinitionDesignationComponent.getValue());
                    conceptDesignationEntitys.add(conceptDesignationEntity);
                }
                conceptEntity.designation = (conceptDesignationEntitys);
            }
            // property
            if (concept.hasProperty()) {
                List<ConceptPropertyEntity> conceptPropertyEntitys = new ArrayList<>();
                for (ConceptPropertyComponent conceptPropertyComponent : concept.getProperty()) {
                    ConceptPropertyEntity conceptPropertyEntity = new ConceptPropertyEntity();
                    conceptPropertyEntity.code = (conceptPropertyComponent.getCode());
                    conceptPropertyEntity.value = (conceptPropertyComponent.getValue());
                    conceptPropertyEntitys.add(conceptPropertyEntity);
                }
                conceptEntity.property = (conceptPropertyEntitys);
            }
            mongo.save(conceptEntity);
            if (concept.hasConcept()) {
                List<ConceptEntity> conceptEntitytmps = new ArrayList<>();
                conceptEntitytmps = checkHasChild(codeSystemId, concept.getConcept(), conceptEntity.id.toString());
                conceptEntity.concept = (conceptEntitytmps);
            }
            conceptEntitys.add(conceptEntity);
        }
        return conceptEntitys;
    }

    public List<ConceptEntity> checkCode_Lange(List<ConceptEntity> concepts, String codeSystemId, TokenParam code,
            TokenParam language) {
        List<ConceptEntity> conceptmps = new ArrayList<>();
        if (concepts.size() > 0) {
            for (ConceptEntity conceptEntity : concepts) {
                Criteria criteria2 = null;
                criteria2 = Criteria.where("codeSystemId").regex(codeSystemId);
                criteria2.and("parentConceptId").regex(conceptEntity.id.toString());
                Query qry2 = Query.query(criteria2);
                List<ConceptEntity> conceptresultmps = mongo.find(qry2, ConceptEntity.class);
                if (conceptresultmps.size() > 0) {
                    concepts(conceptresultmps, codeSystemId);
                    conceptEntity.concept = (conceptresultmps);
                }
            }
            conceptmps.addAll(concepts);
        }
        return conceptmps;
    }

    public List<ConceptEntity> concepts(List<ConceptEntity> concepts, String codeSystemId) {
        List<ConceptEntity> conceptmps = new ArrayList<>();
        if (concepts.size() > 0) {
            for (ConceptEntity conceptEntity : concepts) {
                Criteria criteria2 = null;
                criteria2 = Criteria.where("codeSystemId").regex(codeSystemId);
                criteria2.and("parentConceptId").regex(conceptEntity.id.toString());
                Query qry2 = Query.query(criteria2);
                List<ConceptEntity> conceptresultmps = mongo.find(qry2, ConceptEntity.class);
                if (conceptresultmps.size() > 0) {
                    concepts(conceptresultmps, codeSystemId);
                    conceptEntity.concept = (conceptresultmps);
                }
            }
            conceptmps.addAll(concepts);
        }
        return conceptmps;
    }

    @Override
    public Parameters getLookupParams(TokenParam code, UriParam system, StringParam version, Coding coding,
            DateRangeParam date, TokenParam displayLanguage, TokenParam property, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content, StringParam _page) {
        Parameters retVal = new Parameters();
        Criteria criteria = null;
        criteria = Criteria.where("active").is(true);
        // default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                null);
        // custom
        if (coding != null) {// post
            criteria.and("url").is(coding.getSystem());
            Query qry = Query.query(criteria);
            List<CodeSystemEntity> results = mongo.find(qry, CodeSystemEntity.class);
            for (CodeSystemEntity codeSystemEntity : results) {
                Criteria criteria1 = null;
                criteria1 = Criteria.where("codeSystemId").is(codeSystemEntity.id.toString())
                        .and("parentConceptId").is("");
                if (code != null) {
                    criteria1.and("code").regex(code.getValue());
                }
                Query qry1 = Query.query(criteria1);
                List<ConceptEntity> conceptresults = mongo.find(qry1, ConceptEntity.class);
                conceptresults = concepts(conceptresults, codeSystemEntity.id.toString());
                for (ConceptEntity conceptEntity : conceptresults) {
                    retVal.addParameter().setName("name").setValue(new StringType(conceptEntity.code));
                    retVal.addParameter().setName("version").setValue(new IntegerType(conceptEntity.version));
                    retVal.addParameter().setName("display").setValue(new StringType(conceptEntity.display));
                    List<ConceptDesignationEntity> designations = conceptEntity.designation;
                    if (designations != null && designations.size() > 0) {
                        retVal.addParameter().setName("designation");
                        for (ConceptDesignationEntity conceptDesignationEntity : designations) {
                            retVal.addParameter().addPart().setName("value")
                                    .setValue(new StringType(conceptDesignationEntity.value));
                            retVal.addParameter().addPart().setName("use").setValue(conceptDesignationEntity.use);
                            retVal.addParameter().addPart().setName("language")
                                .setValue(new StringType(conceptDesignationEntity.language));
                        }
                    }
                    List<ConceptPropertyEntity> properties = conceptEntity.property;
                    if (properties != null && properties.size() > 0) {
                        retVal.addParameter().setName("property");
                        for (ConceptPropertyEntity conceptPropertyEntity : properties) {
                            retVal.addParameter().addPart().setName("code")
                                    .setValue(new StringType(conceptPropertyEntity.code));
                            retVal.addParameter().addPart().setName("value").setValue(conceptPropertyEntity.value);
                        }

                    }
                }
            }
        } else {// get
            if (system != null) {
                criteria.and("url").is(system.getValue());
            }
            Query qry = Query.query(criteria);
            List<CodeSystemEntity> results = mongo.find(qry, CodeSystemEntity.class);
            for (CodeSystemEntity codeSystemEntity : results) {
                Criteria criteria1 = null;
                criteria1 = Criteria.where("codeSystemId").is(codeSystemEntity.id.toString())
                        .and("parentConceptId").is("");
                if (code != null) {
                    criteria1.and("code").regex(code.getValue());
                }
                if (version != null) {
                    criteria.and("version").regex(version.getValue());
                }
                Query qry1 = Query.query(criteria1);
                List<ConceptEntity> conceptresults = mongo.find(qry1, ConceptEntity.class);
                conceptresults = concepts(conceptresults, codeSystemEntity.id.toString());
                for (ConceptEntity conceptEntity : conceptresults) {
                    retVal.addParameter().setName("name").setValue(new StringType(conceptEntity.code));
                    retVal.addParameter().setName("version").setValue(new IntegerType(conceptEntity.version));
                    retVal.addParameter().setName("display").setValue(new StringType(conceptEntity.display));
                    List<ConceptDesignationEntity> designations = conceptEntity.designation;
                    if (designations != null && designations.size() > 0) {
                        retVal.addParameter().setName("designation");
                        for (ConceptDesignationEntity conceptDesignationEntity : designations) {
                            retVal.addParameter().addPart().setName("value")
                                    .setValue(new StringType(conceptDesignationEntity.value));
                            retVal.addParameter().addPart().setName("use").setValue(conceptDesignationEntity.use);
                            retVal.addParameter().addPart().setName("language")
                                    .setValue(new StringType(conceptDesignationEntity.language));
                        }
                    }
                    List<ConceptPropertyEntity> properties = conceptEntity.property;
                    if (properties != null && properties.size() > 0) {
                        retVal.addParameter().setName("property");
                        for (ConceptPropertyEntity conceptPropertyEntity : properties) {
                            retVal.addParameter().addPart().setName("code")
                                .setValue(new StringType(conceptPropertyEntity.code));
                            retVal.addParameter().addPart().setName("value").setValue(conceptPropertyEntity.value);
                        }

                    }
                }
            }
        }

        return retVal;
    }

    @Override
    public long findMatchesAdvancedTotal(FhirContext ctx, DateRangeParam date, TokenParam identifier, StringParam name,
            TokenParam code, TokenParam contentMode, StringParam description, TokenParam jurisdiction,
            TokenParam language, StringParam publisher, TokenParam status, UriParam system, StringParam title,
            UriParam url, TokenParam version, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = null;
        criteria = Criteria.where("active").is(true);
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "date", date);
        }
        // default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        // custom
        if (name != null) {
            criteria.and("name").regex(name.getValue());
        }
        if (contentMode != null) {
            criteria.and("content").regex(contentMode.getValue());
        }
        if (description != null) {
            criteria.and("description").is(description.getValue());
        }
        if (jurisdiction != null) {
            criteria.and("jurisdiction").regex(jurisdiction.getValue());
        }
        if (publisher != null) {
            criteria.and("publisher").regex(publisher.getValue());
        }
        if (status != null) {
            criteria.and("status").regex(status.getValue());
        }
        if (system != null) {
            criteria.and("system").regex(system.getValue());
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
            List<CodeSystemEntity> results = mongo.find(qry, CodeSystemEntity.class);
            if (code != null || language != null) {
                for (CodeSystemEntity codeSystemEntity : results) {
                    Criteria criteria1 = null;
                    criteria1 = Criteria.where("codeSystemId").is(codeSystemEntity.id.toString());
                    if (code != null) {
                        criteria1.and("code").regex(code.getValue());
                    }
                    if (language != null) {
                        criteria1.and("designation.language").regex(language.getValue());
                    }
                    Query qry1 = Query.query(criteria1);
                    List<ConceptEntity> conceptresults = mongo.find(qry1, ConceptEntity.class);
                    if (conceptresults != null && conceptresults.size() > 0) {
                        CodeSystem codeSystem = codeSystemEntityToFHIRCodeSystem.transform(codeSystemEntity);
                        resources.add(codeSystem);
                    }
                }
            } else {
                for (CodeSystemEntity codeSystemEntity : results) {
                    CodeSystem codeSystem = codeSystemEntityToFHIRCodeSystem.transform(codeSystemEntity);
                    resources.add(codeSystem);
                }
            }
        }
        return resources.size();
    }

    private List<ConceptEntity> readConcepts(String codeSystemId, String parentConceptId) {
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
                List<ConceptEntity> concepts = new ArrayList<>();
                concepts = readConcepts(codeSystemId, item.id.toString());
                if (concepts != null) {
                    item.concept = (concepts);
                }
            }
        }
        return conceptParents;
    }
    
    private List<ConceptEntity> readConcepts(String codeSystemId, String parentConceptId, int version) {
        List<ConceptEntity> conceptParents = new ArrayList<>();
        Criteria criteria = new Criteria();
        if (codeSystemId != null && !codeSystemId.isEmpty()) {
            criteria.and(ConstantKeys.SP_CODE_SYSTEM_ID).is(codeSystemId).and(ConstantKeys.SP_VERSION).is(version);
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
                concepts = readConcepts(codeSystemId, item.id.toString());
                if (concepts != null) {
                    item.concept = (concepts);
                }
            }
        }
        return conceptParents;
    }

    private CodeSystemEntity saveCodeSystemEntity(CodeSystem object, int version, CodeSystemEntity entity,
            String fhirId) {
        entity = createNewCodeSystemEntity(object, version, fhirId);
        // save CodeSystemEntity database
        mongo.save(entity);
        ObjectId codeSystemId = entity.id;
        if (codeSystemId != null && object.hasConcept()) {
            // save concept
            List<ConceptEntity> concepts = new ArrayList<>();
            concepts = saveConcepts(object.getConcept(), version, codeSystemId.toString(), null, fhirId);
            entity.concept = (concepts);
        }
        return entity;
    }

    private List<ConceptEntity> saveConcepts(List<ConceptDefinitionComponent> objectConcepts, int version,
            String codeSystemId, String parentId, String fhirId) {
        List<ConceptEntity> conceptParents = new ArrayList<>();
        for (ConceptDefinitionComponent item : objectConcepts) {
            ConceptEntity conceptParent = createNewConceptEntity(item, codeSystemId, parentId, version);
            if (fhirId != null && !fhirId.isEmpty()) {
                conceptParent.resUpdated = (new Date());
            }
            mongo.save(conceptParent);
            if (item.hasConcept() && conceptParent.id != null && !conceptParent.id.toString().isEmpty()) {
                List<ConceptEntity> concepts = new ArrayList<>();
                concepts = saveConcepts(item.getConcept(), version, codeSystemId, conceptParent.id.toString(),
                        fhirId);
                if (concepts != null) {
                    conceptParent.concept = (concepts);
                }
            }
            conceptParents.add(conceptParent);

        }
        return conceptParents;
    }

    private void removeCodeSystemEntity(CodeSystemEntity entity) {
        // remove CodeSystemEntity old
        entity.resDeleted = (new Date());
        entity.active = (false);
        mongo.save(entity);
        ObjectId codeSystemId = entity.id;
        if (codeSystemId != null) {
            // remove concept by codeSystemId
            List<ConceptEntity> conceptCodeSystems = new ArrayList<>();
            Query queryConceptCodeSystem = Query.query(Criteria.where(ConstantKeys.SP_CODE_SYSTEM_ID)
                    .is(codeSystemId.toString()).and(ConstantKeys.SP_ACTIVE).is(true));
            conceptCodeSystems = mongo.find(queryConceptCodeSystem, ConceptEntity.class);
            if (conceptCodeSystems != null) {
                for (ConceptEntity item : conceptCodeSystems) {
                    item.resDeleted = (new Date());
                    item.active = (false);
                    mongo.save(item);
                }
            }

        }
    }

    private CodeSystemEntity createNewCodeSystemEntity(CodeSystem obj, int version, String fhirId) {
        var ent = CodeSystemEntity.fromCodeSystem(obj);
        DataConvertUtil.setMetaExt(obj, ent);
        if (fhirId != null && !fhirId.isEmpty()) {
            ent.fhir_id = (fhirId);
        } else {
            ent.fhir_id = (StringUtil.generateUID());
        }
        
        ent.active = (true);
        ent.version = (version);
        ent.resCreated = (new Date());
        return ent;
    }


    private ConceptEntity createNewConceptEntity(ConceptDefinitionComponent object, String codeSystemId,
            String parentId, int version) {
        var entity = ConceptEntity.fromConceptDefinitionComponent(object);
        // codeSystemId
        if (codeSystemId != null && !codeSystemId.isEmpty()) {
            entity.codeSystemId = (codeSystemId);
        }
        // parentConceptId
        if (parentId != null && !parentId.isEmpty()) {
            entity.parentConceptId = (parentId);
        }
        // active
        entity.active = (true);
        // version
        entity.version = (version);
        // ResCreated
        entity.resCreated = (new Date());
        return entity;
    }
}
