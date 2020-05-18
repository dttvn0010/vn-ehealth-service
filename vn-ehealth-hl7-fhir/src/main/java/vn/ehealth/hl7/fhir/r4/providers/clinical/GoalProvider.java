package vn.ehealth.hl7.fhir.r4.providers.clinical;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Goal;
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
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.clinical.dao.impl.GoalDao;
import vn.ehealth.hl7.fhir.clinical.entity.GoalEntity;
import vn.ehealth.hl7.fhir.controller.BaseController;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeException;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeFactory;

@Component
public class GoalProvider extends BaseController<GoalEntity, Goal> implements IResourceProvider {
	@Autowired
	GoalDao goalDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Goal.class;
	}

	@Override
	protected List<String> getProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Search
	public IBundleProvider searchGoal(HttpServletRequest request,
			@OptionalParam(name = Goal.SP_ACHIEVEMENT_STATUS) TokenParam achievementStatus,
			@OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = Goal.SP_LIFECYCLE_STATUS) TokenParam lifecycleStatus,
			@OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = ConstantKeys.SP_START_DATE) DateRangeParam startDate,
			@OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
			@OptionalParam(name = ConstantKeys.SP_TARGET_DATE) DateRangeParam targetDate,
			// COMMON PARAMS
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
			@OptionalParam(name = ConstantKeys.SP_PAGE) NumberParam _page, @Sort SortSpec theSort, @Count Integer count,
			@IncludeParam(allow = { "Goal:subject", "*" }) Set<Include> includes) throws OperationOutcomeException {
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<IBaseResource> results = new ArrayList<>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = goalDao.search(fhirContext, category, identifier, patient, startDate, status, subject,
						targetDate, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam,
						count, includes);
				// return results;
			} else
				results = goalDao.search(fhirContext, category, identifier, patient, startDate, status, subject,
						targetDate, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, null,
						count, includes);
			// final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x
			// -> x);
			final List<IBaseResource> finalResults = results;

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(goalDao.countMatchesAdvancedTotal(fhirContext, category,
							identifier, patient, startDate, status, subject, targetDate, resid, _lastUpdated, _tag,
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
			@OperationParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
			@OperationParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OperationParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OperationParam(name = ConstantKeys.SP_START_DATE) DateRangeParam startDate,
			@OperationParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OperationParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
			@OperationParam(name = ConstantKeys.SP_TARGET_DATE) DateRangeParam targetDate,
			@OperationParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OperationParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OperationParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OperationParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OperationParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OperationParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OperationParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = goalDao.countMatchesAdvancedTotal(fhirContext, category, identifier, patient, startDate, status,
				subject, targetDate, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	@Override
	protected BaseDao<GoalEntity, Goal> getDao() {
		return goalDao;
	}
}
