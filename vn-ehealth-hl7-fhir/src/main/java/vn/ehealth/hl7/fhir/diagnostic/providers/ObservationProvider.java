package vn.ehealth.hl7.fhir.diagnostic.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.providers.BaseController;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.ObservationDao;
import vn.ehealth.hl7.fhir.diagnostic.entity.ObservationEntity;

@Component
public class ObservationProvider extends BaseController<ObservationEntity, Observation> implements IResourceProvider {
	@Autowired
	ObservationDao observationDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Observation.class;
	}

	@Search
	public IBundleProvider searchObservation(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam basedOn,
			@OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
			@OptionalParam(name = ConstantKeys.SP_CODE) TokenOrListParam code,
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
			@OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Sort SortSpec theSort, @Count Integer count)
			throws OperationOutcomeException {
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<Resource> results = new ArrayList<Resource>();
			var codeitem = new TokenParam();
			if (code != null && code.getValuesAsQueryTokens().size() > 0)
				codeitem = code.getValuesAsQueryTokens().get(0);
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = observationDao.search(fhirContext, active, basedOn, category, codeitem, comboCode,
						comboDataAbsentReason, comboValueConcept, componentCode, componentDataAbsentReason,
						componentValueConcept, conetext, dataAbsentReason, date, device, encounter, identifier, method,
						patient, performer, relatedTarget, relatedType, specimen, status, subject, valueConcept,
						valueDate, valueString, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page,
						sortParam, count);
			} else
				results = observationDao.search(fhirContext, active, basedOn, category, codeitem, comboCode,
						comboDataAbsentReason, comboValueConcept, componentCode, componentDataAbsentReason,
						componentValueConcept, conetext, dataAbsentReason, date, device, encounter, identifier, method,
						patient, performer, relatedTarget, relatedType, specimen, status, subject, valueConcept,
						valueDate, valueString, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page,
						null, count);
			final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);
			final var codeToSearch = codeitem;
			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(observationDao.countMatchesAdvancedTotal(fhirContext, active,
							basedOn, category, codeToSearch, comboCode, comboDataAbsentReason, comboValueConcept,
							componentCode, componentDataAbsentReason, componentValueConcept, conetext, dataAbsentReason,
							date, device, encounter, identifier, method, patient, performer, relatedTarget, relatedType,
							specimen, status, subject, valueConcept, valueDate, valueString, resid, _lastUpdated, _tag,
							_profile, _query, _security, _content)));
				}

				@Override
				public Integer preferredPageSize() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getUuid() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
					return finalResults;
				}

				@Override
				public IPrimitiveType<Date> getPublished() {
					// TODO Auto-generated method stub
					return null;
				}
			};
		}
	}

	@Operation(name = "$total", idempotent = true)
	public Parameters getTotal(HttpServletRequest request,
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
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = observationDao.countMatchesAdvancedTotal(fhirContext, active, basedOn, category, code, comboCode,
				comboDataAbsentReason, comboValueConcept, componentCode, componentDataAbsentReason,
				componentValueConcept, conetext, dataAbsentReason, date, device, encounter, identifier, method, patient,
				performer, relatedTarget, relatedType, specimen, status, subject, valueConcept, valueDate, valueString,
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}
	
	@Operation(name = "$lastn", idempotent = true)
    public List<Resource> getLastNObservation(HttpServletRequest request,
    		@RequiredParam(name = ConstantKeys.SP_OBSERVATION_MAX) NumberParam maxItem,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam basedOn,
            @RequiredParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @RequiredParam(name = ConstantKeys.SP_CODE) TokenOrListParam code,
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
            @RequiredParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
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
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Sort SortSpec theSort, @Count Integer count)
            throws OperationOutcomeException {
        if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("Total is not gre than " +  ConstantKeys.DEFAULT_PAGE_MAX_SIZE), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.NOTSUPPORTED);
        } else if (code == null) {
       	 throw OperationOutcomeFactory.buildOperationOutcomeException(
                 new ResourceNotFoundException("Code Param is required!"), OperationOutcome.IssueSeverity.ERROR,
                 OperationOutcome.IssueType.PROCESSING);
        } else if (maxItem == null | maxItem.getValue().intValue() == 0) {
       	 throw OperationOutcomeFactory.buildOperationOutcomeException(
                 new ResourceNotFoundException("Max item is needed to be greater than 0"), OperationOutcome.IssueSeverity.ERROR,
                 OperationOutcome.IssueType.PROCESSING);
        } 
        else {
        	List<Resource> results = new ArrayList<>();
            if (code !=null && code.getValuesAsQueryTokens().size() > 0) {
            	for (TokenParam codeitem : code.getValuesAsQueryTokens()) {            		
            		List<Resource> subResults = observationDao.search(fhirContext, active, basedOn, category, codeitem, comboCode,
                            comboDataAbsentReason, comboValueConcept, componentCode, componentDataAbsentReason,
                            componentValueConcept, conetext, dataAbsentReason, date, device, encounter, identifier, method,
                            patient, performer, relatedTarget, relatedType, specimen, status, subject, valueConcept, valueDate,
                            valueString, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, 
                            (theSort != null ? theSort.getParamName() : null) , maxItem.getValue().intValue());
            		if (subResults != null && subResults.size() > 0)
            			results.addAll(subResults);
            	} 
            }
            return results;
        }
    }

    @Override
    protected BaseDao<ObservationEntity, Observation> getDao() {
        return observationDao;
    }
}
