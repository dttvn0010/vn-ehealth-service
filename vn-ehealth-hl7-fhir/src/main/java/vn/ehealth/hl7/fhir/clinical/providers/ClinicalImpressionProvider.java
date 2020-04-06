package vn.ehealth.hl7.fhir.clinical.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.ClinicalImpression;
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
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.providers.BaseController;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ClinicalImpressionDao;
import vn.ehealth.hl7.fhir.clinical.entity.ClinicalImpressionEntity;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;

@Component
public class ClinicalImpressionProvider extends BaseController<ClinicalImpressionEntity, ClinicalImpression>
		implements IResourceProvider {

	@Autowired
	ClinicalImpressionDao clinicalImpressionDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return ClinicalImpression.class;
	}

	@Search
	public IBundleProvider searchClinicalImpression(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ASSESSOR) ReferenceParam assessor,
			@OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OptionalParam(name = ClinicalImpression.SP_ENCOUNTER) ReferenceParam encounter,
			@OptionalParam(name = ConstantKeys.SP_FINDING_CODE) TokenParam findingCode,
			@OptionalParam(name = ConstantKeys.SP_FINDING_REF) ReferenceParam findingRef,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_INVESTIGATION) ReferenceParam investigation,
			@OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = ConstantKeys.SP_PREVIOUS) ReferenceParam previous,
			@OptionalParam(name = ConstantKeys.SP_PROBLEM) ReferenceParam problem,
			@OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
			@OptionalParam(name = ClinicalImpression.SP_SUPPORTING_INFO) ReferenceParam supportingInfo,
			@OptionalParam(name = ConstantKeys.SP_ACTION) ReferenceParam action,
			@OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
			// COMMON PARAMS
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
			@OptionalParam(name = ConstantKeys.SP_PAGE) NumberParam _page, @Sort SortSpec theSort, @Count Integer count,
			@IncludeParam(allow = { "ClinicalImpression:subject", "ClinicalImpression:encounter",
					"*" }) Set<Include> includes)
			throws OperationOutcomeException {
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<IBaseResource> results = new ArrayList<>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = clinicalImpressionDao.search(fhirContext, action, assessor, context, date, findingCode,
						findingRef, identifier, investigation, patient, previous, problem, status, subject, resid,
						_lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam, count, includes);
			} else
				results = clinicalImpressionDao.search(fhirContext, action, assessor, context, date, findingCode,
						findingRef, identifier, investigation, patient, previous, problem, status, subject, resid,
						_lastUpdated, _tag, _profile, _query, _security, _content, _page, null, count, includes);
			// final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x
			// -> x);
			final List<IBaseResource> finalResults = results;

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(clinicalImpressionDao.countMatchesAdvancedTotal(fhirContext,
							action, assessor, context, date, findingCode, findingRef, identifier, investigation,
							patient, previous, problem, status, subject, resid, _lastUpdated, _tag, _profile, _query,
							_security, _content)));
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
			@OptionalParam(name = ConstantKeys.SP_ACTION) ReferenceParam action,
			@OptionalParam(name = ConstantKeys.SP_ASSESSOR) ReferenceParam assessor,
			@OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
			@OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OptionalParam(name = ConstantKeys.SP_FINDING_CODE) TokenParam findingCode,
			@OptionalParam(name = ConstantKeys.SP_FINDING_REF) ReferenceParam findingRef,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_INVESTIGATION) ReferenceParam investigation,
			@OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = ConstantKeys.SP_PREVIOUS) ReferenceParam previous,
			@OptionalParam(name = ConstantKeys.SP_PROBLEM) ReferenceParam problem,
			@OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = clinicalImpressionDao.countMatchesAdvancedTotal(fhirContext, action, assessor, context, date,
				findingCode, findingRef, identifier, investigation, patient, previous, problem, status, subject, resid,
				_lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	@Override
	protected BaseDao<ClinicalImpressionEntity, ClinicalImpression> getDao() {
		return clinicalImpressionDao;
	}
}
