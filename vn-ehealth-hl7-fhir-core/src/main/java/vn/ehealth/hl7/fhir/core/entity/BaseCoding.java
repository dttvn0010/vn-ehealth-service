package vn.ehealth.hl7.fhir.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseCoding extends BaseSimpleType  {

    public String system;
    public String version;
    public String code;
    public String display;
    public Boolean userSelected;
    
}
