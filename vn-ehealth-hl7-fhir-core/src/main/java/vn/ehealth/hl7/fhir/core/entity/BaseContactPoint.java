package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;

public class BaseContactPoint {

    public String system;
    public String value;
    public String use;
    public Integer rank;
    public BasePeriod period;
    public List<Extension> extension;
    
}
