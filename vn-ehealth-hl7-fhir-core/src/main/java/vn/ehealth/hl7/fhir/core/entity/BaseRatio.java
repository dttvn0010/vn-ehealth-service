package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;

public class BaseRatio {

    public BaseQuantity numerator;
    public BaseQuantity denominator;
    public List<Extension> extension;
    
}
