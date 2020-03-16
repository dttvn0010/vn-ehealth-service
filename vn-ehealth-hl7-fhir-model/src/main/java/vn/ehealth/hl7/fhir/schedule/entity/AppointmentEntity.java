package vn.ehealth.hl7.fhir.schedule.entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Appointment;
import org.hl7.fhir.r4.model.Appointment.AppointmentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "appointment")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class AppointmentEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public List<BaseCodeableConcept> serviceCategory;
    public List<BaseCodeableConcept> serviceType;
    public List<BaseCodeableConcept> specialty;
    public BaseCodeableConcept appointmentType;
    public List<BaseCodeableConcept> reasonCode;
    //    public List<BaseReference> indication;
    public int priority;
    public String description;
    public List<BaseReference> supportingInformation;
    public Date start;
    public Date end;
    public int minutesDuration;
    public List<BaseReference> slot;
    public Date created;
    public String comment;
    //public List<BaseReference> incomingReferral;
    public List<ParticipantEntity> participant;
    public List<BasePeriod> requestedPeriod;
    
    public static AppointmentEntity fromAppointment(Appointment obj) {
        if(obj == null) return null;
        var ent = new AppointmentEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.serviceCategory = BaseCodeableConcept.fromCodeableConcept(obj.getServiceCategory());
        ent.serviceType = BaseCodeableConcept.fromCodeableConcept(obj.getServiceType());
        ent.specialty = BaseCodeableConcept.fromCodeableConcept(obj.getSpecialty());
        ent.appointmentType = BaseCodeableConcept.fromCodeableConcept(obj.getAppointmentType());
        ent.reasonCode = BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode());
        ent.priority = obj.getPriority();
        ent.description = obj.getDescription();
        ent.supportingInformation = BaseReference.fromReferenceList(obj.getSupportingInformation());
        ent.start = obj.getStart();
        ent.end = obj.getEnd();
        ent.minutesDuration = obj.getMinutesDuration();
        ent.slot = BaseReference.fromReferenceList(obj.getSlot());
        ent.comment = obj.getComment();
        ent.participant = transform(obj.getParticipant(), ParticipantEntity::fromAppointmentParticipantComponent);
        ent.requestedPeriod = BasePeriod.fromPeriodList(obj.getRequestedPeriod());
        return ent;
    }
    
    public static Appointment toAppointment(AppointmentEntity ent) {
        if(ent == null) return null;
        var obj = new Appointment();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setStatus(AppointmentStatus.fromCode(ent.status));
        obj.setServiceCategory(BaseCodeableConcept.toCodeableConcept(ent.serviceCategory));
        obj.setServiceType(BaseCodeableConcept.toCodeableConcept(ent.serviceType));
        obj.setSpecialty(BaseCodeableConcept.toCodeableConcept(ent.specialty));
        obj.setAppointmentType(BaseCodeableConcept.toCodeableConcept(ent.appointmentType));
        obj.setReasonCode(BaseCodeableConcept.toCodeableConcept(ent.reasonCode));
        obj.setPriority(ent.priority);
        obj.setDescription(ent.description);
        obj.setSupportingInformation(BaseReference.toReferenceList(ent.supportingInformation));
        obj.setStart(ent.start);
        obj.setEnd(ent.end);
        obj.setMinutesDuration(ent.minutesDuration);
        obj.setSlot(BaseReference.toReferenceList(ent.slot));
        obj.setParticipant(transform(ent.participant, ParticipantEntity::toAppointmentParticipantComponent));
        obj.setRequestedPeriod(BasePeriod.toPeriodList(ent.requestedPeriod));
        return obj;
    }
}
