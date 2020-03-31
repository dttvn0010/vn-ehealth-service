package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseDosage extends BaseComplexType {
    
    @JsonInclude(Include.NON_NULL)
    public static class BaseDosageDoseAndRate {

        public BaseCodeableConcept type;
        public BaseSimpleType dose;
        public BaseSimpleType rate;
        public List<SimpleExtension> extension;
    }
        
    public Integer sequence;
    public String text;
    public List<BaseCodeableConcept> additionalInstruction;
    public String patientInstruction;
    public BaseTiming timing;
    public BaseSimpleType asNeeded;
    public BaseCodeableConcept site;
    public BaseCodeableConcept route;
    public BaseCodeableConcept method;
    public List<BaseDosageDoseAndRate> doseAndRate;
    public BaseRatio maxDosePerPeriod;
    public BaseQuantity maxDosePerAdministration;
    public BaseQuantity maxDosePerLifetime;
    
}
