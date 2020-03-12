package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.List;

import org.hl7.fhir.r4.model.CarePlan.CarePlanActivityComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class CarePlanActivityEntity {
    
    public List<BaseCodeableConcept> outcomeCodeableConcept;
    public List<BaseReference> outcomeReference;
    public List<BaseAnnotation> progress;
    public BaseReference reference;
    public CarePlanActivityDetailEntity detail;
    
    
    public static CarePlanActivityEntity fromCarePlanActivityComponent(CarePlanActivityComponent obj) {
        if(obj == null) return null;
                
        var ent = new CarePlanActivityEntity();
        ent.outcomeCodeableConcept = BaseCodeableConcept.fromCodeableConcept(obj.getOutcomeCodeableConcept());
        ent.outcomeReference = BaseReference.fromReferenceList(obj.getOutcomeReference());
        ent.progress = BaseAnnotation.fromAnnotationList(obj.getProgress());
        ent.reference = BaseReference.fromReference(obj.getReference());
        ent.detail = CarePlanActivityDetailEntity.fromCarePlanActivityDetailComponent(obj.getDetail());
        
        return ent;
    }
    
    public static CarePlanActivityComponent toCarePlanActivityComponent(CarePlanActivityEntity ent) {
        if(ent == null) return null;
        
        var obj = new CarePlanActivityComponent();
        
        obj.setOutcomeCodeableConcept(BaseCodeableConcept.toCodeableConcept(ent.outcomeCodeableConcept));
        obj.setOutcomeReference(BaseReference.toReferenceList(ent.outcomeReference));
        obj.setProgress(BaseAnnotation.toAnnotationList(ent.progress));
        obj.setReference(BaseReference.toReference(ent.reference));
        obj.setDetail(CarePlanActivityDetailEntity.toCarePlanActivityDetailComponent(ent.detail));
        
        return obj;                
    }
    
}
