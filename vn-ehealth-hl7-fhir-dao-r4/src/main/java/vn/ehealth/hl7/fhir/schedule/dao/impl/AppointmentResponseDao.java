package vn.ehealth.hl7.fhir.schedule.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.AppointmentResponse;
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
import vn.ehealth.hl7.fhir.schedule.dao.IAppointmentResponse;
import vn.ehealth.hl7.fhir.schedule.dao.transform.AppointmentResponseEntityToFHIRAppointmentResponse;
import vn.ehealth.hl7.fhir.schedule.entity.AppointmentResponseEntity;

@Repository
public class AppointmentResponseDao implements IAppointmentResponse {

    @Autowired
    MongoOperations mongo;

    @Autowired
    AppointmentResponseEntityToFHIRAppointmentResponse appointmentResponseEntityToFHIRAppointmentResponse;

    @Override
    public AppointmentResponse create(FhirContext fhirContext, AppointmentResponse object) {
        AppointmentResponseEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewAppointmentResponseEntity(object, version, null);
            // save AppointmentResponseEntity database
            mongo.save(entity);
            return appointmentResponseEntityToFHIRAppointmentResponse.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "appointmentResponse", key = "#idType")
    public AppointmentResponse update(FhirContext fhirContext, AppointmentResponse object, IdType idType) {
        AppointmentResponseEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, AppointmentResponseEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove AppointmentResponseEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save AppointmentResponseEntity
            int version = entityOld.version + 1;
            if (object != null) {
                AppointmentResponseEntity entity = createNewAppointmentResponseEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return appointmentResponseEntityToFHIRAppointmentResponse.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "appointmentResponse", key = "#idType")
    public AppointmentResponse read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            AppointmentResponseEntity entity = mongo.findOne(query, AppointmentResponseEntity.class);
            if (entity != null) {
                return appointmentResponseEntityToFHIRAppointmentResponse.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "appointmentResponse", key = "#idType")
    public AppointmentResponse remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            AppointmentResponseEntity entity = mongo.findOne(query, AppointmentResponseEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return appointmentResponseEntityToFHIRAppointmentResponse.transform(entity);
            }
        }
        return null;
    }

    @Override
    public AppointmentResponse readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                AppointmentResponseEntity entity = mongo.findOne(query, AppointmentResponseEntity.class);
                if (entity != null) {
                    return appointmentResponseEntityToFHIRAppointmentResponse.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, ReferenceParam actor,
            TokenParam identifier, ReferenceParam appointment, ReferenceParam location, ReferenceParam patient,
            ReferenceParam practitioner, TokenParam partStatus, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content,
            StringParam _page, String sortParam, Integer count) {

        List<Resource> resources = new ArrayList<>();

        Criteria criteria = setParamToCriteria(fhirContext, active, actor, identifier, appointment, location, patient,
                practitioner, partStatus, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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
        List<AppointmentResponseEntity> appointmentResponseEntitys = mongo.find(query, AppointmentResponseEntity.class);
        if (appointmentResponseEntitys != null) {
            for (AppointmentResponseEntity item : appointmentResponseEntitys) {
                AppointmentResponse appointmentResponse = appointmentResponseEntityToFHIRAppointmentResponse
                        .transform(item);
                resources.add(appointmentResponse);
            }
        }
        return resources;
    }

    @Override
    public long findMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, ReferenceParam actor,
            TokenParam identifier, ReferenceParam appointment, ReferenceParam location, ReferenceParam patient,
            ReferenceParam practitioner, TokenParam partStatus, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(fhirContext, active, actor, identifier, appointment, location, patient,
                practitioner, partStatus, resid, _lastUpdated, _tag, _profile, _query, _security, _content);

        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, AppointmentResponseEntity.class);
        return total;
    }

    private Criteria setParamToCriteria(FhirContext fhirContext, TokenParam active, ReferenceParam actor,
            TokenParam identifier, ReferenceParam appointment, ReferenceParam location, ReferenceParam patient,
            ReferenceParam practitioner, TokenParam partStatus, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        Criteria criteria = null;
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active.getValue());
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        if (actor != null) {
            if(actor.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("actor.reference").is(actor.getValue()),
                        Criteria.where("actor.display").is(actor.getValue()));
            }else {
                String[] ref= actor.getValue().split("\\|");
                criteria.and("actor.identifier.system").is(ref[0]).and("actor.identifier.value").is(ref[1]);
            }
        }

        if (appointment != null) {
            if(appointment.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("appointment.reference").is(appointment.getValue()),
                        Criteria.where("appointment.display").is(appointment.getValue()));
            }else {
                String[] ref= appointment.getValue().split("\\|");
                criteria.and("appointment.identifier.system").is(ref[0]).and("appointment.identifier.value").is(ref[1]);
            }
        }
        if (location != null) {
            if(location.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("actor.reference").is(location.getValue()),
                        Criteria.where("actor.display").is(location.getValue()));
            }else {
                String[] ref= location.getValue().split("\\|");
                criteria.and("actor.identifier.system").is(ref[0]).and("actor.identifier.value").is(ref[1]);
            }
        }
        if (patient != null) {
            if(patient.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("actor.reference").is(patient.getValue()),
                        Criteria.where("actor.display").is(patient.getValue()));
            }else {
                String[] ref= patient.getValue().split("\\|");
                criteria.and("actor.identifier.system").is(ref[0]).and("actor.identifier.value").is(ref[1]);
            }
        }
        if (practitioner != null) {
            if(practitioner.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("actor.reference").is(practitioner.getValue()),
                        Criteria.where("actor.display").is(practitioner.getValue()));
            }else {
                String[] ref= practitioner.getValue().split("\\|");
                criteria.and("actor.identifier.system").is(ref[0]).and("actor.identifier.value").is(ref[1]);
            }
        }
        if (partStatus != null) {
            criteria.and("participantStatus").is(partStatus.getValue());
        }
        return criteria;
    }

    private AppointmentResponseEntity createNewAppointmentResponseEntity(AppointmentResponse obj, int version,
            String fhirId) {
        var ent = AppointmentResponseEntity.from(obj);
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
}
