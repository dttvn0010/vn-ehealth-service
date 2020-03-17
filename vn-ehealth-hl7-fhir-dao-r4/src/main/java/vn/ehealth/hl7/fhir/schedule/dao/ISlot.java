package vn.ehealth.hl7.fhir.schedule.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Slot;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;

public interface ISlot {

    Slot create(FhirContext fhirContext, Slot object);

    Slot update(FhirContext fhirContext, Slot object, IdType idType);

    Slot read(FhirContext fhirContext, IdType idType);

    Slot remove(FhirContext fhirContext, IdType idType);

    Slot readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            TokenParam status,
            TokenParam identifier,
            ReferenceParam schedule,
            DateRangeParam date,
            TokenParam slotType,
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
            TokenParam status,
            TokenParam identifier,
            ReferenceParam schedule,
            DateRangeParam date,
            TokenParam slotType,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
