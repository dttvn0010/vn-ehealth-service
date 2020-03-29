package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseType {

    @JsonIgnore public List<SimpleExtension> extension;
    
}
