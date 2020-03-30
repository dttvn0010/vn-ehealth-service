package vn.ehealth.hl7.fhir.core.entity;

import org.hl7.fhir.r4.model.Identifier.IdentifierUse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseIdentifier extends BaseComplexType {

    public String system;

    public String value;
    
    public BaseCodeableConcept type;
    
    public BasePeriod period;
    
    public BaseReference assigner;

    public IdentifierUse use;
      
}
