package vn.ehealth.hl7.fhir.dao;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.InstantType;

import ca.uhn.fhir.rest.param.DateRangeParam;

public interface iPlainDao {
	List<IBaseResource> history(InstantType theSince, DateRangeParam theAt);
}
