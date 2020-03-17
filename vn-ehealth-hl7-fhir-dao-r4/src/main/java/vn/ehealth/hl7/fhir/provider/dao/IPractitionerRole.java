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

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            DateRangeParam date,
            TokenParam email,
            ReferenceParam endpoint,
            TokenParam identifier,
            ReferenceParam location,
            ReferenceParam organization,
            TokenParam phone,
            ReferenceParam practitioner,
            TokenParam role,
            ReferenceParam service,
            TokenParam specialty,
            TokenParam telecom,
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
            TokenParam email,
            ReferenceParam endpoint,
            TokenParam identifier,
            ReferenceParam location,
            ReferenceParam organization,
            TokenParam phone,
            ReferenceParam practitioner,
            TokenParam role,
            ReferenceParam service,
            TokenParam specialty,
            TokenParam telecom,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
