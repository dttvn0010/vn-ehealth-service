package vn.ehealth.hl7.fhir.medication.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
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
public interface IImmunization {

    Immunization create(FhirContext fhirContext, Immunization object);

    Immunization update(FhirContext fhirContext, Immunization object, IdType idType);

    Immunization read(FhirContext fhirContext, IdType idType);

    Immunization remove(FhirContext fhirContext, IdType idType);

    Immunization readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            DateRangeParam date,
            NumberParam doseSequence,
            TokenParam identifier,
            ReferenceParam location,
            StringParam lotNumber,
            ReferenceParam manufacturer,
            TokenParam notgiven,
            ReferenceParam patient,
            ReferenceParam practitioner,
            ReferenceParam reaction,
            DateRangeParam reactionDate,
            TokenParam reason,
            TokenParam reasonNotGiven,
            TokenParam status,
            TokenParam vaccineCode,
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
            DateRangeParam date,
            NumberParam doseSequence,
            TokenParam identifier,
            ReferenceParam location,
            StringParam lotNumber,
            ReferenceParam manufacturer,
            TokenParam notgiven,
            ReferenceParam patient,
            ReferenceParam practitioner,
            ReferenceParam reaction,
            DateRangeParam reactionDate,
            TokenParam reason,
            TokenParam reasonNotGiven,
            TokenParam status,
            TokenParam vaccineCode,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
