package vn.ehealth.hl7.fhir.provider.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.PractitionerRole;
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
public interface IPractitionerRole {

    PractitionerRole create(FhirContext fhirContext, PractitionerRole object);

    PractitionerRole update(FhirContext fhirContext, PractitionerRole object, IdType idType);

    PractitionerRole read(FhirContext fhirContext, IdType idType);

    PractitionerRole remove(FhirContext fhirContext, IdType idType);

    PractitionerRole readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
            @OptionalParam(name = ConstantKeys.SP_ENDPOINT) ReferenceParam endpoint,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_ORGANIZATION) ReferenceParam organization,
            @OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
            @OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
            @OptionalParam(name = ConstantKeys.SP_ROLE) TokenParam role,
            @OptionalParam(name = ConstantKeys.SP_SERVICE) ReferenceParam service,
            @OptionalParam(name = ConstantKeys.SP_SPECIALTY) TokenParam specialty,
            @OptionalParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,
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
            @OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
            @OptionalParam(name = ConstantKeys.SP_ENDPOINT) ReferenceParam endpoint,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_ORGANIZATION) ReferenceParam organization,
            @OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
            @OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
            @OptionalParam(name = ConstantKeys.SP_ROLE) TokenParam role,
            @OptionalParam(name = ConstantKeys.SP_SERVICE) ReferenceParam service,
            @OptionalParam(name = ConstantKeys.SP_SPECIALTY) TokenParam specialty,
            @OptionalParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content);
}
