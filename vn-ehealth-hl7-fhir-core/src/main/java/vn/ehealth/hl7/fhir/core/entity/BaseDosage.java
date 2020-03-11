package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Ratio;
import org.hl7.fhir.r4.model.Timing;
import org.hl7.fhir.r4.model.Type;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BaseDosage {
    public Integer sequence;
    public String text;
    public List<CodeableConcept> additionalInstruction;
    public String patientInstruction;
    public Timing timing;
    public Type asNeeded;
    public CodeableConcept site;
    public CodeableConcept route;
    public CodeableConcept method;
    //private Type dose;
    public Ratio maxDosePerPeriod;
    public BaseQuantity maxDosePerAdministration;
    public BaseQuantity maxDosePerLifetime;
    //private Type rate;
    List<BaseDosageDoseAndRate> doseAndRate;    
    public List<Extension> extension;
  
    
    public static BaseDosage fromDosage(Dosage object) {
        if(object == null) return null;
        
        var entity = new BaseDosage();
                
        entity.sequence = object.getSequence();
        entity.text = object.getText();
        entity.additionalInstruction = object.getAdditionalInstruction();
        entity.patientInstruction = object.getPatientInstruction();
        entity.timing = object.getTiming();
        entity.asNeeded = object.getAsNeeded();
        entity.site = object.getSite();
        entity.route = object.getRoute();
        entity.method = object.getMethod();
        entity.doseAndRate = transform(object.getDoseAndRate(), BaseDosageDoseAndRate::fromDosageDoseAndRateComponent);
        entity.maxDosePerPeriod = object.getMaxDosePerPeriod();
        entity.maxDosePerAdministration = BaseQuantity.fromQuantity(object.getMaxDosePerAdministration());
        entity.maxDosePerLifetime = BaseQuantity.fromQuantity(object.getMaxDosePerLifetime());
        entity.extension = object.getExtension();
        return entity;
    }
    
    public static List<BaseDosage> fromDosageList(List<Dosage> lst) {
        return transform(lst, x -> fromDosage(x));        
    }
    
    public static Dosage toDosage(BaseDosage entity) {
        if(entity == null) return null;
                
        var object = new Dosage();
        object.setSequence(entity.sequence);
        object.setText(entity.text);
        object.setAdditionalInstruction(entity.additionalInstruction);
        object.setPatientInstruction(entity.patientInstruction);
        object.setTiming(entity.timing);
        object.setAsNeeded(entity.asNeeded);
        object.setSite(entity.site);
        object.setRoute(entity.route);
        object.setMethod(entity.method);
        object.setDoseAndRate(transform(entity.doseAndRate, BaseDosageDoseAndRate::toDosageDoseAndRateComponent));
        object.setMaxDosePerPeriod(entity.maxDosePerPeriod);
        object.setMaxDosePerAdministration(BaseQuantity.toQuantity(entity.maxDosePerAdministration));
        object.setMaxDosePerLifetime(BaseQuantity.toQuantity(entity.maxDosePerLifetime));
        object.setExtension(entity.extension);
        return object;
    }
    
    public static List<Dosage> toDosageList(List<BaseDosage> entityList) {
        return transform(entityList, x -> toDosage(x));        
    }
    
}
