package vn.ehealth.hl7.fhir.ehr.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.EpisodeOfCare;
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
import vn.ehealth.hl7.fhir.ehr.dao.IEpisodeOfCare;
import vn.ehealth.hl7.fhir.ehr.dao.transform.EpisodeOfCareEntityToFHIREpisodeOfCare;
import vn.ehealth.hl7.fhir.ehr.entity.EpisodeOfCareEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Repository
public class EpisodeOfCareDao implements IEpisodeOfCare {

    @Autowired
    MongoOperations mongo;

    @Autowired
    EpisodeOfCareEntityToFHIREpisodeOfCare episodeOfCareEntityToFHIREpisodeOfCare;

    @Override
    public EpisodeOfCare create(FhirContext fhirContext, EpisodeOfCare object) {
        EpisodeOfCareEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewEpisodeOfCareEntity(object, version, null);
            // save EpisodeOfCareEntity database
            mongo.save(entity);
            return episodeOfCareEntityToFHIREpisodeOfCare.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "episodeOfCare", key = "#idType")
    public EpisodeOfCare update(FhirContext fhirContext, EpisodeOfCare object, IdType idType) {
        EpisodeOfCareEntity entityOld = null;
        String fhirId = "";
        if (idType != null) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, EpisodeOfCareEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove EpisodeOfCareEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save EpisodeOfCareEntity
            int version = entityOld.version + 1;
            if (object != null) {
                EpisodeOfCareEntity entity = createNewEpisodeOfCareEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return episodeOfCareEntityToFHIREpisodeOfCare.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "episodeOfCare", key = "#idType")
    public EpisodeOfCare read(FhirContext fhirContext, IdType idType) {
        if (idType != null) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            EpisodeOfCareEntity entity = mongo.findOne(query, EpisodeOfCareEntity.class);
            if (entity != null) {
                return episodeOfCareEntityToFHIREpisodeOfCare.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "episodeOfCare", key = "#idType")
    public EpisodeOfCare remove(FhirContext fhirContext, IdType idType) {
        if (idType != null) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            EpisodeOfCareEntity entity = mongo.findOne(query, EpisodeOfCareEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return episodeOfCareEntityToFHIREpisodeOfCare.transform(entity);
            }
        }
        return null;
    }

    @Override
    public EpisodeOfCare readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                EpisodeOfCareEntity entity = mongo.findOne(query, EpisodeOfCareEntity.class);
                if (entity != null) {
                    return episodeOfCareEntityToFHIREpisodeOfCare.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, ReferenceParam careManager,
            ReferenceParam condition, DateRangeParam date, TokenParam identifier, ReferenceParam incomingreferral,
            ReferenceParam organization, ReferenceParam patient, TokenParam status, TokenParam type, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content, StringParam _page, String sortParam, Integer count) {

        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(fhirContext, active, careManager, condition, date, identifier,
                incomingreferral, organization, patient, status, type, resid, _lastUpdated, _tag, _profile, _query,
                _security, _content);
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
        List<EpisodeOfCareEntity> episodeOfCareEntitys = mongo.find(query, EpisodeOfCareEntity.class);
        if (episodeOfCareEntitys != null) {
            for (EpisodeOfCareEntity item : episodeOfCareEntitys) {
                EpisodeOfCare episodeOfCare = episodeOfCareEntityToFHIREpisodeOfCare.transform(item);
                resources.add(episodeOfCare);
            }
        }
        return resources;
    }

    @Override
    public long getTotal(FhirContext fhirContext, TokenParam active, ReferenceParam careManager,
            ReferenceParam condition, DateRangeParam date, TokenParam identifier, ReferenceParam incomingreferral,
            ReferenceParam organization, ReferenceParam patient, TokenParam status, TokenParam type, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(fhirContext, active, careManager, condition, date, identifier,
                incomingreferral, organization, patient, status, type, resid, _lastUpdated, _tag, _profile, _query,
                _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, EpisodeOfCareEntity.class);
        return total;
    }

    private EpisodeOfCareEntity createNewEpisodeOfCareEntity(EpisodeOfCare obj, int version, String fhirId) {
        var ent = EpisodeOfCareEntity.fromEpisodeOfCare(obj);
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

    private Criteria setParamToCriteria(FhirContext fhirContext, TokenParam active, ReferenceParam careManager,
            ReferenceParam condition, DateRangeParam date, TokenParam identifier, ReferenceParam incomingreferral,
            ReferenceParam organization, ReferenceParam patient, TokenParam status, TokenParam type, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        Criteria criteria = null;
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active);
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // param default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        // careManager
        if (careManager != null) {
            if(careManager.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("careManager.reference").is(careManager.getValue()),
                        Criteria.where("careManager.display").is(careManager.getValue()));
            }else {
                String[] ref= careManager.getValue().split("\\|");
                criteria.and("careManager.identifier.system").is(ref[0]).and("careManager.identifier.value").is(ref[1]);
            }
        }
        // condition
        if (condition != null) {
            if(condition.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("diagnosis.condition.reference").is(condition.getValue()),
                        Criteria.where("diagnosis.condition.display").is(condition.getValue()));
            }else {
                String[] ref= condition.getValue().split("\\|");
                criteria.and("diagnosis.condition.identifier.system").is(ref[0]).and("diagnosis.condition.identifier.value").is(ref[1]);
            }
        }
        // date
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "period", date);
        }
        // incomingreferral
        if (incomingreferral != null) {
            if(incomingreferral.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("referralRequest.reference").is(incomingreferral.getValue()),
                        Criteria.where("referralRequest.display").is(incomingreferral.getValue()));
            }else {
                String[] ref= incomingreferral.getValue().split("\\|");
                criteria.and("referralRequest.identifier.system").is(ref[0]).and("referralRequest.identifier.value").is(ref[1]);
            }
        }
        // organization
        if (organization != null) {
            if(organization.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("managingOrganization.reference").is(organization.getValue()),
                        Criteria.where("managingOrganization.display").is(organization.getValue()));
            }else {
                String[] ref= organization.getValue().split("\\|");
                criteria.and("managingOrganization.identifier.system").is(ref[0]).and("managingOrganization.identifier.value").is(ref[1]);
            }
        }
        // patient
        if (patient != null) {
            if(patient.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("patient.reference").is(patient.getValue()),
                        Criteria.where("patient.display").is(patient.getValue()));
            }else {
                String[] ref= patient.getValue().split("\\|");
                criteria.and("patient.identifier.system").is(ref[0]).and("patient.identifier.value").is(ref[1]);
            }
        }
        // status
        if (status != null) {
            criteria.and("status").is(status.getValue());
        }
        // type
        if (type != null) {
            criteria.and("type.coding.code.myStringValue").is(type.getValue());
        }
        return criteria;
    }
}
