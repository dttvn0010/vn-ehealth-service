package vn.ehealth.hl7.fhir.core.entity;

public class BaseContactPoint extends BaseComplexType  {

    public String system;
    public String value;
    public String use;
    public Integer rank;
    public BasePeriod period;
    
}
