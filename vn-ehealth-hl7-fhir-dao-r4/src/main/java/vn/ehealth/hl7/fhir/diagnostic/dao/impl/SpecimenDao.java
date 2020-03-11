package vn.ehealth.hl7.fhir.diagnostic.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Specimen;
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
import vn.ehealth.hl7.fhir.diagnostic.dao.ISpecimen;
import vn.ehealth.hl7.fhir.diagnostic.dao.transform.SpecimenEntityToFHIRSpecimen;
import vn.ehealth.hl7.fhir.diagnostic.entity.SpecimenEntity;

@Repository
public class SpecimenDao implements ISpecimen {

    @Autowired
    MongoOperations mongo;

    @Autowired
    SpecimenEntityToFHIRSpecimen specimenEntityToFHIRSpecimen;

    @Override
    public Specimen create(FhirContext fhirContext, Specimen object) {
        SpecimenEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewSpecimenEntity(object, version, null);
            // save SpecimenEntity database
            mongo.save(entity);
            return specimenEntityToFHIRSpecimen.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "specimen", key = "#idType")
    public Specimen update(FhirContext fhirContext, Specimen object, IdType idType) {
        SpecimenEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, SpecimenEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove SpecimenEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save SpecimenEntity
            int version = entityOld.version + 1;
            if (object != null) {
                SpecimenEntity entity = createNewSpecimenEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return specimenEntityToFHIRSpecimen.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "specimen", key = "#idType")
    public Specimen read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            SpecimenEntity entity = mongo.findOne(query, SpecimenEntity.class);
            if (entity != null) {
                return specimenEntityToFHIRSpecimen.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "specimen", key = "#idType")
    public Specimen remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            SpecimenEntity entity = mongo.findOne(query, SpecimenEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return specimenEntityToFHIRSpecimen.transform(entity);
            }
        }
        return null;
    }
    
    @Override
    public Specimen readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query
                        .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                SpecimenEntity entity = mongo.findOne(query, SpecimenEntity.class);
                if (entity != null) {
                    return specimenEntityToFHIRSpecimen.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, resid, _lastUpdated, _tag, _profile, _query, _security,
                _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        Pageable pageableRequest;
        pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
        query.with(pageableRequest);
        if (sortParam != null && !sortParam.equals("")) {
            query.with(new Sort(Sort.Direction.ASC, sortParam));
        }
        List<SpecimenEntity> specimenEntitys = mongo.find(query, SpecimenEntity.class);
        if (specimenEntitys != null) {
            for (SpecimenEntity item : specimenEntitys) {
                Specimen specimen = specimenEntityToFHIRSpecimen.transform(item);
                resources.add(specimen);
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, resid, _lastUpdated, _tag, _profile, _query, _security,
                _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, SpecimenEntity.class);
        return total;
    }

    private SpecimenEntity createNewSpecimenEntity(Specimen obj, int version, String fhirId) {
        var ent = SpecimenEntity.fromSpecimen(obj);
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

    private Criteria setParamToCriteria(TokenParam active, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        Criteria criteria = null;
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active);
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // set param default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                null);

        return criteria;
    }
}
