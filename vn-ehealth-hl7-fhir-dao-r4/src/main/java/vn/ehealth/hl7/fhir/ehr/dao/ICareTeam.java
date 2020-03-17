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

    List<Resource> search(FhirContext fhirContext, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_PARTICIPANT) ReferenceParam participant,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,

            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, String sortParam, Integer count);

    long getTotal(FhirContext fhirContext, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_PARTICIPANT) ReferenceParam participant,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,

            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content);
}
