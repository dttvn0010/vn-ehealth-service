package vn.ehealth.hl7.fhir.clinical.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
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
public interface IProcedure {

    Procedure create(FhirContext fhirContext, Procedure object);

    Procedure update(FhirContext fhirContext, Procedure object, IdType idType);

    Procedure read(FhirContext fhirContext, IdType idType);

    Procedure remove(FhirContext fhirContext, IdType idType);
    
    Procedure readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            ReferenceParam bassedOn,
            TokenParam category,
            TokenParam code,
            ReferenceParam context,
            DateRangeParam date,
            ReferenceParam definition,
            ReferenceParam encounter,
            TokenParam identifier,
            ReferenceParam location,
            ReferenceParam partOf,
            ReferenceParam patient,
            ReferenceParam performer,
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
            ReferenceParam bassedOn,
            TokenParam category,
            TokenParam code,
            ReferenceParam context,
            DateRangeParam date,
            ReferenceParam definition,
            ReferenceParam encounter,
            TokenParam identifier,
            ReferenceParam location,
            ReferenceParam partOf,
            ReferenceParam patient,
            ReferenceParam performer,
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
