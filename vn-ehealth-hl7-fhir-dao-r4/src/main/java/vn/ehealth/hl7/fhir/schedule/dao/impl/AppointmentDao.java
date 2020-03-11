package vn.ehealth.hl7.fhir.schedule.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Appointment;
import org.hl7.fhir.r4.model.Appointment.AppointmentParticipantComponent;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
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
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.schedule.dao.IAppointment;
import vn.ehealth.hl7.fhir.schedule.dao.transform.AppointmentEntityToFHIRAppointment;
import vn.ehealth.hl7.fhir.schedule.entity.AppointmentEntity;
import vn.ehealth.hl7.fhir.schedule.entity.ParticipantEntity;

@Repository
public class AppointmentDao implements IAppointment {

    @Autowired
    MongoOperations mongo;

    @Autowired
    AppointmentEntityToFHIRAppointment appointmentEntityToFHIRAppointment;

    @Override
    public Appointment create(FhirContext fhirContext, Appointment object) {
        AppointmentEntity objEntity = null;
        int version = ConstantKeys.VERSION_1;
        objEntity = createNewAppointmentEntity(object, version, null);
        // save AppointmentEntity database
        mongo.save(objEntity);

        //
        List<ParticipantEntity> participantEntitys = new ArrayList<>();
        ObjectId appointmentEntityId = objEntity.id;
        if (appointmentEntityId != null && object.hasParticipant()) {
            participantEntitys = saveListParticipantEntity(appointmentEntityId, object.getParticipant(), version);
        }
        // set List ParticipantEntity to AppointmentEntity
        objEntity.participant = (participantEntitys);
        return appointmentEntityToFHIRAppointment.transform(objEntity);
    }

