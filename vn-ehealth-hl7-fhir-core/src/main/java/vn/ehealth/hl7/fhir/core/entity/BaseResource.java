package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.List;

public abstract class BaseResource {
    public Date resCreated;
    public Date resUpdated;
    public Date resDeleted;
    public String resMessage;
    public String resource;
    public BaseGeneratedText text;
    public List<String> profile;
    public Integer version = 1;
    public boolean active;
    public List<BaseCoding> security;
    public List<BaseCoding> tag;
    public List<BaseExtension> extension;
    public List<BaseExtension> modifierExtension;
    public String fhir_id;
}
