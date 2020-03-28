package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;

public class BaseRange {
    public BaseQuantity low;
    public BaseQuantity high;
    public List<Extension> extension;
   
}
