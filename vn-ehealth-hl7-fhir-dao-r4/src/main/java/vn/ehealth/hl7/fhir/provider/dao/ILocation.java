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

    List<Resource> search(FhirContext ctx, StringParam address,
            StringParam addressCity,
            StringParam addressCountry,
            StringParam addressState,
            ReferenceParam endpoint,
            TokenParam identifier,
            TokenParam near,
            QuantityParam nearDistance,
            TokenParam operationalStatus,
            ReferenceParam organization,
            TokenParam status,
            TokenParam type,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content,
            StringParam _page, String sortParam, Integer count);

    long findMatchesAdvancedTotal(FhirContext ctx, StringParam address,
            StringParam addressCity,
            StringParam addressCountry,
            StringParam addressState,
            ReferenceParam endpoint,
            TokenParam identifier,
            TokenParam near,
            QuantityParam nearDistance,
            TokenParam operationalStatus,
            ReferenceParam organization,
            TokenParam status,
            TokenParam type,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