    @Override
    public Appointment update(FhirContext fhirContext, Appointment object, IdType idType) {
        AppointmentEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, AppointmentEntity.class);
        }
        if (entityOld != null && !fhirId.isEmpty()) {
            // remove AppointmentEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);

            // remove ParticipantEntity old
            List<ParticipantEntity> participantEntityOlds = new ArrayList<>();
            Query query = Query.query(Criteria.where("appointmentEntityID").is(entityOld.id));
            participantEntityOlds = mongo.find(query, ParticipantEntity.class);
            if (participantEntityOlds != null) {
                for (ParticipantEntity item : participantEntityOlds) {
                    item.resDeleted = (new Date());
                    item.active = (false);
                    mongo.save(item);
                }
            }
            int version = entityOld.version + 1;
            if (object != null) {
                // create new Appointment
                AppointmentEntity entity = createNewAppointmentEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                // save PractitionerEntity database
                mongo.save(entity);

                List<ParticipantEntity> participantEntitys = new ArrayList<>();
                ObjectId appointmentEntityId = entity.id;
                if (appointmentEntityId != null && object.hasParticipant()) {
                    if (appointmentEntityId != null && object.hasParticipant()) {
                        participantEntitys = saveListParticipantEntity(appointmentEntityId, object.getParticipant(),
                                version);
                    }
                }
                // set List ParticipantEntity to AppointmentEntity
                entity.participant = (participantEntitys);
                return appointmentEntityToFHIRAppointment.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Appointment remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            AppointmentEntity entity = mongo.findOne(query, AppointmentEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                // remove database AppointmentEntity
                mongo.save(entity);

                List<ParticipantEntity> participantEntitys = new ArrayList<>();
                Query queryParticipant = Query.query(Criteria.where("appointmentEntityID").is(entity.id));
                participantEntitys = mongo.find(queryParticipant, ParticipantEntity.class);
                if (participantEntitys != null) {
                    for (ParticipantEntity item : participantEntitys) {
                        item.active = (false);
                        item.resDeleted = (new Date());
                        mongo.save(item);
                    }
                    entity.participant = (participantEntitys);
                }
                return appointmentEntityToFHIRAppointment.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Appointment read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            AppointmentEntity entity = mongo.findOne(query, AppointmentEntity.class);
            if (entity != null) {
                List<ParticipantEntity> participantEntitys = new ArrayList<>();
                Query queryParticipant = Query.query(
                        Criteria.where("appointmentEntityID").is(entity.id).and(ConstantKeys.SP_ACTIVE).is(true));
                participantEntitys = mongo.find(queryParticipant, ParticipantEntity.class);
                entity.participant = (participantEntitys);
                return appointmentEntityToFHIRAppointment.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Appointment readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                AppointmentEntity entity = mongo.findOne(query, AppointmentEntity.class);
                if (entity != null) {
                    return appointmentEntityToFHIRAppointment.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, ReferenceParam actor,
            TokenParam appointmentType, DateRangeParam date, TokenParam identifier, ReferenceParam incomingreferral,
            ReferenceParam location, TokenParam partStatus, ReferenceParam patient, ReferenceParam practitioner,
            TokenParam serviceType, TokenParam status, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
            String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = null;
        criteria = setParamToCriteria(active, actor, appointmentType, date, identifier, incomingreferral, location,
                partStatus, patient, practitioner, serviceType, status, resid, _lastUpdated, _tag, _profile, _query,
                _security, _content);
        Query qry = new Query();
        if (criteria != null) {
            qry = Query.query(criteria);
        }
        Pageable pageableRequest;
        pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
        qry.with(pageableRequest);
        if (!sortParam.equals("")) {
            qry.with(new Sort(Sort.Direction.ASC, sortParam));
        }
        List<AppointmentEntity> appointmentResults = mongo.find(qry, AppointmentEntity.class);
        for (AppointmentEntity appointmentEntity : appointmentResults) {
            resources.add(appointmentEntityToFHIRAppointment.transform(appointmentEntity));
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, ReferenceParam actor,
            TokenParam appointmentType, DateRangeParam date, TokenParam identifier, ReferenceParam incomingreferral,
            ReferenceParam location, TokenParam partStatus, ReferenceParam patient, ReferenceParam practitioner,
            TokenParam serviceType, TokenParam status, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        Criteria criteria = null;
        criteria = setParamToCriteria(active, actor, appointmentType, date, identifier, incomingreferral, location,
                partStatus, patient, practitioner, serviceType, status, resid, _lastUpdated, _tag, _profile, _query,
                _security, _content);
        long count = 0;
        if (criteria != null) {
            Query qry = Query.query(criteria);
            count = mongo.count(qry, AppointmentEntity.class);
        } else {
            Query query = new Query();
            count = mongo.count(query, AppointmentEntity.class);
        }
        return count;
    }

    private List<ParticipantEntity> saveListParticipantEntity(ObjectId appointmentEntityId,
            List<AppointmentParticipantComponent> appointmentParticipantComponents, int version) {

        List<ParticipantEntity> participantEntitys = new ArrayList<>();
        for (AppointmentParticipantComponent item : appointmentParticipantComponents) {
            // cretae new ParticipantEntity
            ParticipantEntity participantEntity = createNewParticipantEntity(appointmentEntityId, item, version);
            // save ParticipantEntity to database
            mongo.save(participantEntity);
            participantEntitys.add(participantEntity);
        }
        return participantEntitys;
    }

    private ParticipantEntity createNewParticipantEntity(ObjectId appointmentEntityId,
            AppointmentParticipantComponent object, int version) {
        ParticipantEntity participantEntity = new ParticipantEntity();
        // type
        if (object.hasType()) {
            participantEntity.type = object.getType();
        }
        // actor
        if (object.hasActor()) {
            participantEntity.actor = BaseReference.fromReference(object.getActor());
        }
        // required
        if (object.hasRequired()) {
            participantEntity.required = object.getRequired().toCode();
        }
        // status
        if (object.hasStatus()) {
            participantEntity.status = object.getStatus().toCode();
        }
        // appointmentEntityID
        participantEntity.appointmentEntityID = appointmentEntityId.toString();
        // active
        participantEntity.active = (true);
        // version
        participantEntity.version = (version);
        // ResCreated
        participantEntity.resCreated = (new Date());
        return participantEntity;
    }

    private AppointmentEntity createNewAppointmentEntity(Appointment obj, int version, String fhirId) {
        var ent = AppointmentEntity.fromAppointment(obj);
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

    private Criteria setParamToCriteria(TokenParam active, ReferenceParam actor, TokenParam appointmentType,
            DateRangeParam date, TokenParam identifier, ReferenceParam incomingreferral, ReferenceParam location,
            TokenParam partStatus, ReferenceParam patient, ReferenceParam practitioner, TokenParam serviceType,
            TokenParam status, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
            TokenParam _query, TokenParam _security, StringParam _content) {
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
        // actor
        if (actor != null) {
            if(actor.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("participant.actor.reference").is(actor.getValue()),
                        Criteria.where("participant.actor.display").is(actor.getValue()));
            }else {
                String[] ref= actor.getValue().split("\\|");
                criteria.and("participant.actor.identifier.system").is(ref[0]).and("participant.actor.identifier.value").is(ref[1]);
            }
        }
        // appointment-type
        if (appointmentType != null) {
            criteria.and("appointmentType").regex(appointmentType.getValue());
        }
        // date
        if (date != null) {
            String keySearch = "start";
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, keySearch, date);
        }
        // identifier
        if (identifier != null) {
            criteria.and("identifiers.system").is(identifier.getSystem()).and("identifiers.value")
                    .is(identifier.getValue());
        }
        // incomingreferral
        if (incomingreferral != null) {
            if(incomingreferral.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("incomingreferral.reference").is(incomingreferral.getValue()),
                        Criteria.where("incomingreferral.display").is(incomingreferral.getValue()));
            }else {
                String[] ref= incomingreferral.getValue().split("\\|");
                criteria.and("incomingreferral.identifier.system").is(ref[0]).and("incomingreferral.identifier.value").is(ref[1]);
            }
        }
        // location
        if (location != null) {
            if(location.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("participant.actor.reference").is(location.getValue()),
                        Criteria.where("participant.actor.display").is(location.getValue()));
            }else {
                String[] ref= location.getValue().split("\\|");
                criteria.and("participant.actor.identifier.system").is(ref[0]).and("participant.actor.identifier.value").is(ref[1]);
            }
        }
        // part-status
        if (partStatus != null) {
            criteria.and("participant.actor").regex(partStatus.getValue());
        }
        // patient
        if (patient != null) {
            if(patient.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("participant.actor.reference").is(patient.getValue()),
                        Criteria.where("participant.actor.display").is(patient.getValue()));
            }else {
                String[] ref= patient.getValue().split("\\|");
                criteria.and("participant.actor.identifier.system").is(ref[0]).and("participant.actor.identifier.value").is(ref[1]);
            }
        }
        // practitioner
        if (practitioner != null) {
            if(practitioner.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("participant.actor.reference").is(practitioner.getValue()),
                        Criteria.where("participant.actor.display").is(practitioner.getValue()));
            }else {
                String[] ref= practitioner.getValue().split("\\|");
                criteria.and("participant.actor.identifier.system").is(ref[0]).and("participant.actor.identifier.value").is(ref[1]);
            }
        }
        // service-type
        if (serviceType != null) {
            criteria.and("serviceType").is(serviceType.getValue());
        }
        // status
        if (status != null) {
            criteria.and("status").is(status.getValue());
        }
        return criteria;
    }
}
