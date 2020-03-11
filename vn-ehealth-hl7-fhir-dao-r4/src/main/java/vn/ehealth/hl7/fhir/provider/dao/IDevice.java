package vn.ehealth.hl7.fhir.provider.dao;

import java.util.List;

import org.hl7.fhir.r4.model.Device;
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

public interface IDevice {

    Device create(FhirContext fhirContext, Device object);

    Device update(FhirContext fhirContext, Device object, IdType idType);

    Device read(FhirContext fhirContext, IdType idType);

    Device remove(FhirContext fhirContext, IdType idType);
    
    Device readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext ctx, @OptionalParam(name = "device-name") StringParam deviceName,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = "location") ReferenceParam location,
            @OptionalParam(name = "manufacturer") StringParam manufacturer,
            @OptionalParam(name = "model") StringParam model,
            @OptionalParam(name = "organization") ReferenceParam organization,
            @OptionalParam(name = "patient") ReferenceParam patient,
            @OptionalParam(name = "udi-carrier") StringParam udiCarrier,
            @OptionalParam(name = "udi-di") StringParam udiDi, @OptionalParam(name = "url") UriParam url,
            @OptionalParam(name = "status") TokenParam status, @OptionalParam(name = "type") TokenParam type,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, String sortParam, Integer count);

    long findMatchesAdvancedTotal(FhirContext ctx, @OptionalParam(name = "device-name") StringParam deviceName,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = "location") ReferenceParam location,
            @OptionalParam(name = "manufacturer") StringParam manufacturer,
            @OptionalParam(name = "model") StringParam model,
            @OptionalParam(name = "organization") ReferenceParam organization,
            @OptionalParam(name = "patient") ReferenceParam patient,
            @OptionalParam(name = "udi-carrier") StringParam udiCarrier,
            @OptionalParam(name = "udi-di") StringParam udiDi, @OptionalParam(name = "url") UriParam url,
            @OptionalParam(name = "status") TokenParam status, @OptionalParam(name = "type") TokenParam type,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content);
}
