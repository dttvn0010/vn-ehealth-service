package vn.ehealth.hl7.fhir.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseReference extends BaseComplexType {
	public String reference;
	public String type;
	public BaseIdentifier identifier;
	public String display;
	
}
