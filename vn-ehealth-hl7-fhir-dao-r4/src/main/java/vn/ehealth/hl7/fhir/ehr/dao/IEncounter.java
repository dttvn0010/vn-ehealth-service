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

    List<Resource> search(FhirContext ctx, TokenParam active,
            ReferenceParam appointment,
            TokenParam _class,
            DateRangeParam date,
            ReferenceParam diagnosis,
            ReferenceParam episodeofcare,
            TokenParam identifier,
            ReferenceParam incomingreferral,
            NumberParam length,
            ReferenceParam location,
            DateRangeParam locationPeriod,
            ReferenceParam partOf,
            ReferenceParam participant,
            TokenParam participantType,
            ReferenceParam patient,
            ReferenceParam practitioner,
            TokenParam reason,
            ReferenceParam serviceProvider,
            TokenParam specialArrangement,
            TokenParam status,
            ReferenceParam subject,
            TokenParam type,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content,
            StringParam _page, String sortParam, Integer count);

    long getTotal(FhirContext fhirContext, TokenParam active,
            ReferenceParam appointment,
            TokenParam _class,
            DateRangeParam date,
            ReferenceParam diagnosis,
            ReferenceParam episodeofcare,
            TokenParam identifier,
            ReferenceParam incomingreferral,
            NumberParam length,
            ReferenceParam location,
            DateRangeParam locationPeriod,
            ReferenceParam partOf,
            ReferenceParam participant,
            TokenParam participantType,
            ReferenceParam patient,
            ReferenceParam practitioner,
            TokenParam reason,
            ReferenceParam serviceProvider,
            TokenParam specialArrangement,
            TokenParam status,
            ReferenceParam subject,
            TokenParam type,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
