package vn.ehealth.hl7.fhir.clinical.dao;

import java.util.List;

import org.hl7.fhir.r4.model.CarePlan;
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

/**
 * 
 * @author sonvt
 * @since 2019
 */
public interface ICarePlan {

    CarePlan create(FhirContext fhirContext, CarePlan object);  

    CarePlan update(FhirContext fhirContext, CarePlan object, IdType idType);

    CarePlan read(FhirContext fhirContext, IdType idType);

    CarePlan remove(FhirContext fhirContext, IdType idType);

    CarePlan readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, 
    		TokenParam active,
            TokenParam activityCode,
            DateRangeParam activityDate,
            ReferenceParam activityReference,
            ReferenceParam basedOn,
            ReferenceParam careTeam,
            TokenParam category,
            ReferenceParam condition,
            ReferenceParam context,
            DateRangeParam date,
            ReferenceParam definition,
            ReferenceParam encounter,
            ReferenceParam goal,
            TokenParam identifier,
            TokenParam intent,
            ReferenceParam partOf,
            ReferenceParam patient,
            ReferenceParam performer,
            ReferenceParam replaces,
            TokenParam status,
            ReferenceParam subject,
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
            TokenParam activityCode,
            DateRangeParam activityDate,
            ReferenceParam activityReference,
            ReferenceParam basedOn,
            ReferenceParam careTeam,
            TokenParam category,
            ReferenceParam condition,
            ReferenceParam context,
            DateRangeParam date,
            ReferenceParam definition,
            ReferenceParam encounter,
            ReferenceParam goal,
            TokenParam identifier,
            TokenParam intent,
            ReferenceParam partOf,
            ReferenceParam patient,
            ReferenceParam performer,
            ReferenceParam replaces,
            TokenParam status,
            ReferenceParam subject,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
