package vn.ehealth.hl7.fhir.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseExpression extends BaseComplexType {
	public String description;
	public String name;
	public String language;
	public String expression;
	public String reference;
}
