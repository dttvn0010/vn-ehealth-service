package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;

public class BaseSignature  extends BaseComplexType {
	public BaseCoding type;
	public Date when;
	public BaseReference who;
	public BaseReference onBehalfOf;
	public String targetFormat;
	public String sigFormat;
	public byte[] data;
}
