package vn.ehealth.hl7.fhir.clinical.dao;

import java.util.List;

import org.hl7.fhir.r4.model.DetectedIssue;
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
public interface IDetectedIssue {

    DetectedIssue create(FhirContext fhirContext, DetectedIssue object);

    DetectedIssue update(FhirContext fhirContext, DetectedIssue object, IdType idType);

    DetectedIssue read(FhirContext fhirContext, IdType idType);

    DetectedIssue remove(FhirContext fhirContext, IdType idType);

    DetectedIssue readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            ReferenceParam author,
            TokenParam category,
            DateRangeParam date,
            TokenParam identifier,
            ReferenceParam implicated,
            ReferenceParam patient,
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
            ReferenceParam author,
            TokenParam category,
            DateRangeParam date,
            TokenParam identifier,
            ReferenceParam implicated,
            ReferenceParam patient,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
