package vn.ehealth.hl7.fhir.term.dao;

import java.util.List;

import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public interface ICodeSystem {

    CodeSystem create(FhirContext fhirContext, CodeSystem object);

    CodeSystem update(FhirContext fhirContext, CodeSystem object, IdType idType);

    CodeSystem read(FhirContext fhirContext, IdType idType);

    CodeSystem remove(FhirContext fhirContext, IdType idType);
    
    CodeSystem readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) TokenParam contentMode,
            @OptionalParam(name = ConstantKeys.SP_DESCRIPTION) StringParam description,
            @OptionalParam(name = ConstantKeys.SP_JURIS) TokenParam jurisdiction,
            @OptionalParam(name = ConstantKeys.SP_LANGUAGE) TokenParam language,
            @OptionalParam(name = ConstantKeys.SP_PUBLISHER) StringParam publisher,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SYSTEM) UriParam system,
            @OptionalParam(name = ConstantKeys.SP_TITLE) StringParam title,
            @OptionalParam(name = ConstantKeys.SP_URL) UriParam url,
            @OptionalParam(name = ConstantKeys.SP_VERSION) TokenParam version,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, String theSort, Integer count);

    Parameters getLookupParams(@OperationParam(name = "code") TokenParam code,
            @OperationParam(name = "system") UriParam system, @OperationParam(name = "version") StringParam version,
            @OperationParam(name = "coding") Coding coding, @OperationParam(name = "date") DateRangeParam date,
            @OperationParam(name = "displayLanguage") TokenParam displayLanguage,
            @OperationParam(name = "property") TokenParam property,
            // Parameters for all resources
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = "_lastUpdated") DateRangeParam _lastUpdated,
            @OptionalParam(name = "_tag") TokenParam _tag, @OptionalParam(name = "_profile") UriParam _profile,
            @OptionalParam(name = "_query") TokenParam _query, @OptionalParam(name = "_security") TokenParam _security,
            @OptionalParam(name = "_content") StringParam _content,
            // Search result parameters
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page);

    long findMatchesAdvancedTotal(FhirContext fhirContext, @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) TokenParam contentMode,
            @OptionalParam(name = ConstantKeys.SP_DESCRIPTION) StringParam description,
            @OptionalParam(name = ConstantKeys.SP_JURIS) TokenParam jurisdiction,
            @OptionalParam(name = ConstantKeys.SP_LANGUAGE) TokenParam language,
            @OptionalParam(name = ConstantKeys.SP_PUBLISHER) StringParam publisher,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SYSTEM) UriParam system,
            @OptionalParam(name = ConstantKeys.SP_TITLE) StringParam title,
            @OptionalParam(name = ConstantKeys.SP_URL) UriParam url,
            @OptionalParam(name = ConstantKeys.SP_VERSION) TokenParam version,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content);
}
