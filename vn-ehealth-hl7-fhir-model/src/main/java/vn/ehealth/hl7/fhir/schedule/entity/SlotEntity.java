package vn.ehealth.hl7.fhir.schedule.entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Slot;
import org.hl7.fhir.r4.model.Slot.SlotStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "slot")
public class SlotEntity extends BaseResource{
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseCodeableConcept> serviceCategory;
    public List<BaseCodeableConcept> serviceType;
    public List<BaseCodeableConcept> specialty;
    public BaseCodeableConcept appointmentType;
    public BaseReference schedule;
    public String status;
    public Date start;
    public Date end;
    public boolean overbooked;
    public String comment;
    
    public static SlotEntity fromSlot(Slot obj) {
        if(obj == null) return null;
        var ent = new SlotEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.serviceCategory = BaseCodeableConcept.fromCodeableConcept(obj.getServiceCategory());
        ent.serviceType = BaseCodeableConcept.fromCodeableConcept(obj.getServiceType());
        ent.specialty = BaseCodeableConcept.fromCodeableConcept(obj.getSpecialty());
        ent.appointmentType = BaseCodeableConcept.fromCodeableConcept(obj.getAppointmentType());
        ent.schedule = BaseReference.fromReference(obj.getSchedule());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.start = obj.getStart();
        ent.end = obj.getEnd();
        ent.overbooked = obj.getOverbooked();
        ent.comment = obj.getComment();
        return ent;
    }
    
    public static Slot toSlot(SlotEntity ent) {
        if(ent == null) return null;
        
        var obj = new Slot();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setServiceCategory(BaseCodeableConcept.toCodeableConcept(ent.serviceCategory));
        obj.setServiceType(BaseCodeableConcept.toCodeableConcept(ent.serviceType));
        obj.setSpecialty(BaseCodeableConcept.toCodeableConcept(ent.specialty));
        obj.setAppointmentType(BaseCodeableConcept.toCodeableConcept(ent.appointmentType));
        obj.setSchedule(BaseReference.toReference(ent.schedule));
        obj.setStatus(SlotStatus.fromCode(ent.status));
        obj.setStart(ent.start);
        obj.setEnd(ent.end);
        obj.setOverbooked(ent.overbooked);
        obj.setComment(ent.comment);
        return obj;
    }
}
