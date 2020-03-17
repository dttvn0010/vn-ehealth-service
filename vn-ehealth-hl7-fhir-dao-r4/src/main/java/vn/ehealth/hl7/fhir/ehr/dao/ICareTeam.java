package vn.ehealth.hl7.fhir.ehr.dao;

import java.util.List;

import org.hl7.fhir.r4.model.CareTeam;
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

public interface ICareTeam {

    CareTeam create(FhirContext fhirContext, CareTeam object);

    CareTeam update(FhirContext fhirContext, CareTeam object, IdType idType);

    CareTeam read(FhirContext fhirContext, IdType idType);

    CareTeam remove(FhirContext fhirContext, IdType idType);

    CareTeam readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            TokenParam category,
            ReferenceParam context,
            DateRangeParam date,
            ReferenceParam encounter,
            TokenParam identifier,
            ReferenceParam participant,
            ReferenceParam patient,
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

    long getTotal(FhirContext fhirContext, TokenParam active,
            TokenParam category,
            ReferenceParam context,
            DateRangeParam date,
            ReferenceParam encounter,
            TokenParam identifier,
            ReferenceParam participant,
            ReferenceParam patient,
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
