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
            TokenParam active,
            TokenParam code,
            TokenParam category,
            TokenParam identifier,
            TokenParam intent,
            TokenParam priority,
            TokenParam status,
            ReferenceParam subject,
            ReferenceParam requester,
            ReferenceParam patient,
            ReferenceParam medication,
            ReferenceParam context,
            ReferenceParam intendedDispenser,
            DateRangeParam authoredon,
            DateRangeParam date,
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
            TokenParam code,
            TokenParam category,
            TokenParam identifier,
            TokenParam intent,
            TokenParam priority,
            TokenParam status,
            ReferenceParam subject,
            ReferenceParam requester,
            ReferenceParam patient,
            ReferenceParam medication,
            ReferenceParam context,
            ReferenceParam intendedDispenser,
            DateRangeParam authoredon,
            DateRangeParam date,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
