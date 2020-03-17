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

    List<Resource> search(FhirContext ctx, StringParam deviceName,
            TokenParam identifier,
            ReferenceParam location,
            StringParam manufacturer,
            StringParam model,
            ReferenceParam organization,
            ReferenceParam patient,
            StringParam udiCarrier,
            UriParam url,
            TokenParam type,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content,
            StringParam _page, String sortParam, Integer count);

    long findMatchesAdvancedTotal(FhirContext ctx, StringParam deviceName,
            TokenParam identifier,
            ReferenceParam location,
            StringParam manufacturer,
            StringParam model,
            ReferenceParam organization,
            ReferenceParam patient,
            StringParam udiCarrier,
            UriParam url,
            TokenParam type,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
