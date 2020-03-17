package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.Extension;

public abstract class BaseResource {
    public Date resCreated;
    public Date resUpdated;
    public Date resDeleted;
    public String resMessage;
    public String resource;
    public List<String> profile;
    public Integer version = 1;
    public boolean active;
    public List<BaseCoding> security;
    public List<BaseCoding> tag;
    public List<Extension> extension;
    public List<Extension> modifierExtension;
    public String fhirId;
}
