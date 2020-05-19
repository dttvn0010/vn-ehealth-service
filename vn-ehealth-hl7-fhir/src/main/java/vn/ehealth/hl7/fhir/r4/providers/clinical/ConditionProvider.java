package vn.ehealth.hl7.fhir.r4.providers.clinical;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ConditionDao;
import vn.ehealth.hl7.fhir.clinical.entity.ConditionEntity;
import vn.ehealth.hl7.fhir.controller.BaseController;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeException;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeFactory;

@Component
public class ConditionProvider extends BaseController<ConditionEntity, Condition> implements IResourceProvider {

	@Autowired
	ConditionDao conditionDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Condition.class;
	}

	@Override
	protected List<String> getProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Search
	public IBundleProvider searchCondition(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ABATEMENT_AGE) QuantityParam abatementAge,
			@OptionalParam(name = ConstantKeys.SP_ABATEMENT_DATE) DateRangeParam abatementDate,
			@OptionalParam(name = ConstantKeys.SP_ABATEMENT_BOOLEAN) TokenParam abatementBoolean,
			@OptionalParam(name = ConstantKeys.SP_ABATEMENT_STRING) TokenParam abatementString,
			@OptionalParam(name = ConstantKeys.SP_ASSERTER) ReferenceParam asserter,
			@OptionalParam(name = ConstantKeys.SP_ASSERTED_DATE) DateRangeParam assertedDate,
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
			@OptionalParam(name = Condition.SP_RECORDED_DATE) DateRangeParam recordedDate,
			@OptionalParam(name = ConstantKeys.SP_SEVERITY) TokenParam severity,
			@OptionalParam(name = ConstantKeys.SP_STAGE) TokenParam stage,
			@OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
			@OptionalParam(name = ConstantKeys.SP_VERIFICATION_STATUS) TokenParam verificationStatus,
			// COMMON PARAMS
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
			@OptionalParam(name = ConstantKeys.SP_PAGE) NumberParam _page, @Sort SortSpec theSort, @Count Integer count,
			@IncludeParam(allow = { "Condition:subject", "Condition:encounter", "*" }) Set<Include> includes)
			throws OperationOutcomeException {
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<IBaseResource> results = new ArrayList<>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = conditionDao.search(fhirContext, abatementAge, abatementBoolean, abatementDate,
						abatementString, assertedDate, asserter, bodySite, category, clinicalStatus, code, context,
						encounter, evidence, evidenceDetail, identifier, onsetAge, onseDate, onsetInfo, patient,
						severity, stage, subject, verificationStatus, resid, _lastUpdated, _tag, _profile, _query,
						_security, _content, _page, sortParam, count, includes);
				// return results;
			} else
				results = conditionDao.search(fhirContext, abatementAge, abatementBoolean, abatementDate,
						abatementString, assertedDate, asserter, bodySite, category, clinicalStatus, code, context,
						encounter, evidence, evidenceDetail, identifier, onsetAge, onseDate, onsetInfo, patient,
						severity, stage, subject, verificationStatus, resid, _lastUpdated, _tag, _profile, _query,
						_security, _content, _page, null, count, includes);

			// final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x
			// -> x);
			final List<IBaseResource> finalResults = results;

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(conditionDao.countMatchesAdvancedTotal(fhirContext,
							abatementAge, abatementBoolean, abatementDate, abatementString, assertedDate, asserter,
							bodySite, category, clinicalStatus, code, context, encounter, evidence, evidenceDetail,
							identifier, onsetAge, onseDate, onsetInfo, patient, severity, stage, subject,
							verificationStatus, resid, _lastUpdated, _tag, _profile, _query, _security, _content)));
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
					// TODO Auto-generated method stub
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
			@OperationParam(name = ConstantKeys.SP_ABATEMENT_AGE) QuantityParam abatementAge,
			@OperationParam(name = ConstantKeys.SP_ABATEMENT_BOOLEAN) TokenParam abatementBoolean,
			@OperationParam(name = ConstantKeys.SP_ABATEMENT_DATE) DateRangeParam abatementDate,
			@OperationParam(name = ConstantKeys.SP_ABATEMENT_STRING) TokenParam abatementString,
			@OperationParam(name = ConstantKeys.SP_ASSERTED_DATE) DateRangeParam assertedDate,
			@OperationParam(name = ConstantKeys.SP_ASSERTER) ReferenceParam asserter,
			@OperationParam(name = ConstantKeys.SP_BODY_SITE) TokenParam bodySite,
			@OperationParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
			@OperationParam(name = ConstantKeys.SP_CLINICAL_STATUS) TokenParam clinicalStatus,
			@OperationParam(name = ConstantKeys.SP_CODE) TokenParam code,
			@OperationParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
			@OperationParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
			@OperationParam(name = ConstantKeys.SP_EVIDENCE) TokenParam evidence,
			@OperationParam(name = ConstantKeys.SP_EVIDENCE_DETAIL) ReferenceParam evidenceDetail,
			@OperationParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OperationParam(name = ConstantKeys.SP_ONSET_AGE) QuantityParam onsetAge,
			@OperationParam(name = ConstantKeys.SP_ONSET_DATE) DateRangeParam onseDate,
			@OperationParam(name = ConstantKeys.SP_ONSET_INFO) StringParam onsetInfo,
			@OperationParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OperationParam(name = ConstantKeys.SP_SEVERITY) TokenParam severity,
			@OperationParam(name = ConstantKeys.SP_STAGE) TokenParam stage,
			@OperationParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
			@OperationParam(name = ConstantKeys.SP_VERIFICATION_STATUS) TokenParam verificationStatus,
			@OperationParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OperationParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OperationParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OperationParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OperationParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OperationParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OperationParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = conditionDao.countMatchesAdvancedTotal(fhirContext, abatementAge, abatementBoolean, abatementDate,
				abatementString, assertedDate, asserter, bodySite, category, clinicalStatus, code, context, encounter,
				evidence, evidenceDetail, identifier, onsetAge, onseDate, onsetInfo, patient, severity, stage, subject,
				verificationStatus, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	@Override
	protected BaseDao<ConditionEntity, Condition> getDao() {
		return conditionDao;
	}
}
