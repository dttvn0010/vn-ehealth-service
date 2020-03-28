package vn.ehealth.hl7.fhir.term.entity;

import org.hl7.fhir.r4.model.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ValueSetExpansionParameterEntity {

    
    public String name;
    @JsonIgnore public Type value;
    
}
