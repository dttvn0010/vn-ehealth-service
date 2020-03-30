package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
}
