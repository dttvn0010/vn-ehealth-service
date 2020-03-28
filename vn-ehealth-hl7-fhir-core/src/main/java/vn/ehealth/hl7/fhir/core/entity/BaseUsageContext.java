package vn.ehealth.hl7.fhir.core.entity;

import org.hl7.fhir.r4.model.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseUsageContext extends BaseType {

    public BaseCoding code;
    @JsonIgnore public Type value;
}
