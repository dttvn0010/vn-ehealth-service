package vn.ehealth.hl7.fhir.medication.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;

/**
 * 
 * @author sonvt
 * @since 2019
 */
public interface IMedicationRequest {

    MedicationRequest create(FhirContext fhirContext, MedicationRequest object);

    MedicationRequest update(FhirContext fhirContext, MedicationRequest object, IdType idType);

    MedicationRequest read(FhirContext fhirContext, IdType idType);

    MedicationRequest remove(FhirContext fhirContext, IdType idType);

    MedicationRequest readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = "category") TokenParam category,
            @OptionalParam(name = "identifier") TokenParam identifier,
            @OptionalParam(name = "intent") TokenParam intent,
            @OptionalParam(name = "priority") TokenParam priority,
            @OptionalParam(name = "status") TokenParam status,
            @OptionalParam(name = "subject") ReferenceParam subject,
            @OptionalParam(name = "requester") ReferenceParam requester,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = "medication") ReferenceParam medication,
            @OptionalParam(name = "context") ReferenceParam context,
            @OptionalParam(name = "intended-dispenser    ") ReferenceParam intendedDispenser,
            @OptionalParam(name = "authoredon") DateRangeParam authoredon,
            @OptionalParam(name = "date") DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, String sortParam, Integer count);

    long countMatchesAdvancedTotal(FhirContext fhirContext,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = "category") TokenParam category,
            @OptionalParam(name = "identifier") TokenParam identifier,
            @OptionalParam(name = "intent") TokenParam intent,
            @OptionalParam(name = "priority") TokenParam priority,
            @OptionalParam(name = "status") TokenParam status,
            @OptionalParam(name = "subject") ReferenceParam subject,
            @OptionalParam(name = "requester") ReferenceParam requester,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = "medication") ReferenceParam medication,
            @OptionalParam(name = "context") ReferenceParam context,
            @OptionalParam(name = "intended-dispenser    ") ReferenceParam intendedDispenser,
            @OptionalParam(name = "authoredon") DateRangeParam authoredon,
            @OptionalParam(name = "date") DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content);
}
