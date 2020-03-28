package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;

public class BaseIdentifier {

    public String system;

    public String value;

    //public Integer order;
    
    public BaseCodeableConcept type;
    
    public BasePeriod period;
    
    public BaseReference assigner;

    public IdentifierUse use;
    
    public List<Extension> extension;
      
}
