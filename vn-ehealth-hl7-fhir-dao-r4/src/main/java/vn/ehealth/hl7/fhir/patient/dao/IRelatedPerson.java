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

    List<Resource> search(FhirContext ctx, @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
            @OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
            @OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
            @OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
            @OptionalParam(name = ConstantKeys.SP_BIRTHDATE) DateRangeParam birthDate,
            @OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
            @OptionalParam(name = ConstantKeys.SP_GENDER) StringParam gender,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
            @OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
            @OptionalParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, String sortParam, Integer count);



    long findMatchesAdvancedTotal(FhirContext ctx, @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
            @OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
            @OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
            @OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
            @OptionalParam(name = ConstantKeys.SP_BIRTHDATE) DateRangeParam birthDate,
            @OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
            @OptionalParam(name = ConstantKeys.SP_GENDER) StringParam gender,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
            @OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
            @OptionalParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content);
}
