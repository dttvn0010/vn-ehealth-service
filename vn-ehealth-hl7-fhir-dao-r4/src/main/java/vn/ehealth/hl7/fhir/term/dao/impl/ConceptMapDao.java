package vn.ehealth.hl7.fhir.term.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ConceptMap;
import org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent;
import org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent;
import org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
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
import vn.ehealth.hl7.fhir.term.dao.IConceptMap;
import vn.ehealth.hl7.fhir.term.dao.transform.ConceptMapEntityToFHIRConceptMap;
import vn.ehealth.hl7.fhir.term.entity.ConceptMapEntity;
import vn.ehealth.hl7.fhir.term.entity.DependOnEntity;
import vn.ehealth.hl7.fhir.term.entity.ElementEntity;
import vn.ehealth.hl7.fhir.term.entity.GroupElementEntity;
import vn.ehealth.hl7.fhir.term.entity.TargetElementEntity;
import vn.ehealth.hl7.fhir.term.entity.UnMappedEntity;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Repository
public class ConceptMapDao implements IConceptMap {
    @Autowired
    MongoOperations mongo;

    @Autowired
    ConceptMapEntityToFHIRConceptMap conceptMapEntityToFHIRConceptMap;

    @Override
    public ConceptMap create(FhirContext fhirContext, ConceptMap object) {
        ConceptMapEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            // save ConceptMapEntity
            entity = saveConceptMapEntity(object, version, entity, null);
            return conceptMapEntityToFHIRConceptMap.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "conceptMap", key = "#idType")
    public ConceptMap update(FhirContext fhirContext, ConceptMap object, IdType idType) {
        ConceptMapEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, ConceptMapEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove ConceptMapEntity old
            removeConceptMapEntity(entityOld);
            // save ConceptMapEntity
            int version = entityOld.version + 1;
            if (object != null) {
                ConceptMapEntity entity = new ConceptMapEntity();
                entity.resUpdated = (new Date());
                entity = saveConceptMapEntity(object, version, entity, fhirId);
                return conceptMapEntityToFHIRConceptMap.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "conceptMap", key = "#idType")
    public ConceptMap read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ConceptMapEntity entity = mongo.findOne(query, ConceptMapEntity.class);
            if (entity != null) {
                ObjectId conceptMapEntityId = entity.id;
                if (conceptMapEntityId != null) {
                    List<GroupElementEntity> groups = readGroupElementEntity(conceptMapEntityId.toString(), null);
                    entity.group = (groups);
                }
                return conceptMapEntityToFHIRConceptMap.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "conceptMap", key = "#idType")
    public ConceptMap remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ConceptMapEntity entity = mongo.findOne(query, ConceptMapEntity.class);
            if (entity != null) {
                // remove ConceptMapEntity old
                removeConceptMapEntity(entity);
                return conceptMapEntityToFHIRConceptMap.transform(entity);
            }
        }
        return null;
    }

    @Override
    public ConceptMap readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                ConceptMapEntity entity = mongo.findOne(query, ConceptMapEntity.class);
                if (entity != null) {
                    ObjectId conceptMapEntityId = entity.id;
                    if (conceptMapEntityId != null) {
                        List<GroupElementEntity> groups = readGroupElementEntity(conceptMapEntityId.toString(), version);
                        entity.group = (groups);
                    }
                    return conceptMapEntityToFHIRConceptMap.transform(entity);
                }
            }
        }
        return null;
    }

    private ConceptMapEntity removeConceptMapEntity(ConceptMapEntity entity) {
        // remove CodeSystemEntity old
        entity.resDeleted = (new Date());
        entity.active = (false);
        mongo.save(entity);
        // remove and set group to concept - start
        ObjectId conceptMapEntityId = entity.id;
        if (conceptMapEntityId != null) {
            List<GroupElementEntity> groups = removeGroupElementEntity(conceptMapEntityId.toString());
            entity.group = (groups);
        }
        // remove and set group to concept - end
        return entity;
    }

    private List<GroupElementEntity> removeGroupElementEntity(String conceptMapEntityId) {
        List<GroupElementEntity> groups = new ArrayList<>();
        Query queryGroup = Query.query(Criteria.where(ConstantKeys.SP_CONCEPT_MAP_ID).is(conceptMapEntityId.toString())
                .and(ConstantKeys.SP_ACTIVE).is(true));
        groups = mongo.find(queryGroup, GroupElementEntity.class);
        if (groups != null) {
            for (GroupElementEntity itemGroup : groups) {
                itemGroup.resDeleted = (new Date());
                itemGroup.active = (false);
                mongo.save(itemGroup);
                // remove and set element to group - start
                ObjectId groupElementId = itemGroup.id;
                List<ElementEntity> elements = removeElementEntity(groupElementId.toString());
                itemGroup.element = (elements);
                // remove and set element to group - end
            }
        }
        return groups;
    }

    private List<ElementEntity> removeElementEntity(String groupElementId) {
        List<ElementEntity> elements = new ArrayList<>();
        Query queryElement = Query.query(Criteria.where(ConstantKeys.SP_GROUP_ELEMENT_ID).is(groupElementId.toString())
                .and(ConstantKeys.SP_ACTIVE).is(true));
        elements = mongo.find(queryElement, ElementEntity.class);
        if (elements != null) {
            for (ElementEntity itemElement : elements) {
                itemElement.resDeleted = (new Date());
                itemElement.active = (false);
                mongo.save(itemElement);
                // save and set target to element - start
                ObjectId elementEntityId = itemElement.id;
                List<TargetElementEntity> targets = new ArrayList<>();
                if (elementEntityId != null) {
                    Query queryTarget = Query.query(Criteria.where(ConstantKeys.SP_ELEMENT_ENTITY_ID)
                            .is(elementEntityId.toString()).and(ConstantKeys.SP_ACTIVE).is(true));
                    targets = mongo.find(queryTarget, TargetElementEntity.class);
                    if (targets != null) {
                        for (TargetElementEntity itemTarget : targets) {
                            itemTarget.resDeleted = (new Date());
                            itemTarget.active = (false);
                            mongo.save(itemTarget);
                        }
                    }

                    itemElement.target = (targets);
                }
                // save and set target to element - end
            }
        }
        return elements;
    }

    private List<GroupElementEntity> readGroupElementEntity(String conceptMapEntityId, Integer version) {
        List<GroupElementEntity> groups = new ArrayList<>();
        Query queryGroup = null;
        if (version != null) {
            queryGroup = Query.query(Criteria.where(ConstantKeys.SP_CONCEPT_MAP_ID).is(conceptMapEntityId.toString())
                    .and(ConstantKeys.SP_VERSION).is(version));
        }else {
            queryGroup = Query.query(Criteria.where(ConstantKeys.SP_CONCEPT_MAP_ID).is(conceptMapEntityId.toString())
                    .and(ConstantKeys.SP_ACTIVE).is(true));
        }
        
        groups = mongo.find(queryGroup, GroupElementEntity.class);
        if (groups != null) {
            for (GroupElementEntity itemGroup : groups) {
                ObjectId groupElementId = itemGroup.id;
                List<ElementEntity> elements = readElementEntity(groupElementId.toString(), version);
                itemGroup.element = (elements);
            }
        }
        return groups;
    }

    private List<ElementEntity> readElementEntity(String groupElementId, Integer version) {
        List<ElementEntity> elements = new ArrayList<>();
        Query queryElement = null;
        if (version != null) {
            queryElement = Query.query(Criteria.where(ConstantKeys.SP_GROUP_ELEMENT_ID).is(groupElementId.toString())
                    .and(ConstantKeys.SP_VERSION).is(version));
        }else {
            queryElement = Query.query(Criteria.where(ConstantKeys.SP_GROUP_ELEMENT_ID).is(groupElementId.toString())
                    .and(ConstantKeys.SP_ACTIVE).is(true));
        }
        elements = mongo.find(queryElement, ElementEntity.class);
        if (elements != null) {
            for (ElementEntity itemElement : elements) {
                ObjectId elementEntityId = itemElement.id;
                List<TargetElementEntity> targets = new ArrayList<>();
                if (elementEntityId != null) {
                    Query queryTarget = null;
                    if (version != null) {
                        queryTarget = Query.query(Criteria.where(ConstantKeys.SP_ELEMENT_ENTITY_ID)
                                .is(elementEntityId.toString()).and(ConstantKeys.SP_VERSION).is(version));
                    }else {
                        queryTarget = Query.query(Criteria.where(ConstantKeys.SP_ELEMENT_ENTITY_ID)
                                .is(elementEntityId.toString()).and(ConstantKeys.SP_ACTIVE).is(true));
                    }
                    
                    targets = mongo.find(queryTarget, TargetElementEntity.class);
                    if (targets != null) {
                        itemElement.target = (targets);
                    }
                }
            }
        }
        return elements;
    }

    @Override
    public List<Resource> search(FhirContext ctx, TokenParam active, DateRangeParam date, UriParam dependson,
            StringParam description, TokenParam identifier, TokenParam jurisdiction, StringParam name, UriParam other,
            UriParam product, StringParam publisher, TokenParam code, UriParam source, TokenParam status,
            UriParam target, StringParam title, UriParam url, TokenParam version, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, date, dependson, description, identifier, jurisdiction, name,
                other, product, publisher, code, source, status, target, title, url, version, resid, _lastUpdated, _tag,
                _profile, _query, _security, _content);

        if (criteria != null) {
            Query qry = Query.query(criteria);
            Pageable pageableRequest;
            pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                    count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
            qry.with(pageableRequest);
            if (sortParam != null && !sortParam.equals("")) {
                qry.with(new Sort(Sort.Direction.ASC, sortParam));
            }
            List<ConceptMapEntity> results = mongo.find(qry, ConceptMapEntity.class);

            for (ConceptMapEntity conceptMapEntity : results) {
                Criteria criteria1 = null;
                criteria1 = Criteria.where("conceptMapID").is(conceptMapEntity.id.toString());
                if (source != null) {
                    criteria1.and("source").regex(source.getValue());
                }
                if (target != null) {
                    criteria1.and("target").regex(target.getValue());
                }
                Query qry1 = Query.query(criteria1);
                List<GroupElementEntity> groups = mongo.find(qry1, GroupElementEntity.class);
                List<GroupElementEntity> groupTmps = new ArrayList<>();
                if (groups != null && groups.size() > 0) {
                    for (GroupElementEntity groupElementEntity : groups) {
                        List<ElementEntity> elements = getElements(groupElementEntity.id.toString());
                        groupElementEntity.element = (elements);
                        UnMappedEntity unMapped = getUnMappeds(groupElementEntity.id.toString());
                        groupElementEntity.unmapped = (unMapped);
                        groupTmps.add(groupElementEntity);
                    }
                    conceptMapEntity.group = (groupTmps);
                    ConceptMap conceptMap = conceptMapEntityToFHIRConceptMap.transform(conceptMapEntity);
                    resources.add(conceptMap);
                }

            }
        }
        return resources;
    }

    @Override
    public Parameters getTranslateParams(TokenParam code, UriParam system, StringParam version, UriParam source,
            Coding coding, UriParam target, StringParam reverse) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConceptMap getClosureParams(StringParam name, StringParam version, Coding concept) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long findMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, DateRangeParam date,
            UriParam dependson, StringParam description, TokenParam identifier, TokenParam jurisdiction,
            StringParam name, UriParam other, UriParam product, StringParam publisher, TokenParam code, UriParam source,
            TokenParam status, UriParam target, StringParam title, UriParam url, TokenParam version, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, date, dependson, description, identifier, jurisdiction, name,
                other, product, publisher, code, source, status, target, title, url, version, resid, _lastUpdated, _tag,
                _profile, _query, _security, _content);

        Query qry = Query.query(criteria);
        List<ConceptMapEntity> results = mongo.find(qry, ConceptMapEntity.class);

        for (ConceptMapEntity conceptMapEntity : results) {
            Criteria criteria1 = null;
            criteria1 = Criteria.where("conceptMapID").is(conceptMapEntity.id.toString());
            if (source != null) {
                criteria1.and("source").regex(source.getValue());
            }
            if (target != null) {
                criteria1.and("target").regex(target.getValue());
            }
            Query qry1 = Query.query(criteria1);
            List<GroupElementEntity> groups = mongo.find(qry1, GroupElementEntity.class);
            List<GroupElementEntity> groupTmps = new ArrayList<>();
            if (groups != null && groups.size() > 0) {
                for (GroupElementEntity groupElementEntity : groups) {
                    List<ElementEntity> elements = getElements(groupElementEntity.id.toString());
                    groupElementEntity.element = (elements);
                    UnMappedEntity unMapped = getUnMappeds(groupElementEntity.id.toString());
                    groupElementEntity.unmapped = (unMapped);
                    groupTmps.add(groupElementEntity);
                }
                conceptMapEntity.group = (groupTmps);
                ConceptMap conceptMap = conceptMapEntityToFHIRConceptMap.transform(conceptMapEntity);
                resources.add(conceptMap);
            }

        }
        return resources.size();
    }

    private ConceptMapEntity saveConceptMapEntity(ConceptMap object, int version, ConceptMapEntity entity,
            String fhirId) {
        entity = createNewConceptMapEntity(object, version, fhirId);
        mongo.save(entity);
        // save and set group to concept - start
        ObjectId conceptMapEntityId = entity.id;
        if (conceptMapEntityId != null && object.hasGroup()) {
            List<GroupElementEntity> groups = new ArrayList<>();
            for (ConceptMapGroupComponent itemGroup : object.getGroup()) {
                GroupElementEntity group = createNewGroupElementEntity(itemGroup, version,
                        conceptMapEntityId.toString());
                mongo.save(group);
                // save and set element to group - start
                ObjectId groupElementId = entity.id;
                List<ElementEntity> elements = new ArrayList<>();
                if (groupElementId != null && itemGroup.hasElement() && !groupElementId.toString().isEmpty()) {
                    for (SourceElementComponent itemElement : itemGroup.getElement()) {
                        ElementEntity element = createNewElementEntity(itemElement, version, groupElementId.toString());
                        mongo.save(element);
                        // save and set target to element - start
                        ObjectId elementEntityId = element.id;
                        if (itemElement != null && itemElement.hasTarget()) {
                            List<TargetElementEntity> targets = new ArrayList<>();
                            for (TargetElementComponent itemTarget : itemElement.getTarget()) {
                                TargetElementEntity target = createNewTargetElementEntity(itemTarget, version,
                                        elementEntityId.toString());
                                mongo.save(target);
                                targets.add(target);
                            }
                            element.target = (targets);
                        }
                        // save and set target to element - end
                        elements.add(element);
                    }
                    group.element = (elements);
                }
                // save and set element to group - end
                groups.add(group);
            }
            entity.group = (groups);
        }
        // save and set group to concept - end
        return entity;
    }

    private TargetElementEntity createNewTargetElementEntity(TargetElementComponent object, int version,
            String elementEntityId) {
        var entity = TargetElementEntity.fromTargetElementComponent(object);
        entity.elementEntityID = (elementEntityId);
        entity.active = (true);
        entity.version = (version);
        entity.resCreated = (new Date());
        return entity;
    }

    private ElementEntity createNewElementEntity(SourceElementComponent object, int version, String groupElementId) {
        var entity = ElementEntity.fromSourceElementComponent(object);
        // groupElementId
        if (groupElementId != null && !groupElementId.isEmpty()) {
            entity.groupElementID = (groupElementId);
        }
        entity.active = (true);
        entity.version = (version);
        entity.resCreated = (new Date());
        return entity;
    }

    private GroupElementEntity createNewGroupElementEntity(ConceptMapGroupComponent object, int version,
            String conceptMapId) {
        var entity = GroupElementEntity.fromConceptMapGroupComponent(object);
        entity.conceptMapID = (conceptMapId);
        entity.active = (true);
        entity.version = (version);
        entity.resCreated = (new Date());
        return entity;
    }

    private ConceptMapEntity createNewConceptMapEntity(ConceptMap object, int version, String fhirId) {
        
        var entity = ConceptMapEntity.fromConceptMap(object);
        DataConvertUtil.setMetaExt(object, entity);
        if (fhirId != null && !fhirId.isEmpty()) {
            entity.fhir_id = (fhirId);
        } else {
            entity.fhir_id = (StringUtil.generateUID());
        }
        entity.active = (true);
        entity.version = (version);
        entity.resCreated = (new Date());
        return entity;
    }

    public List<GroupElementEntity> getGroups(String objectId) {
        List<GroupElementEntity> groupTmps = new ArrayList<>();
        if (!StringUtils.isBlank(objectId)) {
            Criteria criteria = null;
            criteria = Criteria.where("conceptMapID").regex(objectId);
            Query qry = Query.query(criteria);
            List<GroupElementEntity> groups = mongo.find(qry, GroupElementEntity.class);
            if (groups.size() > 0) {
                for (GroupElementEntity groupElementEntity : groups) {
                    List<ElementEntity> elements = getElements(groupElementEntity.id.toString());
                    groupElementEntity.element = (elements);
                    UnMappedEntity unMapped = getUnMappeds(groupElementEntity.id.toString());
                    groupElementEntity.unmapped = (unMapped);
                    groupTmps.add(groupElementEntity);
                }
            }
        }
        return groupTmps;
    }

    public List<ElementEntity> getElements(String objectId) {
        List<ElementEntity> elementTmps = new ArrayList<>();
        if (!StringUtils.isBlank(objectId)) {
            Criteria criteria = null;
            criteria = Criteria.where("groupElementID").regex(objectId);
            Query qry = Query.query(criteria);
            List<ElementEntity> elements = mongo.find(qry, ElementEntity.class);
            if (elements.size() > 0) {
                for (ElementEntity elementEntity : elements) {
                    List<TargetElementEntity> targets = getTargets(elementEntity.id.toString());
                    elementEntity.target = (targets);
                    elementTmps.add(elementEntity);
                }
            }
        }
        return elementTmps;
    }

    public List<TargetElementEntity> getTargets(String objectId) {
        List<TargetElementEntity> targetTmps = new ArrayList<>();
        if (!StringUtils.isBlank(objectId)) {
            Criteria criteria = null;
            criteria = Criteria.where("elementEntityID").regex(objectId);
            Query qry = Query.query(criteria);
            List<TargetElementEntity> targets = mongo.find(qry, TargetElementEntity.class);
            if (targets.size() > 0) {
                for (TargetElementEntity targetElementEntity : targets) {
                    List<DependOnEntity> dependOns = getDependOns(targetElementEntity.id.toString());
                    targetElementEntity.dependsOn = (dependOns);
                    targetTmps.add(targetElementEntity);
                }
            }
        }
        return targetTmps;
    }

    public List<DependOnEntity> getDependOns(String objectId) {
        List<DependOnEntity> dependOns = new ArrayList<>();
        if (!StringUtils.isBlank(objectId)) {
            Criteria criteria = null;
            criteria = Criteria.where("targetElementEntityID").regex(objectId);
            Query qry = Query.query(criteria);
            dependOns = mongo.find(qry, DependOnEntity.class);
        }
        return dependOns;
    }

    public UnMappedEntity getUnMappeds(String objectId) {
        UnMappedEntity unMapped = null;
        if (!StringUtils.isBlank(objectId)) {
            Criteria criteria = null;
            criteria = Criteria.where("groupElementEntityID").regex(objectId);
            Query qry = Query.query(criteria);
            unMapped = mongo.findOne(qry, UnMappedEntity.class);
        }
        return unMapped;
    }

    private Criteria setParamToCriteria(TokenParam active, DateRangeParam date, UriParam dependson,
            StringParam description, TokenParam identifier, TokenParam jurisdiction, StringParam name, UriParam other,
            UriParam product, StringParam publisher, TokenParam code, UriParam source, TokenParam status,
            UriParam target, StringParam title, UriParam url, TokenParam version, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        Criteria criteria = null;
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active);
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // set param default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        // custom
        if (name != null) {
            criteria.and("name").regex(name.getValue());
        }
        // name
        if (description != null) {
            criteria.and("description").is(description.getValue());
        }
        // jurisdiction
        if (jurisdiction != null) {
            criteria.and("jurisdiction").regex(jurisdiction.getValue());
        }
        // publisher
        if (publisher != null) {
            criteria.and("publisher").regex(publisher.getValue());
        }
        // status
        if (status != null) {
            criteria.and("status").regex(status.getValue());
        }
        // title
        if (title != null) {
            criteria.and("title").regex(title.getValue());
        }
        // url
        if (url != null) {
            criteria.and("url").regex(url.getValue());
        }
        // version
        if (version != null) {
            criteria.and("version").regex(version.getValue());
        }
        return criteria;
    }

}
