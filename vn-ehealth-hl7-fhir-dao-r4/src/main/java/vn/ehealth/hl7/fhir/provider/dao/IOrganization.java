package vn.ehealth.hl7.fhir.provider.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
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
public interface IOrganization {

    Organization create(FhirContext fhirContext, Organization object);

    Organization update(FhirContext fhirContext, Organization object, IdType idType);

    Organization read(FhirContext fhirContext, IdType idType);

    Organization remove(FhirContext fhirContext, IdType idType);

    Organization readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            StringParam address,
            StringParam addressCity,
            StringParam addressCountry,
            StringParam addressPostalCode,
            StringParam addressState,
            TokenParam addressUse,
            ReferenceParam endpoint,
            TokenParam identifier,
            StringParam name,
            ReferenceParam partof,
            StringParam phonetic,
            TokenParam type,
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
            StringParam address,
            StringParam addressCity,
            StringParam addressCountry,
            StringParam addressPostalCode,
            StringParam addressState,
            TokenParam addressUse,
            ReferenceParam endpoint,
            TokenParam identifier,
            StringParam name,
            ReferenceParam partof,
            StringParam phonetic,
            TokenParam type,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
