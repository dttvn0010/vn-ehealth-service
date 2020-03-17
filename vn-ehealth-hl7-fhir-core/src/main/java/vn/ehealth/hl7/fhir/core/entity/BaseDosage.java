package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BaseDosage {
    public Integer sequence;
    public String text;
    public List<BaseCodeableConcept> additionalInstruction;
    public String patientInstruction;
    public BaseTiming timing;
    @JsonIgnore public Type asNeeded;
    public BaseCodeableConcept site;
    public BaseCodeableConcept route;
    public BaseCodeableConcept method;
    //private Type dose;
    public BaseRatio maxDosePerPeriod;
    public BaseQuantity maxDosePerAdministration;
    public BaseQuantity maxDosePerLifetime;
    //private Type rate;
    List<BaseDosageDoseAndRate> doseAndRate;    
    public List<Extension> extension;
  
    
    public static BaseDosage fromDosage(Dosage obj) {
        if(obj == null) return null;
        
        var ent = new BaseDosage();
        
        if(obj.hasSequence())                
            ent.sequence = obj.getSequence();
        
        if(obj.hasText())
            ent.text = obj.getText();
        
        if(obj.hasAdditionalInstruction())
            ent.additionalInstruction = BaseCodeableConcept.fromCodeableConcept(obj.getAdditionalInstruction());
        
        if(obj.hasPatientInstruction())
            ent.patientInstruction = obj.getPatientInstruction();
        
        if(obj.hasTiming())
            ent.timing = BaseTiming.fromTiming(obj.getTiming());
        
        if(obj.hasAsNeeded())
            ent.asNeeded = obj.getAsNeeded();
        
        if(obj.hasSite())
            ent.site = BaseCodeableConcept.fromCodeableConcept(obj.getSite());
        
        if(obj.hasRoute())
            ent.route = BaseCodeableConcept.fromCodeableConcept(obj.getRoute());
        
        if(obj.hasMethod())
            ent.method = BaseCodeableConcept.fromCodeableConcept(obj.getMethod());
        
        if(obj.hasDoseAndRate())
            ent.doseAndRate = transform(obj.getDoseAndRate(), BaseDosageDoseAndRate::fromDosageDoseAndRateComponent);
        
        if(obj.hasMaxDosePerPeriod())
            ent.maxDosePerPeriod = BaseRatio.fromRation(obj.getMaxDosePerPeriod());
        
        if(obj.hasMaxDosePerLifetime())
            ent.maxDosePerAdministration = BaseQuantity.fromQuantity(obj.getMaxDosePerAdministration());
        
        if(obj.hasMaxDosePerLifetime())
            ent.maxDosePerLifetime = BaseQuantity.fromQuantity(obj.getMaxDosePerLifetime());
        
        if(obj.hasExtension())
            ent.extension = obj.getExtension();
                
        return ent;
    }
    
    public static List<BaseDosage> fromDosageList(List<Dosage> lst) {
        return transform(lst, x -> fromDosage(x));        
    }
    
    public static Dosage toDosage(BaseDosage entity) {
        if(entity == null) return null;
                
        var object = new Dosage();
        object.setSequence(entity.sequence);
        object.setText(entity.text);
        object.setAdditionalInstruction(BaseCodeableConcept.toCodeableConcept(entity.additionalInstruction));
        object.setPatientInstruction(entity.patientInstruction);
        object.setTiming(BaseTiming.toTiming(entity.timing));
        object.setAsNeeded(entity.asNeeded);
        object.setSite(BaseCodeableConcept.toCodeableConcept(entity.site));
        object.setRoute(BaseCodeableConcept.toCodeableConcept(entity.route));
        object.setMethod(BaseCodeableConcept.toCodeableConcept(entity.method));
        object.setDoseAndRate(transform(entity.doseAndRate, BaseDosageDoseAndRate::toDosageDoseAndRateComponent));
        object.setMaxDosePerPeriod(BaseRatio.toRatio(entity.maxDosePerPeriod));
        object.setMaxDosePerAdministration(BaseQuantity.toQuantity(entity.maxDosePerAdministration));
        object.setMaxDosePerLifetime(BaseQuantity.toQuantity(entity.maxDosePerLifetime));
        object.setExtension(entity.extension);
        return object;
    }
    
    public static List<Dosage> toDosageList(List<BaseDosage> entityList) {
        return transform(entityList, x -> toDosage(x));        
    }
    
}
