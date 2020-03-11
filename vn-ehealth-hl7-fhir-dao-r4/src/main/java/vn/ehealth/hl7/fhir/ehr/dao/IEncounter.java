package vn.ehealth.hl7.fhir.ehr.dao;

import java.util.List;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;

public interface IEncounter {

    Encounter create(FhirContext fhirContext, Encounter object);

    Encounter update(FhirContext fhirContext, Encounter object, IdType idType);

    Encounter read(FhirContext fhirContext, IdType idType);

    Encounter remove(FhirContext fhirContext, IdType idType);

    Encounter readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext ctx, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_APPOINTMENT) ReferenceParam appointment,
            @OptionalParam(name = ConstantKeys.SP_CLASS) TokenParam _class,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DIAGNOSIS) ReferenceParam diagnosis,
            @OptionalParam(name = ConstantKeys.SP_EPISODEOFCARE) ReferenceParam episodeofcare,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_INCOMINGREFERRAL) ReferenceParam incomingreferral,
            @OptionalParam(name = ConstantKeys.SP_LENGHTH) NumberParam length,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_LOCATION_PERIOD) DateRangeParam locationPeriod,
            @OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partOf,
            @OptionalParam(name = ConstantKeys.SP_PARTICIPANT) ReferenceParam participant,
            @OptionalParam(name = ConstantKeys.SP_PARTICIPANT_TYPE) TokenParam participantType,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
            @OptionalParam(name = ConstantKeys.SP_REASON) TokenParam reason,
            @OptionalParam(name = ConstantKeys.SP_SERVICE_PROVIDER) ReferenceParam serviceProvider,
            @OptionalParam(name = ConstantKeys.SP_SPECIAL_ARRANGEMENT) TokenParam specialArrangement,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_TYPE) TokenParam type,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, String sortParam, Integer count);

    long getTotal(FhirContext fhirContext, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_APPOINTMENT) ReferenceParam appointment,
            @OptionalParam(name = ConstantKeys.SP_CLASS) TokenParam _class,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DIAGNOSIS) ReferenceParam diagnosis,
            @OptionalParam(name = ConstantKeys.SP_EPISODEOFCARE) ReferenceParam episodeofcare,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_INCOMINGREFERRAL) ReferenceParam incomingreferral,
            @OptionalParam(name = ConstantKeys.SP_LENGHTH) NumberParam length,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_LOCATION_PERIOD) DateRangeParam locationPeriod,
            @OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partOf,
            @OptionalParam(name = ConstantKeys.SP_PARTICIPANT) ReferenceParam participant,
            @OptionalParam(name = ConstantKeys.SP_PARTICIPANT_TYPE) TokenParam participantType,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
            @OptionalParam(name = ConstantKeys.SP_REASON) TokenParam reason,
            @OptionalParam(name = ConstantKeys.SP_SERVICE_PROVIDER) ReferenceParam serviceProvider,
            @OptionalParam(name = ConstantKeys.SP_SPECIAL_ARRANGEMENT) TokenParam specialArrangement,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_TYPE) TokenParam type,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content);
}
