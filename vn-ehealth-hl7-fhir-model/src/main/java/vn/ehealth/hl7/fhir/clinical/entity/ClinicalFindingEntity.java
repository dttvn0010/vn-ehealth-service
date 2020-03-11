package vn.ehealth.hl7.fhir.clinical.entity;

import org.hl7.fhir.r4.model.ClinicalImpression.ClinicalImpressionFindingComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseReference;



public class ClinicalFindingEntity {

    public BaseReference itemReference;
    public String basis;
    
    public static ClinicalFindingEntity fromClinicalImpressionFindingComponent(ClinicalImpressionFindingComponent obj) {
        if(obj == null) return null;
        
        var ent = new ClinicalFindingEntity();
        ent.itemReference = BaseReference.fromReference(obj.getItemReference());
        ent.basis = obj.getBasis();
        
        return ent;
    }
    
    public static ClinicalImpressionFindingComponent toClinicalImpressionFindingComponent(ClinicalFindingEntity ent) {
        if(ent == null) return null;
        
        var obj = new ClinicalImpressionFindingComponent();
        obj.setItemReference(BaseReference.toReference(ent.itemReference));
        obj.setBasis(ent.basis);
        
        return obj;
    }
}
