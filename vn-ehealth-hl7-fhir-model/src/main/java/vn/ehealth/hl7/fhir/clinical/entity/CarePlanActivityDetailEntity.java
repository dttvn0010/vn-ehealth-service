package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hl7.fhir.r4.model.CarePlan.CarePlanActivityDetailComponent;
import org.hl7.fhir.r4.model.CarePlan.CarePlanActivityStatus;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class CarePlanActivityDetailEntity {
    //public CodeableConcept category;
    //public BaseReference definition;
    public BaseCodeableConcept code;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonBaseReference;
    public List<BaseReference> goal;
    public String status;
    public BaseCodeableConcept statusReason;
    //public Boolean prohibited;
    @JsonIgnore public Type scheduled;
    public BaseReference location;
    public List<BaseReference> performer;
    @JsonIgnore public Type product;
    public BaseQuantity dailyAmount;
    public BaseQuantity quantity;
    public String description;
    
    public static CarePlanActivityDetailEntity fromCarePlanActivityDetailComponent(CarePlanActivityDetailComponent obj) {
        if(obj == null) return null;
        
        var ent = new CarePlanActivityDetailEntity();
        
        ent.code = BaseCodeableConcept.fromCodeableConcept(obj.getCode());
        ent.reasonCode = BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode());
        ent.reasonBaseReference = BaseReference.fromReferenceList(obj.getReasonReference());
        ent.goal = BaseReference.fromReferenceList(obj.getGoal());
        ent.status = obj.getStatus() != null? obj.getStatus().toCode() : null;
        ent.statusReason = BaseCodeableConcept.fromCodeableConcept(obj.getStatusReason());
        ent.scheduled = obj.getScheduled();
        ent.location = BaseReference.fromReference(obj.getLocation());
        ent.performer = BaseReference.fromReferenceList(obj.getPerformer());
        ent.product = obj.getProduct();
        ent.dailyAmount = BaseQuantity.fromQuantity(obj.getDailyAmount());
        ent.quantity = BaseQuantity.fromQuantity(obj.getQuantity());
        ent.description = obj.getDescription();
        
        return ent;
    }
    
    public static CarePlanActivityDetailComponent toCarePlanActivityDetailComponent(CarePlanActivityDetailEntity ent) {
        if(ent == null) return null;
        
        var obj = new CarePlanActivityDetailComponent();
        
        obj.setCode(BaseCodeableConcept.toCodeableConcept(ent.code));
        obj.setReasonCode(BaseCodeableConcept.toCodeableConcept(ent.reasonCode));
        obj.setReasonReference(BaseReference.toReferenceList(ent.reasonBaseReference));
        obj.setGoal(BaseReference.toReferenceList(ent.goal));
        obj.setStatus(CarePlanActivityStatus.fromCode(ent.status));
        obj.setStatusReason(BaseCodeableConcept.toCodeableConcept(ent.statusReason));
        obj.setScheduled(ent.scheduled);
        obj.setLocation(BaseReference.toReference(ent.location));
        obj.setPerformer(BaseReference.toReferenceList(ent.performer));
        obj.setProduct(ent.product);
        obj.setDailyAmount(BaseQuantity.toQuantity(ent.dailyAmount));
        obj.setQuantity(BaseQuantity.toQuantity(ent.quantity));
        obj.setDescription(ent.description);
        
        return obj;
    }
}
