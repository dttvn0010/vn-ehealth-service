package vn.ehealth.hl7.fhir.provider.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.IdType;
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
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.provider.dao.IHealthcareService;
import vn.ehealth.hl7.fhir.provider.dao.transform.HealthcareServiceEntityToFHIRHealthcareService;
import vn.ehealth.hl7.fhir.provider.entity.HealthcareServiceEntity;

@Repository
public class HealthcareServiceDao implements IHealthcareService {

    @Autowired
    MongoOperations mongo;

    @Autowired
    HealthcareServiceEntityToFHIRHealthcareService healthcareServiceEntityToFHIRHealthcareService;

    @Override
    public HealthcareService create(FhirContext fhirContext, HealthcareService object) {
        HealthcareServiceEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewHealthcareServiceEntity(object, version, null);
            // save HealthcareServiceEntity database
            mongo.save(entity);
            return healthcareServiceEntityToFHIRHealthcareService.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "healthcareService", key = "#idType")
    public HealthcareService update(FhirContext fhirContext, HealthcareService object, IdType idType) {
        HealthcareServiceEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, HealthcareServiceEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove HealthcareServiceEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save HealthcareServiceEntity
            int version = entityOld.version + 1;
            if (object != null) {
                HealthcareServiceEntity entity = createNewHealthcareServiceEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return healthcareServiceEntityToFHIRHealthcareService.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "healthcareService", key = "#idType")
    public HealthcareService read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            HealthcareServiceEntity entity = mongo.findOne(query, HealthcareServiceEntity.class);
            if (entity != null) {
                return healthcareServiceEntityToFHIRHealthcareService.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "healthcareService", key = "#idType")
    public HealthcareService remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            HealthcareServiceEntity entity = mongo.findOne(query, HealthcareServiceEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return healthcareServiceEntityToFHIRHealthcareService.transform(entity);
            }
        }
        return null;
    }

    @Override
    public HealthcareService readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                HealthcareServiceEntity entity = mongo.findOne(query, HealthcareServiceEntity.class);
                if (entity != null) {
                    return healthcareServiceEntityToFHIRHealthcareService.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, TokenParam category,
            TokenParam characteristic, ReferenceParam endpoint, TokenParam identifier, ReferenceParam location,
            StringParam name, ReferenceParam organization, StringParam programname, TokenParam type, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = null;
        criteria = setParamToCriteria(active, category, characteristic, endpoint, identifier, location, name,
                organization, programname, type, resid, _lastUpdated, _tag, _profile, _query, _security, _content);

        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        Pageable pageableRequest;
        pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
        query.with(pageableRequest);
        if (!sortParam.equals("")) {
            query.with(new Sort(Sort.Direction.ASC, sortParam));
        }
        List<HealthcareServiceEntity> healthcareServiceResults = mongo.find(query, HealthcareServiceEntity.class);
        for (HealthcareServiceEntity healthcareServiceEntity : healthcareServiceResults) {
            resources.add(healthcareServiceEntityToFHIRHealthcareService.transform(healthcareServiceEntity));
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, TokenParam category,
            TokenParam characteristic, ReferenceParam endpoint, TokenParam identifier, ReferenceParam location,
            StringParam name, ReferenceParam organization, StringParam programname, TokenParam type, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        Criteria criteria = null;
        criteria = setParamToCriteria(active, category, characteristic, endpoint, identifier, location, name,
                organization, programname, type, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        long count = 0;
        if (criteria != null) {
            Query qry = Query.query(criteria);
            count = mongo.count(qry, HealthcareServiceEntity.class);
        } else {
            Query query = new Query();
            count = mongo.count(query, HealthcareServiceEntity.class);
        }
        return count;
    }

    private HealthcareServiceEntity createNewHealthcareServiceEntity(HealthcareService obj, int version,
            String fhirId) {
        var ent = HealthcareServiceEntity.fromHealthcareService(obj);
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

    private Criteria setParamToCriteria(TokenParam active, TokenParam category, TokenParam characteristic,
            ReferenceParam endpoint, TokenParam identifier, ReferenceParam location, StringParam name,
            ReferenceParam organization, StringParam programname, TokenParam type, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        Criteria criteria = null;
        // default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active.getValue());
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // category
        if (category != null) {
            criteria.and("category").regex(category.getValue());
        }
        // characteristic
        if (characteristic != null) {
            criteria.and("characteristic").regex(characteristic.getValue());
        }
        // endpoint
        if (endpoint != null) {
            criteria.orOperator(Criteria.where("endpoint.reference").regex(endpoint.getValue()),
                    Criteria.where("endpoint.display").regex(endpoint.getValue()),
                    Criteria.where("endpoint.identifier.value").regex(endpoint.getValue()),
                    Criteria.where("endpoint.identifier.system").regex(endpoint.getValue()));
        }
        // identifier
        if (identifier != null) {
            criteria.and("identifier.system").is(identifier.getSystem()).and("identifier.value")
                    .is(identifier.getValue());
        }
        // location
        if (location != null) {
            criteria.orOperator(Criteria.where("location.reference").regex(location.getValue()),
                    Criteria.where("location.display").regex(location.getValue()),
                    Criteria.where("location.identifier.value").regex(location.getValue()),
                    Criteria.where("location.identifier.system").regex(location.getValue()));
        }
        // name
        if (name != null && !name.isEmpty()) {
            criteria.and("name").regex(name.getValue());
        }
        // organization
        if (organization != null) {
            criteria.orOperator(Criteria.where("providedBy.reference").regex(organization.getValue()),
                    Criteria.where("providedBy.display").regex(organization.getValue()),
                    Criteria.where("providedBy.identifier.value").regex(organization.getValue()),
                    Criteria.where("providedBy.identifier.system").regex(organization.getValue()));
        }
        // programName
        if (programname != null) {
            criteria.and("programName").regex(characteristic.getValue());
        }
        // type
        if (type != null) {
            criteria.and("type").regex(type.getValue());
        }

        return criteria;
    }

}
