package vn.ehealth.hl7.fhir.medication.entity;

import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;

import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestSubstitutionComponent;



public class MedicationRequestSubstitutionEntity{
    @JsonIgnore public Type allowed;
    public BaseCodeableConcept reason;
    
    public static MedicationRequestSubstitutionEntity fromMedicationRequestSubstitutionComponent(MedicationRequestSubstitutionComponent obj) {
        if(obj == null) return null;
        var ent = new MedicationRequestSubstitutionEntity();
        ent.allowed = obj.getAllowed();
        ent.reason = BaseCodeableConcept.fromCodeableConcept(obj.getReason());
        return ent;
    }
    
    public static MedicationRequestSubstitutionComponent toMedicationRequestSubstitutionComponent(MedicationRequestSubstitutionEntity ent) {
        if(ent == null) return null;
        
        var obj = new MedicationRequestSubstitutionComponent();
        obj.setAllowed(ent.allowed);
        obj.setReason(BaseCodeableConcept.toCodeableConcept(ent.reason));
        return obj;
    }
}
