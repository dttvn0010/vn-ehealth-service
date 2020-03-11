package vn.ehealth.hl7.fhir.core.entity;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent;
import org.hl7.fhir.r4.model.Type;

public class BaseDosageDoseAndRate {

    public CodeableConcept type;
    public Type dose;
    public Type rate;
 
    public static BaseDosageDoseAndRate fromDosageDoseAndRateComponent(DosageDoseAndRateComponent item) {
        if(item == null) return null;
        
        var entity = new BaseDosageDoseAndRate();
        entity.type = item.getType();
        entity.dose = item.getDose();
        entity.rate = item.getRate();
        
        return entity;
    }    
    
    public static DosageDoseAndRateComponent toDosageDoseAndRateComponent(BaseDosageDoseAndRate entity) {
        if(entity == null) return null;
                
        var item = new DosageDoseAndRateComponent();
        item.setType(entity.type);
        item.setDose(entity.dose);
        item.setRate(entity.rate);
        return item;
    }
}
