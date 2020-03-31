package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;

import vn.ehealth.hl7.fhir.core.view.DTOView;

@JsonInclude(Include.NON_NULL)
public abstract class BaseResource {
    public Date resCreated;
    public Date resUpdated;
    public Date resDeleted;
    public String resMessage;
    public String resource;
    public String fhirVersion;
    public List<String> profile;
    @JsonIgnore public Integer version = 1;
    @JsonIgnore public boolean active;
    public List<BaseCoding> security;
    public List<BaseCoding> tag;
    public List<BaseExtension> extension;
    public List<BaseExtension> modifierExtension;
    @JsonIgnore public String fhirId;
    
    @JsonView(DTOView.class)
    public Map<String, Object> getDto() {
        return new HashMap<>(); 
    }
}
