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

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            QuantityParam abatementAge,
            TokenParam abatementBoolean,
            DateRangeParam abatementDate,
            TokenParam abatementString,
            DateRangeParam assertedDate,
            ReferenceParam asserter,
            TokenParam bodySite,
            TokenParam category,
            TokenParam clinicalStatus,
            TokenParam code,
            ReferenceParam context,
            ReferenceParam encounter,
            TokenParam evidence,
            ReferenceParam evidenceDetail,
            TokenParam identifier,
            QuantityParam onsetAge,
            DateRangeParam onseDate,
            StringParam onsetInfo,
            ReferenceParam patient,
            TokenParam severity,
            TokenParam stage,
            ReferenceParam subject,
            TokenParam verificationStatus,
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
            QuantityParam abatementAge,
            TokenParam abatementBoolean,
            DateRangeParam abatementDate,
            TokenParam abatementString,
            DateRangeParam assertedDate,
            ReferenceParam asserter,
            TokenParam bodySite,
            TokenParam category,
            TokenParam clinicalStatus,
            TokenParam code,
            ReferenceParam context,
            ReferenceParam encounter,
            TokenParam evidence,
            ReferenceParam evidenceDetail,
            TokenParam identifier,
            QuantityParam onsetAge,
            DateRangeParam onseDate,
            StringParam onsetInfo,
            ReferenceParam patient,
            TokenParam severity,
            TokenParam stage,
            ReferenceParam subject,
            TokenParam verificationStatus,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
