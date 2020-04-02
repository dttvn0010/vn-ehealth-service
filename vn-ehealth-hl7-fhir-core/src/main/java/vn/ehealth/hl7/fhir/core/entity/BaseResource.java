package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public abstract class BaseResource {
    public Date _resCreated;
    public Date _resUpdated;
    public Date _resDeleted;
    public String _resMessage;
    public String _resource;
    public String _fhirVersion;
    public List<String> _profile;
    public Integer _version = 1;
    public boolean _active;
    public List<BaseCoding> _security;
    public List<BaseCoding> _tag;
    public List<BaseExtension> _extension;
    public List<BaseExtension> _modifierExtension;
    public String _fhirId;
}
