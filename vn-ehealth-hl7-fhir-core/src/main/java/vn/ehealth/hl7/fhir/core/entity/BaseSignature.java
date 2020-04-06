package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseSignature extends BaseComplexType {
	public List<BaseCoding> type;
	public Date when;
	public BaseReference who;
	public BaseReference onBehalfOf;
	public String targetFormat;
	public String sigFormat;
	public byte[] data;
}
