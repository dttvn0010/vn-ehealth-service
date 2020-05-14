package vn.ehealth.hl7.fhir.core.entity;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseReference extends BaseSimpleType {
	public String reference;
	public String type;
	public BaseIdentifier identifier;
	public String display;
	
	@Transient
	@JsonIgnore
	public IBaseResource resource;
}
