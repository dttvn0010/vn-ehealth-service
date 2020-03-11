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

    List<Resource> search(FhirContext fhirContext, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
            @OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
            @OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
            @OptionalParam(name = ConstantKeys.SP_ADDRESS_POSTALCODE) StringParam addressPostalCode,
            @OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
            @OptionalParam(name = ConstantKeys.SP_ADDRESS_USE) TokenParam addressUse,
            @OptionalParam(name = ConstantKeys.SP_ENDPOINT) ReferenceParam endpoint,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partof,
            @OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
            @OptionalParam(name = ConstantKeys.SP_TYPE) TokenParam type,
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
            @OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
            @OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
            @OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
            @OptionalParam(name = ConstantKeys.SP_ADDRESS_POSTALCODE) StringParam addressPostalCode,
            @OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
            @OptionalParam(name = ConstantKeys.SP_ADDRESS_USE) TokenParam addressUse,
            @OptionalParam(name = ConstantKeys.SP_ENDPOINT) ReferenceParam endpoint,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partof,
            @OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
            @OptionalParam(name = ConstantKeys.SP_TYPE) TokenParam type,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content);
}
