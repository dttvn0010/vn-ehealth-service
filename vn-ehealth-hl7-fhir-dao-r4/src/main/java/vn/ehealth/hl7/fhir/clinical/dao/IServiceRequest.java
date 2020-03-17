package vn.ehealth.hl7.fhir.clinical.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;

public interface IServiceRequest {
	ServiceRequest create(FhirContext fhirContext, ServiceRequest object);

	ServiceRequest update(FhirContext fhirContext, ServiceRequest object, IdType idType);

	ServiceRequest read(FhirContext fhirContext, IdType idType);

	ServiceRequest remove(FhirContext fhirContext, IdType idType);
    
	ServiceRequest readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            ReferenceParam bassedOn,
            TokenParam category,
            TokenParam code,
            ReferenceParam context,
            DateRangeParam date,
            ReferenceParam definition,
            ReferenceParam encounter,
            TokenParam identifier,
            ReferenceParam location,
            ReferenceParam partOf,
            ReferenceParam patient,
            ReferenceParam performer,
            TokenParam status,
            ReferenceParam subject,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content,
            StringParam _page, String sortParam, Integer count);

    long countMatchesAdvancedTotal(FhirContext fhirContext,
            TokenParam active,
            ReferenceParam bassedOn,
            TokenParam category,
            TokenParam code,
            ReferenceParam context,
            DateRangeParam date,
            ReferenceParam definition,
            ReferenceParam encounter,
            TokenParam identifier,
            ReferenceParam location,
            ReferenceParam partOf,
            ReferenceParam patient,
            ReferenceParam performer,
            TokenParam status,
            ReferenceParam subject,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
