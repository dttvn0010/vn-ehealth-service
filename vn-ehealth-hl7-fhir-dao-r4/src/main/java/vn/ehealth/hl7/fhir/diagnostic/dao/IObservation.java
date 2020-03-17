package vn.ehealth.hl7.fhir.diagnostic.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;

public interface IObservation {

    Observation create(FhirContext fhirContext, Observation object);

    Observation update(FhirContext fhirContext, Observation object, IdType idType);

    Observation read(FhirContext fhirContext, IdType idType);

    Observation remove(FhirContext fhirContext, IdType idType);
    
    Observation readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam basedOn,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = ConstantKeys.SP_COMBO_CODE) TokenParam comboCode,
            @OptionalParam(name = ConstantKeys.SP_COMBO_DATA_ABSENT_REASON) TokenParam comboDataAbsentReason,
            @OptionalParam(name = ConstantKeys.SP_COMBO_CODE_VALUE_CONCEPT) TokenParam comboValueConcept,
            @OptionalParam(name = ConstantKeys.SP_COMPONENT_CODE) TokenParam componentCode,
            @OptionalParam(name = ConstantKeys.SP_COMPONENT_DATA_ABSENT_REASON) TokenParam componentDataAbsentReason,
            @OptionalParam(name = ConstantKeys.SP_COMPONENT_VALUE_CONCEPT) TokenParam componentValueConcept,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam conetext,
            @OptionalParam(name = ConstantKeys.SP_DATA_ABSENT_REASON) TokenParam dataAbsentReason,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DEVICE) ReferenceParam device,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_METHOD) TokenParam method,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PERFORMER) ReferenceParam performer,
            @OptionalParam(name = ConstantKeys.SP_RELATED_TARGET) ReferenceParam relatedTarget,
            @OptionalParam(name = ConstantKeys.SP_RELATED_TYPE) TokenParam relatedType,
            @OptionalParam(name = ConstantKeys.SP_SPECIMEN) ReferenceParam specimen,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_VALUE_CONCEPT) TokenParam valueConcept,
            @OptionalParam(name = ConstantKeys.SP_VALUE_DATE) DateRangeParam valueDate,
            @OptionalParam(name = ConstantKeys.SP_VALUE_STRING) StringParam valueString,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, String sortParam, Integer count);

    long countMatchesAdvancedTotal(FhirContext fhirContext,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam basedOn,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = ConstantKeys.SP_COMBO_CODE) TokenParam comboCode,
            @OptionalParam(name = ConstantKeys.SP_COMBO_DATA_ABSENT_REASON) TokenParam comboDataAbsentReason,
            @OptionalParam(name = ConstantKeys.SP_COMBO_CODE_VALUE_CONCEPT) TokenParam comboValueConcept,
            @OptionalParam(name = ConstantKeys.SP_COMPONENT_CODE) TokenParam componentCode,
            @OptionalParam(name = ConstantKeys.SP_COMPONENT_DATA_ABSENT_REASON) TokenParam componentDataAbsentReason,
            @OptionalParam(name = ConstantKeys.SP_COMPONENT_VALUE_CONCEPT) TokenParam componentValueConcept,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam conetext,
            @OptionalParam(name = ConstantKeys.SP_DATA_ABSENT_REASON) TokenParam dataAbsentReason,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DEVICE) ReferenceParam device,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_METHOD) TokenParam method,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PERFORMER) ReferenceParam performer,
            @OptionalParam(name = ConstantKeys.SP_RELATED_TARGET) ReferenceParam relatedTarget,
            @OptionalParam(name = ConstantKeys.SP_RELATED_TYPE) TokenParam relatedType,
            @OptionalParam(name = ConstantKeys.SP_SPECIMEN) ReferenceParam specimen,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_VALUE_CONCEPT) TokenParam valueConcept,
            @OptionalParam(name = ConstantKeys.SP_VALUE_DATE) DateRangeParam valueDate,
            @OptionalParam(name = ConstantKeys.SP_VALUE_STRING) StringParam valueString,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content);
}
