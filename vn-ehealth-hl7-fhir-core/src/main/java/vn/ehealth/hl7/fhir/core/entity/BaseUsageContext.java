package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseUsageContext {

    public BaseCoding code;
    @JsonIgnore public Type value;
    public List<Extension> extension;
    
}
