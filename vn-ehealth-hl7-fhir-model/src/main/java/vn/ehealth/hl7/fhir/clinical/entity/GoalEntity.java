package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.Date;


import java.util.List;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "goal")
public class GoalEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    //public String status;
    public List<BaseCodeableConcept> category;
    public BaseCodeableConcept priority;
    public BaseCodeableConcept description;
    public BaseReference subject;
    @JsonIgnore public Type start;
    /** target **/
    public Date statusDate;
    public String statusReason;
    public BaseReference expressedBy;
    public List<BaseReference> addresses;
    public List<BaseAnnotation> note;
    public List<BaseCodeableConcept> outcomeCode;
    public List<BaseReference> outcomeReference;
    
    public static GoalEntity fromGoalEntity(Goal obj) {
        if(obj == null) return null;
        
        var ent = new GoalEntity();
        
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.category = BaseCodeableConcept.fromCodeableConcept(obj.getCategory());
        ent.priority = BaseCodeableConcept.fromCodeableConcept(obj.getPriority());
        ent.description = BaseCodeableConcept.fromCodeableConcept(obj.getDescription());
        ent.subject = BaseReference.fromReference(obj.getSubject());
        ent.start = obj.getStart();
        ent.statusDate = obj.getStatusDate();
        ent.statusReason = obj.getStatusReason();
        ent.addresses = BaseReference.fromReferenceList(obj.getAddresses());
        ent.note = BaseAnnotation.fromAnnotationList(obj.getNote());
        ent.outcomeCode = BaseCodeableConcept.fromCodeableConcept(obj.getOutcomeCode());
        ent.outcomeReference = BaseReference.fromReferenceList(obj.getOutcomeReference());
        
        return ent;
    }
    
    public static Goal toGoal(GoalEntity ent) {
        if(ent == null) return null;
        
        var obj = new Goal();
        
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setCategory(BaseCodeableConcept.toCodeableConcept(ent.category));
        obj.setPriority(BaseCodeableConcept.toCodeableConcept(ent.priority));
        obj.setDescription(BaseCodeableConcept.toCodeableConcept(ent.description));
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setStart(ent.start);
        obj.setStatusDate(ent.statusDate);
        obj.setStatusReason(ent.statusReason);
        obj.setAddresses(BaseReference.toReferenceList(ent.addresses));
        obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
        obj.setOutcomeCode(BaseCodeableConcept.toCodeableConcept(ent.outcomeCode));
        obj.setOutcomeReference(BaseReference.toReferenceList(ent.outcomeReference));
        
        return obj;
    }
}
