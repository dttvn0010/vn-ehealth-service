package vn.ehealth.hl7.fhir.ehr.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.CareTeam;
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
import vn.ehealth.hl7.fhir.ehr.dao.ICareTeam;
import vn.ehealth.hl7.fhir.ehr.dao.transform.CareTeamEntityToFHIRCareTeam;
import vn.ehealth.hl7.fhir.ehr.entity.CareTeamEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Repository
public class CareTeamDao implements ICareTeam {

    @Autowired
    MongoOperations mongo;

    @Autowired
    CareTeamEntityToFHIRCareTeam careTeamEntityToFHIRCareTeam;

    @Override
    public CareTeam create(FhirContext fhirContext, CareTeam object) {
        CareTeamEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewCareTeamEntity(object, version, null);
            // save CareTeamEntity database
            mongo.save(entity);
            return careTeamEntityToFHIRCareTeam.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "careTeam", key = "#idType")
    public CareTeam update(FhirContext fhirContext, CareTeam object, IdType idType) {
        CareTeamEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, CareTeamEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove CareTeamEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save CareTeamEntity
            int version = entityOld.version + 1;
            if (object != null) {
                CareTeamEntity entity = createNewCareTeamEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return careTeamEntityToFHIRCareTeam.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "careTeam", key = "#idType")
    public CareTeam read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            CareTeamEntity entity = mongo.findOne(query, CareTeamEntity.class);
            if (entity != null) {
                return careTeamEntityToFHIRCareTeam.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "careTeam", key = "#idType")
    public CareTeam remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            CareTeamEntity entity = mongo.findOne(query, CareTeamEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return careTeamEntityToFHIRCareTeam.transform(entity);
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, TokenParam category,
            ReferenceParam context, DateRangeParam date, ReferenceParam encounter, TokenParam identifier,
            ReferenceParam participant, ReferenceParam patient, TokenParam status, ReferenceParam subject,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, category, context, date, encounter, identifier, participant,
                patient, status, subject, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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
        List<CareTeamEntity> careTeamEntitys = mongo.find(query, CareTeamEntity.class);
        if (careTeamEntitys != null) {
            for (CareTeamEntity item : careTeamEntitys) {
                CareTeam careTeam = careTeamEntityToFHIRCareTeam.transform(item);
                resources.add(careTeam);
            }
        }
        return resources;
    }

    @Override
    public CareTeam readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                CareTeamEntity entity = mongo.findOne(query, CareTeamEntity.class);
                if (entity != null) {
                    return careTeamEntityToFHIRCareTeam.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public long getTotal(FhirContext fhirContext, TokenParam active, TokenParam category, ReferenceParam context,
            DateRangeParam date, ReferenceParam encounter, TokenParam identifier, ReferenceParam participant,
            ReferenceParam patient, TokenParam status, ReferenceParam subject, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, category, context, date, encounter, identifier, participant,
                patient, status, subject, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, CareTeamEntity.class);
        return total;
    }

    private CareTeamEntity createNewCareTeamEntity(CareTeam obj, int version, String fhirId) {
        var ent = CareTeamEntity.fromCareTeam(obj);
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

    private Criteria setParamToCriteria(TokenParam active, TokenParam category, ReferenceParam context,
            DateRangeParam date, ReferenceParam encounter, TokenParam identifier, ReferenceParam participant,
            ReferenceParam patient, TokenParam status, ReferenceParam subject, TokenParam resid,
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
        // category
        if (category != null) {
            criteria.and("category.coding.code.myStringValue").is(category.getValue());
        }
        // date
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "period", date);
        }
        // participant
        if (participant != null) {
            if(participant.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("participant.member.reference").is(participant.getValue()),
                        Criteria.where("participant.member.display").is(participant.getValue()));
            }else {
                String[] ref= participant.getValue().split("\\|");
                criteria.and("participant.member.identifier.system").is(ref[0]).and("participant.member.identifier.value").is(ref[1]);
            }
        }
        // status
        if (status != null) {
            criteria.and("status").is(status.getValue());
        }
        // subject
        if (subject != null) {
            if(subject.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("subject.reference").is(subject.getValue()),
                        Criteria.where("subject.display").is(subject.getValue()));
            }else {
                String[] ref= subject.getValue().split("\\|");
                criteria.and("subject.identifier.system").is(ref[0]).and("subject.identifier.value").is(ref[1]);
            }
        }
        // patient
        if (patient != null) {
            if(patient.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("subject.reference").is(patient.getValue()),
                        Criteria.where("subject.display").is(patient.getValue()));
            }else {
                String[] ref= patient.getValue().split("\\|");
                criteria.and("subject.identifier.system").is(ref[0]).and("subject.identifier.value").is(ref[1]);
            }
        }
        // encounter
        if (encounter != null) {
            if(encounter.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("context.reference").is(encounter.getValue()),
                        Criteria.where("context.display").is(encounter.getValue()));
            }else {
                String[] ref= encounter.getValue().split("\\|");
                criteria.and("context.identifier.system").is(ref[0]).and("context.identifier.value").is(ref[1]);
            }
        }
        return criteria;
    }
}
