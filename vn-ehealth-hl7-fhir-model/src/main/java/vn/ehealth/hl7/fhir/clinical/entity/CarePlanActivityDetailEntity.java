package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.CarePlan.CarePlanActivityDetailComponent;
import org.hl7.fhir.r4.model.CarePlan.CarePlanActivityStatus;

import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class CarePlanActivityDetailEntity {
    //public CodeableConcept category;
    //public BaseReference definition;
    public CodeableConcept code;
    public List<CodeableConcept> reasonCode;
    public List<BaseReference> reasonBaseReference;
    public List<BaseReference> goal;
    public String status;
    public CodeableConcept statusReason;
    //public Boolean prohibited;
    public Type scheduled;
    public BaseReference location;
    public List<BaseReference> performer;
    public Type product;
    public BaseQuantity dailyAmount;
    public BaseQuantity quantity;
    public String description;
    
    public static CarePlanActivityDetailEntity fromCarePlanActivityDetailComponent(CarePlanActivityDetailComponent obj) {
        if(obj == null) return null;
        
        var ent = new CarePlanActivityDetailEntity();
        
        ent.code = obj.getCode();
        ent.reasonCode = obj.getReasonCode();
        ent.reasonBaseReference = BaseReference.fromReferenceList(obj.getReasonReference());
        ent.goal = BaseReference.fromReferenceList(obj.getGoal());
        ent.status = obj.getStatus() != null? obj.getStatus().toCode() : null;
        ent.statusReason = obj.getStatusReason();
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
        
        obj.setCode(ent.code);
        obj.setReasonCode(ent.reasonCode);
        obj.setReasonReference(BaseReference.toReferenceList(ent.reasonBaseReference));
        obj.setGoal(BaseReference.toReferenceList(ent.goal));
        obj.setStatus(CarePlanActivityStatus.fromCode(ent.status));
        obj.setStatusReason(ent.statusReason);
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
