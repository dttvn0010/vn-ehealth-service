package vn.ehealth.hl7.fhir.clinical.entity;

import org.hl7.fhir.r4.model.Procedure.ProcedureFocalDeviceComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class ProcedureFocalDeviceEntity {

    public BaseCodeableConcept action;
    public BaseReference manipulated;
    
    public static ProcedureFocalDeviceEntity fromProcedureFocalDeviceComponent(ProcedureFocalDeviceComponent obj) {
        if(obj == null) return null;
        
        var ent = new ProcedureFocalDeviceEntity();
        
        ent.action = BaseCodeableConcept.fromCodeableConcept(obj.getAction());
        ent.manipulated = BaseReference.fromReference(obj.getManipulated());
        
        return ent;
    }
    
    public static ProcedureFocalDeviceComponent toProcedureFocalDeviceComponent(ProcedureFocalDeviceEntity ent) {
        if(ent == null) return null;
        
        var obj = new ProcedureFocalDeviceComponent();
        
        obj.setAction(BaseCodeableConcept.toCodeableConcept(ent.action));
        obj.setManipulated(BaseReference.toReference(ent.manipulated));
        
        return obj;
    }
}
