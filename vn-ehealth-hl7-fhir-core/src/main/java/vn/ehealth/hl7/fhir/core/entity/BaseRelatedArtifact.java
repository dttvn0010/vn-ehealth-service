package vn.ehealth.hl7.fhir.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseRelatedArtifact extends BaseComplexType {
	public String type;
	public String label;
	public String display;
	public String citation;
	public String url;
	public BaseAttachment document;
	public String resource;	
}
