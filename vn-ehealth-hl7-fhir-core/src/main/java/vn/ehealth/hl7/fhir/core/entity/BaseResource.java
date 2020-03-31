package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public abstract class BaseResource {
    @JsonIgnore public Date resCreated;
    @JsonIgnore public Date resUpdated;
    @JsonIgnore public Date resDeleted;
    @JsonIgnore public String resMessage;
    public String resource;
    @JsonIgnore public String fhirVersion;
    public List<String> profile;
    @JsonIgnore public Integer version = 1;
    @JsonIgnore public boolean active;
    public List<BaseCoding> security;
    public List<BaseCoding> tag;
    public List<BaseExtension> extension;
    public List<BaseExtension> modifierExtension;
    @JsonIgnore public String fhirId;
    
    public Map<String, Object> getDto(Map<String, Object> options) {
        return new HashMap<>(); 
    }
}
