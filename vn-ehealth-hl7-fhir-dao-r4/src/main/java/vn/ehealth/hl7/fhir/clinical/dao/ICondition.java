package vn.ehealth.hl7.fhir.clinical.dao;

import java.util.List;

import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.QuantityParam;
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
public interface ICondition {

    Condition create(FhirContext fhirContext, Condition object);

    Condition update(FhirContext fhirContext, Condition object, IdType idType);

    Condition read(FhirContext fhirContext, IdType idType);

    Condition remove(FhirContext fhirContext, IdType idType);

    Condition readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_AGE) QuantityParam abatementAge,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_BOOLEAN) TokenParam abatementBoolean,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_DATE) DateRangeParam abatementDate,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_STRING) TokenParam abatementString,
            @OptionalParam(name = ConstantKeys.SP_ASSERTED_DATE) DateRangeParam assertedDate,
            @OptionalParam(name = ConstantKeys.SP_ASSERTER) ReferenceParam asserter,
            @OptionalParam(name = ConstantKeys.SP_BODY_SITE) TokenParam bodySite,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CLINICAL_STATUS) TokenParam clinicalStatus,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_EVIDENCE) TokenParam evidence,
            @OptionalParam(name = ConstantKeys.SP_EVIDENCE_DETAIL) ReferenceParam evidenceDetail,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_ONSET_AGE) QuantityParam onsetAge,
            @OptionalParam(name = ConstantKeys.SP_ONSET_DATE) DateRangeParam onseDate,
            @OptionalParam(name = ConstantKeys.SP_ONSET_INFO) StringParam onsetInfo,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_SEVERITY) TokenParam severity,
            @OptionalParam(name = ConstantKeys.SP_STAGE) TokenParam stage,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_VERIFICATION_STATUS) TokenParam verificationStatus,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, String sortParam, Integer count);

    long countMatchesAdvancedTotal(FhirContext fhirContext,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_AGE) QuantityParam abatementAge,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_BOOLEAN) TokenParam abatementBoolean,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_DATE) DateRangeParam abatementDate,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_STRING) TokenParam abatementString,
            @OptionalParam(name = ConstantKeys.SP_ASSERTED_DATE) DateRangeParam assertedDate,
            @OptionalParam(name = ConstantKeys.SP_ASSERTER) ReferenceParam asserter,
            @OptionalParam(name = ConstantKeys.SP_BODY_SITE) TokenParam bodySite,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CLINICAL_STATUS) TokenParam clinicalStatus,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_EVIDENCE) TokenParam evidence,
            @OptionalParam(name = ConstantKeys.SP_EVIDENCE_DETAIL) ReferenceParam evidenceDetail,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_ONSET_AGE) QuantityParam onsetAge,
            @OptionalParam(name = ConstantKeys.SP_ONSET_DATE) DateRangeParam onseDate,
            @OptionalParam(name = ConstantKeys.SP_ONSET_INFO) StringParam onsetInfo,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_SEVERITY) TokenParam severity,
            @OptionalParam(name = ConstantKeys.SP_STAGE) TokenParam stage,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_VERIFICATION_STATUS) TokenParam verificationStatus,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content);
}
