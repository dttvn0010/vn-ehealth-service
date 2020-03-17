package vn.ehealth.hl7.fhir.medication.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Medication;
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
public interface IMedication {

    Medication create(FhirContext fhirContext, Medication object);

    Medication update(FhirContext fhirContext, Medication object, IdType idType);

    Medication read(FhirContext fhirContext, IdType idType);

    Medication remove(FhirContext fhirContext, IdType idType);

    Medication readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            TokenParam code,
            TokenParam container,
            TokenParam form,
            ReferenceParam ingredient,
            TokenParam ingredientCode,
            ReferenceParam manufacturer,
            TokenParam overTheCounter,
            ReferenceParam packageItem,
            TokenParam packageItemCode,
            TokenParam status,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content,
            TokenParam hospital,
            StringParam productName,
            StringParam medicationType,
            StringParam _page, String sortParam, Integer count);

    long countMatchesAdvancedTotal(FhirContext fhirContext,
            TokenParam active,
            TokenParam code,
            TokenParam container,
            TokenParam form,
            ReferenceParam ingredient,
            TokenParam ingredientCode,
            ReferenceParam manufacturer,
            TokenParam overTheCounter,
            ReferenceParam packageItem,
            TokenParam packageItemCode,
            TokenParam status,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content,
            TokenParam hospital,
            StringParam productName,
            StringParam medicationType);
}
