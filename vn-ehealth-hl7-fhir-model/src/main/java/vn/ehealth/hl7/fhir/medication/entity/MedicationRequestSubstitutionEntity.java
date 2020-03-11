package vn.ehealth.hl7.fhir.medication.entity;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestSubstitutionComponent;



public class MedicationRequestSubstitutionEntity{
    public Type allowed;
    public CodeableConcept reason;
    
    public static MedicationRequestSubstitutionEntity fromMedicationRequestSubstitutionComponent(MedicationRequestSubstitutionComponent obj) {
        if(obj == null) return null;
        var ent = new MedicationRequestSubstitutionEntity();
        ent.allowed = obj.getAllowed();
        ent.reason = obj.getReason();
        return ent;
    }
    
    public static MedicationRequestSubstitutionComponent toMedicationRequestSubstitutionComponent(MedicationRequestSubstitutionEntity ent) {
        if(ent == null) return null;
        
        var obj = new MedicationRequestSubstitutionComponent();
        obj.setAllowed(ent.allowed);
        obj.setReason(ent.reason);
        return obj;
    }
}
