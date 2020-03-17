package vn.ehealth.hl7.fhir.medication.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationAdministration;
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
public interface IMedicationAdministration {

    MedicationAdministration create(FhirContext fhirContext, MedicationAdministration object);

    MedicationAdministration update(FhirContext fhirContext, MedicationAdministration object, IdType idType);

    MedicationAdministration read(FhirContext fhirContext, IdType idType);

    MedicationAdministration remove(FhirContext fhirContext, IdType idType);

    MedicationAdministration readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            TokenParam code,
            ReferenceParam context,
            ReferenceParam device,
            DateRangeParam effectiveTime,
            TokenParam identifier,
            ReferenceParam medication,
            TokenParam notGiven,
            ReferenceParam patient,
            ReferenceParam performer,
            ReferenceParam prescription,
            TokenParam reasonGiven,
            TokenParam reasonNotGiven,
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
            TokenParam code,
            ReferenceParam context,
            ReferenceParam device,
            DateRangeParam effectiveTime,
            TokenParam identifier,
            ReferenceParam medication,
            TokenParam notGiven,
            ReferenceParam patient,
            ReferenceParam performer,
            ReferenceParam prescription,
            TokenParam reasonGiven,
            TokenParam reasonNotGiven,
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
