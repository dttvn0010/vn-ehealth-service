package vn.ehealth.hl7.fhir.diagnostic.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;

public interface IObservation {

    Observation create(FhirContext fhirContext, Observation object);

    Observation update(FhirContext fhirContext, Observation object, IdType idType);

    Observation read(FhirContext fhirContext, IdType idType);

    Observation remove(FhirContext fhirContext, IdType idType);
    
    Observation readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            ReferenceParam basedOn,
            TokenParam category,
            TokenParam code,
            TokenParam comboCode,
            TokenParam comboDataAbsentReason,
            TokenParam comboValueConcept,
            TokenParam componentCode,
            TokenParam componentDataAbsentReason,
            TokenParam componentValueConcept,
            ReferenceParam conetext,
            TokenParam dataAbsentReason,
            DateRangeParam date,
            ReferenceParam device,
            ReferenceParam encounter,
            TokenParam identifier,
            TokenParam method,
            ReferenceParam patient,
            ReferenceParam performer,
            ReferenceParam relatedTarget,
            TokenParam relatedType,
            ReferenceParam specimen,
            TokenParam status,
            ReferenceParam subject,
            TokenParam valueConcept,
            DateRangeParam valueDate,
            StringParam valueString,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content,
            StringParam _page, String sortParam, Integer count);

    long countMatchesAdvancedTotal(FhirContext fhirContext,
            TokenParam active,
            ReferenceParam basedOn,
            TokenParam category,
            TokenParam code,
            TokenParam comboCode,
            TokenParam comboDataAbsentReason,
            TokenParam comboValueConcept,
            TokenParam componentCode,
            TokenParam componentDataAbsentReason,
            TokenParam componentValueConcept,
            ReferenceParam conetext,
            TokenParam dataAbsentReason,
            DateRangeParam date,
            ReferenceParam device,
            ReferenceParam encounter,
            TokenParam identifier,
            TokenParam method,
            ReferenceParam patient,
            ReferenceParam performer,
            ReferenceParam relatedTarget,
            TokenParam relatedType,
            ReferenceParam specimen,
            TokenParam status,
            ReferenceParam subject,
            TokenParam valueConcept,
            DateRangeParam valueDate,
            StringParam valueString,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
