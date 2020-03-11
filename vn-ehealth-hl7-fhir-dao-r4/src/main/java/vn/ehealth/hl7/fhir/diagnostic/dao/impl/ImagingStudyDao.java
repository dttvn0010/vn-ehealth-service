package vn.ehealth.hl7.fhir.diagnostic.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.ImagingStudy;
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
import vn.ehealth.hl7.fhir.diagnostic.dao.IImagingStudy;
import vn.ehealth.hl7.fhir.diagnostic.dao.transform.ImagingStudyEntityToFHIRImagingStudy;
import vn.ehealth.hl7.fhir.diagnostic.entity.ImagingStudyEntity;

@Repository
public class ImagingStudyDao implements IImagingStudy {

    @Autowired
    MongoOperations mongo;

    @Autowired
    ImagingStudyEntityToFHIRImagingStudy imagingStudyEntityToFHIRImagingStudy;

    @Override
    public ImagingStudy create(FhirContext fhirContext, ImagingStudy object) {
        ImagingStudyEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewImagingStudyEntity(object, version, null);
            // save ImagingStudyEntity database
            mongo.save(entity);
            return imagingStudyEntityToFHIRImagingStudy.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "imagingStudy", key = "#idType")
    public ImagingStudy update(FhirContext fhirContext, ImagingStudy object, IdType idType) {
        ImagingStudyEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, ImagingStudyEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove ImagingStudyEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save ImagingStudyEntity
            int version = entityOld.version + 1;
            if (object != null) {
                ImagingStudyEntity entity = createNewImagingStudyEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return imagingStudyEntityToFHIRImagingStudy.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "imagingStudy", key = "#idType")
    public ImagingStudy read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ImagingStudyEntity entity = mongo.findOne(query, ImagingStudyEntity.class);
            if (entity != null) {
                return imagingStudyEntityToFHIRImagingStudy.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "imagingStudy", key = "#idType")
    public ImagingStudy remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ImagingStudyEntity entity = mongo.findOne(query, ImagingStudyEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return imagingStudyEntityToFHIRImagingStudy.transform(entity);
            }
        }
        return null;
    }

    @Override
    public ImagingStudy readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                ImagingStudyEntity entity = mongo.findOne(query, ImagingStudyEntity.class);
                if (entity != null) {
                    return imagingStudyEntityToFHIRImagingStudy.transform(entity);
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
        List<ImagingStudyEntity> ImagingStudyEntitys = mongo.find(query, ImagingStudyEntity.class);
        if (ImagingStudyEntitys != null) {
            for (ImagingStudyEntity item : ImagingStudyEntitys) {
                ImagingStudy ImagingStudy = imagingStudyEntityToFHIRImagingStudy.transform(item);
                resources.add(ImagingStudy);
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
        total = mongo.count(query, ImagingStudyEntity.class);
        return total;
    }

    private ImagingStudyEntity createNewImagingStudyEntity(ImagingStudy obj, int version, String fhirId) {
        var ent = ImagingStudyEntity.fromImagingStudy(obj);
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

        return criteria;
    }
}
