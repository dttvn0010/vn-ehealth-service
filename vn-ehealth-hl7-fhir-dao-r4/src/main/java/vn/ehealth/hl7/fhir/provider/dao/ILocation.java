package vn.ehealth.hl7.fhir.provider.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;

public interface ILocation {

    Location create(FhirContext fhirContext, Location object);

    Location update(FhirContext fhirContext, Location object, IdType idType);

    Location read(FhirContext fhirContext, IdType idType);

    Location remove(FhirContext fhirContext, IdType idType);
    
    Location readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext ctx, @OptionalParam(name = "address") StringParam address,
            @OptionalParam(name = "address-city") StringParam addressCity,
            @OptionalParam(name = "address-country") StringParam addressCountry,
            @OptionalParam(name = "address-state") StringParam addressState,
            @OptionalParam(name = "endpoint") ReferenceParam endpoint,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = "name") StringParam name, @OptionalParam(name = "near") TokenParam near,
            @OptionalParam(name = "near-distance") QuantityParam nearDistance,
            @OptionalParam(name = "operational-status") TokenParam operationalStatus,
            @OptionalParam(name = "organization") ReferenceParam organization,
            @OptionalParam(name = "partof") ReferenceParam partof, @OptionalParam(name = "status") TokenParam status,
            @OptionalParam(name = "type") TokenParam type,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, String sortParam, Integer count);

    long findMatchesAdvancedTotal(FhirContext ctx, @OptionalParam(name = "address") StringParam address,
            @OptionalParam(name = "address-city") StringParam addressCity,
            @OptionalParam(name = "address-country") StringParam addressCountry,
            @OptionalParam(name = "address-state") StringParam addressState,
            @OptionalParam(name = "endpoint") ReferenceParam endpoint,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = "name") StringParam name, @OptionalParam(name = "near") TokenParam near,
            @OptionalParam(name = "near-distance") QuantityParam nearDistance,
            @OptionalParam(name = "operational-status") TokenParam operationalStatus,
            @OptionalParam(name = "organization") ReferenceParam organization,
            @OptionalParam(name = "partof") ReferenceParam partof, @OptionalParam(name = "status") TokenParam status,
            @OptionalParam(name = "type") TokenParam type,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content);
}
