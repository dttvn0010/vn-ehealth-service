package vn.ehealth.hl7.fhir.core.entity;

import org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent;
import org.hl7.fhir.r4.model.Type;

public class BaseDosageDoseAndRate {

    public BaseCodeableConcept type;
    public Type dose;
    public Type rate;
 
    public static BaseDosageDoseAndRate fromDosageDoseAndRateComponent(DosageDoseAndRateComponent obj) {
        if(obj == null) return null;
        
        var ent = new BaseDosageDoseAndRate();
        
        if(obj.hasType())
            ent.type = BaseCodeableConcept.fromCodeableConcept(obj.getType());
        
        if(obj.hasDose())
            ent.dose = obj.getDose();
        
        if(obj.hasRate())
            ent.rate = obj.getRate();
        
        return ent;
    }    
    
    public static DosageDoseAndRateComponent toDosageDoseAndRateComponent(BaseDosageDoseAndRate ent) {
        if(ent == null) return null;
                
        var obj = new DosageDoseAndRateComponent();
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setDose(ent.dose);
        obj.setRate(ent.rate);
        return obj;
    }
}
