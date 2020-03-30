package vn.ehealth.hl7.fhir.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseContactPoint extends BaseComplexType  {

    public String system;
    public String value;
    public String use;
    public Integer rank;
    public BasePeriod period;
    
}
