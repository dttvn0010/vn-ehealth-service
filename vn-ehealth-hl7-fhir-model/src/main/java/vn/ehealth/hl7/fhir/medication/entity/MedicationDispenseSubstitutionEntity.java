package vn.ehealth.hl7.fhir.medication.entity;

import java.util.List;



import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseSubstitutionComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class MedicationDispenseSubstitutionEntity {
    public boolean wasSubstituted;
    public CodeableConcept type;
    public List<CodeableConcept> reason;
    public List<BaseReference> responsibleParty;
    
    public static MedicationDispenseSubstitutionEntity fromMedicationDispenseSubstitutionComponent(MedicationDispenseSubstitutionComponent obj) {
        if(obj == null) return null;
        var ent = new MedicationDispenseSubstitutionEntity();
        ent.wasSubstituted = obj.getWasSubstituted();
        ent.type = obj.getType();
        ent.reason = obj.getReason();
        ent.responsibleParty = BaseReference.fromReferenceList(obj.getResponsibleParty());
        return ent;
    }
    
    public static MedicationDispenseSubstitutionComponent toMedicationDispenseSubstitutionComponent(MedicationDispenseSubstitutionEntity ent) {
        if(ent == null) return null;
        var obj = new MedicationDispenseSubstitutionComponent();
        obj.setWasSubstituted(ent.wasSubstituted);
        obj.setType(ent.type);
        obj.setReason(ent.reason);
        obj.setResponsibleParty(BaseReference.toReferenceList(ent.responsibleParty));
        return obj;
    }

}
