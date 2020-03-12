package vn.ehealth.hl7.fhir.clinical.entity;

import org.hl7.fhir.r4.model.Procedure.ProcedurePerformerComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class ProcedurePerformerEntity {

    public BaseCodeableConcept function;
    public BaseReference actor;
    public BaseReference onBehalfOf;
    
    public static ProcedurePerformerEntity fromProcedurePerformerComponent(ProcedurePerformerComponent obj) {
        if(obj == null) return null;
        
        var ent = new ProcedurePerformerEntity();
        
        ent.function = BaseCodeableConcept.fromCodeableConcept(obj.getFunction());
        ent.actor = BaseReference.fromReference(obj.getActor());
        ent.onBehalfOf = BaseReference.fromReference(obj.getOnBehalfOf());
        
        return ent;
    }
    
    public static ProcedurePerformerComponent toProcedurePerformerComponent(ProcedurePerformerEntity ent) {
        if(ent == null) return null;
        
        var obj = new ProcedurePerformerComponent();
        
        obj.setFunction(BaseCodeableConcept.toCodeableConcept(ent.function));
        obj.setActor(BaseReference.toReference(ent.actor));
        obj.setOnBehalfOf(BaseReference.toReference(ent.onBehalfOf));
        
        return obj;
    }
}
