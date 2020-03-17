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

    List<Resource> search(FhirContext fhirContext, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam bassedOn,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DEFINITION) ReferenceParam definition,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partOf,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PERFORMER) ReferenceParam performer,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, String sortParam, Integer count);

    long countMatchesAdvancedTotal(FhirContext fhirContext,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam bassedOn,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DEFINITION) ReferenceParam definition,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partOf,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PERFORMER) ReferenceParam performer,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content);
}
