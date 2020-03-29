package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BaseResource {
    public Date resCreated;
    public Date resUpdated;
    public Date resDeleted;
    public String resMessage;
    public String resource;
    public String fhirVersion;
    public List<String> profile;
    public Integer version = 1;
    public boolean active;
    public List<BaseCoding> security;
    public List<BaseCoding> tag;
    @JsonIgnore public List<BaseExtension> extension;
    @JsonIgnore public List<BaseExtension> modifierExtension;
    public String fhirId;
}
