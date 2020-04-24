package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.List;

public class BaseMetadataResource extends BaseResource {

	public String url;
	public String version;
	public String name;
	public String title;
	public String status;
	public Boolean experimental;
	public Date date;
	public String publisher;
	public List<BaseContactDetail> contact;
	public String description;
	public List<BaseUsageContext> useContext;
	public List<BaseCodeableConcept> jurisdiction;
}
