package vn.ehealth.hl7.fhir.provider.dao;

import java.util.List;

import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.IdType;
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
public interface IHealthcareService {

    HealthcareService create(FhirContext fhirContext, HealthcareService healthcareService);

    HealthcareService update(FhirContext fhirContext, HealthcareService healthcareService, IdType theId);

    HealthcareService read(FhirContext ctx, IdType theId);

    HealthcareService remove(FhirContext ctx, IdType theId);
    
    HealthcareService readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CHARACTERISTIC) TokenParam characteristic,
            @OptionalParam(name = ConstantKeys.SP_ENDPOINT) ReferenceParam endpoint,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_ORG) ReferenceParam organization,
            @OptionalParam(name = ConstantKeys.SP_PROGRAMNAME) StringParam programname,
            @OptionalParam(name = ConstantKeys.SP_TYPE) TokenParam type,
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
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CHARACTERISTIC) TokenParam characteristic,
            @OptionalParam(name = ConstantKeys.SP_ENDPOINT) ReferenceParam endpoint,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_ORG) ReferenceParam organization,
            @OptionalParam(name = ConstantKeys.SP_PROGRAMNAME) StringParam programname,
            @OptionalParam(name = ConstantKeys.SP_TYPE) TokenParam type,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content);
}
