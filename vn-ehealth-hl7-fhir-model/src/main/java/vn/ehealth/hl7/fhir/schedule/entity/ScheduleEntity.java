package vn.ehealth.hl7.fhir.schedule.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Schedule;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "schedule")
public class ScheduleEntity extends BaseResource {
    @Id
    public ObjectId id;

    public List<BaseIdentifier> identifier;
    public List<BaseCodeableConcept> serviceCategory;
    public List<BaseCodeableConcept> serviceType;
    public List<BaseCodeableConcept> specialty;
    public List<BaseReference> actor;
    public BasePeriod planningHorizon;
    public String comment;
    
    public static ScheduleEntity fromSchedule(Schedule obj) {
        if(obj == null) return null;
        var ent = new ScheduleEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.serviceCategory = BaseCodeableConcept.fromCodeableConcept(obj.getServiceCategory());
        ent.serviceType = BaseCodeableConcept.fromCodeableConcept(obj.getServiceType());
        ent.specialty = BaseCodeableConcept.fromCodeableConcept(obj.getSpecialty());
        ent.actor = BaseReference.fromReferenceList(obj.getActor());
        ent.planningHorizon = BasePeriod.fromPeriod(obj.getPlanningHorizon());
        ent.comment = obj.getComment();
        return ent;
    }
    
    public static Schedule toSchedule(ScheduleEntity ent) {
        if(ent == null) return null;
        var obj = new Schedule();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setServiceCategory(BaseCodeableConcept.toCodeableConcept(ent.serviceCategory));
        obj.setServiceType(BaseCodeableConcept.toCodeableConcept(ent.serviceType));
        obj.setSpecialty(BaseCodeableConcept.toCodeableConcept(ent.specialty));
        obj.setActor(BaseReference.toReferenceList(ent.actor));
        obj.setPlanningHorizon(BasePeriod.toPeriod(ent.planningHorizon));
        obj.setComment(ent.comment);
        return obj;
    }
}
