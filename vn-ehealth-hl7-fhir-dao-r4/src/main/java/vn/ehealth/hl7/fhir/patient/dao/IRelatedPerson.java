package vn.ehealth.hl7.fhir.patient.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;

public interface IRelatedPerson {

    RelatedPerson create(FhirContext fhirContext, RelatedPerson object);

    RelatedPerson update(FhirContext fhirContext, RelatedPerson object, IdType idType);

    RelatedPerson read(FhirContext fhirContext, IdType idType);

    RelatedPerson remove(FhirContext fhirContext, IdType idType);
    
    RelatedPerson readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext ctx, TokenParam active,
            StringParam address,
            StringParam addressCity,
            StringParam addressCountry,
            StringParam addressState,
            DateRangeParam birthDate,
            TokenParam email,
            StringParam gender,
            TokenParam identifier,
            StringParam name,
            ReferenceParam patient,
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
            StringParam _page, String sortParam, Integer count);



    long findMatchesAdvancedTotal(FhirContext ctx, TokenParam active,
            StringParam address,
            StringParam addressCity,
            StringParam addressCountry,
            StringParam addressState,
            DateRangeParam birthDate,
            TokenParam email,
            StringParam gender,
            TokenParam identifier,
            StringParam name,
            ReferenceParam patient,
            TokenParam phone,
            StringParam phonetic,
            TokenParam telecom,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
