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

    List<Resource> search(FhirContext fhirContext, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DOSE_SEQUENCE) NumberParam doseSequence,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_LOT_NUMBER) StringParam lotNumber,
            @OptionalParam(name = ConstantKeys.SP_MANUFACTURER) ReferenceParam manufacturer,
            @OptionalParam(name = ConstantKeys.SP_NOTGIVEN) TokenParam notgiven,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
            @OptionalParam(name = ConstantKeys.SP_REACTION) ReferenceParam reaction,
            @OptionalParam(name = ConstantKeys.SP_REACTION_DATE) DateRangeParam reactionDate,
            @OptionalParam(name = ConstantKeys.SP_REASON) TokenParam reason,
            @OptionalParam(name = ConstantKeys.SP_REASON_NOT_GIVEN) TokenParam reasonNotGiven,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_VACCINE_CODE) TokenParam vaccineCode,
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
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DOSE_SEQUENCE) NumberParam doseSequence,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_LOT_NUMBER) StringParam lotNumber,
            @OptionalParam(name = ConstantKeys.SP_MANUFACTURER) ReferenceParam manufacturer,
            @OptionalParam(name = ConstantKeys.SP_NOTGIVEN) TokenParam notgiven,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
            @OptionalParam(name = ConstantKeys.SP_REACTION) ReferenceParam reaction,
            @OptionalParam(name = ConstantKeys.SP_REACTION_DATE) DateRangeParam reactionDate,
            @OptionalParam(name = ConstantKeys.SP_REASON) TokenParam reason,
            @OptionalParam(name = ConstantKeys.SP_REASON_NOT_GIVEN) TokenParam reasonNotGiven,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_VACCINE_CODE) TokenParam vaccineCode,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content);
}
