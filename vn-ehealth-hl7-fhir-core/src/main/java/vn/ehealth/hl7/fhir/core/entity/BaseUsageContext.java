package vn.ehealth.hl7.fhir.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseUsageContext extends BaseType {

    public BaseCoding code;
    @JsonIgnore public Object value;
}
