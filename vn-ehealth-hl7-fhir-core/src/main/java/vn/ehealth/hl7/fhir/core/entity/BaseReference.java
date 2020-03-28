package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;

public class BaseReference {
	public String reference;
	public BaseIdentifier identifier;
	public String display;
	public List<Extension> extension;
}
