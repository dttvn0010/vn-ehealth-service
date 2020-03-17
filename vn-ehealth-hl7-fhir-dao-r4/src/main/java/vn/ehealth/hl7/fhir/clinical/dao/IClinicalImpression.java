package vn.ehealth.hl7.fhir.clinical.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.ClinicalImpression;
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
public interface IClinicalImpression {

    ClinicalImpression create(FhirContext fhirContext, ClinicalImpression object);

    ClinicalImpression update(FhirContext fhirContext, ClinicalImpression object, IdType idType);

    ClinicalImpression read(FhirContext fhirContext, IdType idType);

    ClinicalImpression remove(FhirContext fhirContext, IdType idType);

    ClinicalImpression findNotCache(FhirContext fhirContext, IdType idType);

    ClinicalImpression readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            ReferenceParam action,
            ReferenceParam assessor,
            ReferenceParam context,
            DateRangeParam date,
            TokenParam findingCode,
            ReferenceParam findingRef,
            TokenParam identifier,
            ReferenceParam investigation,
            ReferenceParam patient,
            ReferenceParam previous,
            ReferenceParam problem,
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
            ReferenceParam action,
            ReferenceParam assessor,
            ReferenceParam context,
            DateRangeParam date,
            TokenParam findingCode,
            ReferenceParam findingRef,
            TokenParam identifier,
            ReferenceParam investigation,
            ReferenceParam patient,
            ReferenceParam previous,
            ReferenceParam problem,
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
