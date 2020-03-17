package vn.ehealth.hl7.fhir.schedule.dao;

import java.util.List;

import org.hl7.fhir.r4.model.AppointmentResponse;
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

public interface IAppointmentResponse {

    AppointmentResponse create(FhirContext fhirContext, AppointmentResponse object);

    AppointmentResponse update(FhirContext fhirContext, AppointmentResponse object, IdType idType);

    AppointmentResponse read(FhirContext fhirContext, IdType idType);

    AppointmentResponse remove(FhirContext fhirContext, IdType idType);

    AppointmentResponse readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            ReferenceParam actor,
            TokenParam identifier,
            ReferenceParam appointment,
            ReferenceParam location,
            ReferenceParam patient,
            ReferenceParam practitioner,
            TokenParam partStatus,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content,
            StringParam _page, String sortParam, Integer count);

    long findMatchesAdvancedTotal(FhirContext fhirContext,
            TokenParam active,
            ReferenceParam actor,
            TokenParam identifier,
            ReferenceParam appointment,
            ReferenceParam location,
            ReferenceParam patient,
            ReferenceParam practitioner,
            TokenParam partStatus,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
