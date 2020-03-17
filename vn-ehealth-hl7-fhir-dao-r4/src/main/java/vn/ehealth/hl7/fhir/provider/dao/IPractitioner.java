package vn.ehealth.hl7.fhir.provider.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Practitioner;
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
public interface IPractitioner {

    Practitioner create(FhirContext fhirContext, Practitioner object);

    Practitioner update(FhirContext fhirContext, Practitioner object, IdType idType);

    Practitioner read(FhirContext fhirContext, IdType idType);

    Practitioner remove(FhirContext fhirContext, IdType idType);
    
    Practitioner readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            StringParam address,
            StringParam addressCity,
            StringParam addressCountry,
            StringParam addressPostalCode,
            StringParam addressState,
            TokenParam addressUse,
            TokenParam communication,
            TokenParam email,
            StringParam family,
            TokenParam gender,
            StringParam given,
            TokenParam identifier,
            StringParam name,
            TokenParam phone,
            StringParam phonetic,
            TokenParam telecom,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content,
            ReferenceParam managingOrg,
            StringParam _page, String sortParam, Integer count);

    long countMatchesAdvancedTotal(FhirContext fhirContext,
            TokenParam active,
            StringParam address,
            StringParam addressCity,
            StringParam addressCountry,
            StringParam addressPostalCode,
            StringParam addressState,
            TokenParam addressUse,
            TokenParam communication,
            TokenParam email,
            StringParam family,
            TokenParam gender,
            StringParam given,
            TokenParam identifier,
            StringParam name,
            TokenParam phone,
            StringParam phonetic,
            TokenParam telecom,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content,
            ReferenceParam managingOrg);
}
