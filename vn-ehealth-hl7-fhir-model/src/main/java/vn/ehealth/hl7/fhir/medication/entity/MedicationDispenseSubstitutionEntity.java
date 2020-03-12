package vn.ehealth.hl7.fhir.medication.entity;

import java.util.List;

import org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseSubstitutionComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class MedicationDispenseSubstitutionEntity {
    public boolean wasSubstituted;
    public BaseCodeableConcept type;
    public List<BaseCodeableConcept> reason;
    public List<BaseReference> responsibleParty;
    
    public static MedicationDispenseSubstitutionEntity fromMedicationDispenseSubstitutionComponent(MedicationDispenseSubstitutionComponent obj) {
        if(obj == null) return null;
        var ent = new MedicationDispenseSubstitutionEntity();
        ent.wasSubstituted = obj.getWasSubstituted();
        ent.type = BaseCodeableConcept.fromCodeableConcept( obj.getType());
        ent.reason = BaseCodeableConcept.fromCodeableConcept(obj.getReason());
        ent.responsibleParty = BaseReference.fromReferenceList(obj.getResponsibleParty());
        return ent;
    }
    
    public static MedicationDispenseSubstitutionComponent toMedicationDispenseSubstitutionComponent(MedicationDispenseSubstitutionEntity ent) {
        if(ent == null) return null;
        var obj = new MedicationDispenseSubstitutionComponent();
        obj.setWasSubstituted(ent.wasSubstituted);
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setReason(BaseCodeableConcept.toCodeableConcept(ent.reason));
        obj.setResponsibleParty(BaseReference.toReferenceList(ent.responsibleParty));
        return obj;
    }

}
