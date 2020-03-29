package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseDosage extends BaseType {
    
    public static class BaseDosageDoseAndRate {

        public BaseCodeableConcept type;
        @JsonIgnore public Type dose;
        @JsonIgnore public Type rate;
        public List<Extension> extension;
    }
    
    
    public Integer sequence;
    public String text;
    public List<BaseCodeableConcept> additionalInstruction;
    public String patientInstruction;
    public BaseTiming timing;
    @JsonIgnore public Type asNeeded;
    public BaseCodeableConcept site;
    public BaseCodeableConcept route;
    public BaseCodeableConcept method;
    public List<BaseDosageDoseAndRate> doseAndRate;
    public BaseRatio maxDosePerPeriod;
    public BaseQuantity maxDosePerAdministration;
    public BaseQuantity maxDosePerLifetime;
    
}
