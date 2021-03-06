package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public abstract class BaseResource {
    @JsonIgnore public Date _resCreated;
    @JsonIgnore public Date _resUpdated;
    @JsonIgnore public Date _resDeleted;
    @JsonIgnore public String _resMessage;
    @JsonIgnore public String _resource;
    @JsonIgnore public String _fhirVersion;
    @JsonIgnore public List<String> _profile;
    @JsonIgnore public Integer _version = 1;
    @JsonIgnore public boolean _active;
    @JsonIgnore public List<BaseCoding> _security;
    @JsonIgnore public List<BaseCoding> _tag;
    public List<BaseExtension> extension;
    public List<BaseExtension> modifierExtension;
    @JsonIgnore public String _fhirId;
    
    @Transient
    public Map<String, Object> computes = new HashMap<>();
    
    public String getFhirId() {
    	return _fhirId;
    }
}
