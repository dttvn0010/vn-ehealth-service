package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;

public abstract class BaseResource {
    public Date resCreated;
    public Date resUpdated;
    public Date resDeleted;
    public String resMessage;
    public String resource;
    public BaseGeneratedText text;
    public List<CanonicalType> profile;
    public Integer version = 1;
    public boolean active;
    public List<Coding> security;
    public List<Coding> tag;
    public List<Extension> extension;
    public String fhir_id;
}
