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

    List<Resource> search(FhirContext fhirContext, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = ConstantKeys.SP_CONTAINER) TokenParam container,
            @OptionalParam(name = ConstantKeys.SP_FORM) TokenParam form,
            @OptionalParam(name = ConstantKeys.SP_INGREDIENT) ReferenceParam ingredient,
            @OptionalParam(name = ConstantKeys.SP_INGREDIENT_CODE) TokenParam ingredientCode,
            @OptionalParam(name = ConstantKeys.SP_MANUFACTURER) ReferenceParam manufacturer,
            @OptionalParam(name = ConstantKeys.SP_OVER_THE_COUNTER) TokenParam overTheCounter,
            @OptionalParam(name = ConstantKeys.SP_PACKAGE_ITEM) ReferenceParam packageItem,
            @OptionalParam(name = ConstantKeys.SP_PACKAGE_ITEM_CODE) TokenParam packageItemCode,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
            @OptionalParam(name = "hospital") TokenParam hospital,
            @OptionalParam(name = "productName") StringParam productName,
            @OptionalParam(name = "medicationType") StringParam medicationType,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, String sortParam, Integer count);

    long countMatchesAdvancedTotal(FhirContext fhirContext,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = ConstantKeys.SP_CONTAINER) TokenParam container,
            @OptionalParam(name = ConstantKeys.SP_FORM) TokenParam form,
            @OptionalParam(name = ConstantKeys.SP_INGREDIENT) ReferenceParam ingredient,
            @OptionalParam(name = ConstantKeys.SP_INGREDIENT_CODE) TokenParam ingredientCode,
            @OptionalParam(name = ConstantKeys.SP_MANUFACTURER) ReferenceParam manufacturer,
            @OptionalParam(name = ConstantKeys.SP_OVER_THE_COUNTER) TokenParam overTheCounter,
            @OptionalParam(name = ConstantKeys.SP_PACKAGE_ITEM) ReferenceParam packageItem,
            @OptionalParam(name = ConstantKeys.SP_PACKAGE_ITEM_CODE) TokenParam packageItemCode,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
            @OptionalParam(name = "hospital") TokenParam hospital,
            @OptionalParam(name = "productName") StringParam productName,
            @OptionalParam(name = "medicationType") StringParam medicationType);
}
