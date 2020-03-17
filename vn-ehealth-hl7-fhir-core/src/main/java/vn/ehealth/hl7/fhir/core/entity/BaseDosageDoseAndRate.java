package vn.ehealth.hl7.fhir.core.entity;

import org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Type;

public class BaseDosageDoseAndRate {

    public BaseCodeableConcept type;
    @JsonIgnore public Type dose;
    @JsonIgnore public Type rate;
    public List<Extension> extension;    
 
    public static BaseDosageDoseAndRate fromDosageDoseAndRateComponent(DosageDoseAndRateComponent obj) {
        if(obj == null) return null;
        
        var ent = new BaseDosageDoseAndRate();
        
        if(obj.hasType())
            ent.type = BaseCodeableConcept.fromCodeableConcept(obj.getType());
        
        if(obj.hasDose())
            ent.dose = obj.getDose();
        
        if(obj.hasRate())
            ent.rate = obj.getRate();
        
        if(obj.hasExtension())
            ent.extension = obj.getExtension();
        
        return ent;
    }    
    
    public static DosageDoseAndRateComponent toDosageDoseAndRateComponent(BaseDosageDoseAndRate ent) {
        if(ent == null) return null;
                
        var obj = new DosageDoseAndRateComponent();
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setDose(ent.dose);
        obj.setRate(ent.rate);
        obj.setExtension(ent.extension);
        return obj;
    }
}
