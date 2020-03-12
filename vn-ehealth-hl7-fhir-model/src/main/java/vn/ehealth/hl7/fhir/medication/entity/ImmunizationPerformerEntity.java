package vn.ehealth.hl7.fhir.medication.entity;

import org.hl7.fhir.r4.model.Immunization.ImmunizationPerformerComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class ImmunizationPerformerEntity {

    public BaseCodeableConcept function;
    public BaseReference actor;
    
    public static ImmunizationPerformerEntity fromImmunizationPerformerComponent(ImmunizationPerformerComponent obj) {
        if(obj == null) return null;
        
        var ent = new ImmunizationPerformerEntity();
        ent.function = BaseCodeableConcept.fromCodeableConcept(obj.getFunction());
        ent.actor = BaseReference.fromReference(obj.getActor());
        return ent;
    }
    
    public static ImmunizationPerformerComponent toImmunizationPerformerComponent(ImmunizationPerformerEntity ent) {
        if(ent == null) return null;
        
        var obj = new ImmunizationPerformerComponent();
        obj.setFunction(BaseCodeableConcept.toCodeableConcept(ent.function));
        obj.setActor(BaseReference.toReference(ent.actor));
        return obj;
    }
}
