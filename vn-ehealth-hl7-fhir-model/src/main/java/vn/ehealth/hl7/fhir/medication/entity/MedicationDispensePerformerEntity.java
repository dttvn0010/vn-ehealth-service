package vn.ehealth.hl7.fhir.medication.entity;

import org.hl7.fhir.r4.model.MedicationDispense.MedicationDispensePerformerComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;


public class MedicationDispensePerformerEntity {
    public BaseReference actor;
    public BaseCodeableConcept function;
    
    public static MedicationDispensePerformerEntity fromMedicationDispensePerformerComponent(MedicationDispensePerformerComponent obj) {
        if(obj == null) return null;
        var ent = new MedicationDispensePerformerEntity();
        ent.actor = BaseReference.fromReference(obj.getActor());
        ent.function = BaseCodeableConcept.fromCodeableConcept(obj.getFunction());
        return ent;
    }
    public static MedicationDispensePerformerComponent toMedicationDispensePerformerComponent(MedicationDispensePerformerEntity ent) {
        if(ent == null) return null;
        var obj = new MedicationDispensePerformerComponent();
        obj.setActor(BaseReference.toReference(ent.actor));
        obj.setFunction(BaseCodeableConcept.toCodeableConcept(ent.function));
        return obj;
    }
}
