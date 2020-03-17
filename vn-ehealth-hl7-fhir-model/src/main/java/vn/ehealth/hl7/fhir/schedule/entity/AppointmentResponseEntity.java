package vn.ehealth.hl7.fhir.schedule.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.AppointmentResponse;
import org.hl7.fhir.r4.model.AppointmentResponse.ParticipantStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "appointmentResponse")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class AppointmentResponseEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public BaseReference appointment;
    public Date start;
    public Date end;
    public List<BaseCodeableConcept> participantType;
    public BaseReference actor;
    public ParticipantStatus participantStatus;
    public String comment;
    
    public static AppointmentResponseEntity from(AppointmentResponse obj) {
        if(obj == null) return null;
        
        var ent = new AppointmentResponseEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.appointment = BaseReference.fromReference(obj.getAppointment());
        ent.start = obj.getStart();
        ent.end = obj.getEnd();
        ent.participantType = BaseCodeableConcept.fromCodeableConcept(obj.getParticipantType());
        ent.actor = BaseReference.fromReference(obj.getActor());
        ent.participantStatus = obj.getParticipantStatus();
        ent.comment = obj.getComment();
        return ent;
    }
    
    public static AppointmentResponse toAppointmentResponse(AppointmentResponseEntity ent) {
        if(ent == null) return null;
        var obj = new AppointmentResponse();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setAppointment(BaseReference.toReference(ent.appointment));
        obj.setStart(ent.start);
        obj.setEnd(ent.end);
        obj.setParticipantType(BaseCodeableConcept.toCodeableConcept(ent.participantType));
        obj.setActor(BaseReference.toReference(ent.actor));
        obj.setParticipantStatus(ent.participantStatus);
        obj.setComment(ent.comment);
        return obj;
    }
}
