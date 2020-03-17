package vn.ehealth.hl7.fhir.medication.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationStatement;
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
public interface IMedicationStatement {

    MedicationStatement create(FhirContext fhirContext, MedicationStatement object);

    MedicationStatement update(FhirContext fhirContext, MedicationStatement object, IdType idType);

    MedicationStatement read(FhirContext fhirContext, IdType idType);

    MedicationStatement remove(FhirContext fhirContext, IdType idType);

    MedicationStatement readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            TokenParam category,
            TokenParam code,
            ReferenceParam context,
            DateRangeParam effective,
            TokenParam identifier,
            ReferenceParam medication,
            ReferenceParam partOf,
            ReferenceParam patient,
            ReferenceParam source,
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
            TokenParam category,
            TokenParam code,
            ReferenceParam context,
            DateRangeParam effective,
            TokenParam identifier,
            ReferenceParam medication,
            ReferenceParam partOf,
            ReferenceParam patient,
            ReferenceParam source,
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
