package vn.ehealth.hl7.fhir.schedule.entity;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Appointment.AppointmentParticipantComponent;
import org.hl7.fhir.r4.model.Appointment.ParticipantRequired;
import org.hl7.fhir.r4.model.Appointment.ParticipationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "participant")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class ParticipantEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseCodeableConcept> type;
    public BaseReference actor;
    public String required;
    public String status;
    //public List<BasePeriod> requestedPeriod;
    BasePeriod period;
    public String appointmentEntityID;
    
    public static ParticipantEntity fromAppointmentParticipantComponent(AppointmentParticipantComponent obj) {
        if(obj == null) return null;
        var ent = new ParticipantEntity();
        ent.type = BaseCodeableConcept.fromCodeableConcept(obj.getType());
        ent.actor = BaseReference.fromReference(obj.getActor());
        ent.required = Optional.ofNullable(obj.getRequired()).map(x -> x.toCode()).orElse(null);
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.period = BasePeriod.fromPeriod(obj.getPeriod());
        return ent;
    }
    
    public static AppointmentParticipantComponent toAppointmentParticipantComponent(ParticipantEntity ent) {
        if(ent == null) return null;
        
        var obj = new AppointmentParticipantComponent();
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setActor(BaseReference.toReference(ent.actor));
        obj.setRequired(ParticipantRequired.fromCode(ent.required));
        obj.setStatus(ParticipationStatus.fromCode(ent.status));
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        return obj;
    }
}
