package vn.ehealth.hl7.fhir.r4.providers.clinical;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
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
import vn.ehealth.hl7.fhir.clinical.dao.impl.AllergyIntoleranceDao;
import vn.ehealth.hl7.fhir.clinical.entity.AllergyIntoleranceEntity;
import vn.ehealth.hl7.fhir.controller.BaseController;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeException;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeFactory;

@Component
public class AllergyIntoleranceProvider extends BaseController<AllergyIntoleranceEntity, AllergyIntolerance>
		implements IResourceProvider {

	@Autowired
	AllergyIntoleranceDao baseDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return AllergyIntolerance.class;
	}

	@Override
	protected BaseDao<AllergyIntoleranceEntity, AllergyIntolerance> getDao() {
		return baseDao;
	}

	@Override
	protected List<String> getProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Search
	public IBundleProvider search(HttpServletRequest request,
			@OptionalParam(name = AllergyIntolerance.SP_ASSERTER) ReferenceParam asserter,
			@OptionalParam(name = AllergyIntolerance.SP_CATEGORY) TokenParam category,
			@OptionalParam(name = AllergyIntolerance.SP_CLINICAL_STATUS) TokenParam clinicalStatus,
			@OptionalParam(name = AllergyIntolerance.SP_CODE) TokenParam code,
			@OptionalParam(name = AllergyIntolerance.SP_CRITICALITY) TokenParam criticality,
			@OptionalParam(name = AllergyIntolerance.SP_DATE) DateRangeParam date,
			@OptionalParam(name = AllergyIntolerance.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = AllergyIntolerance.SP_LAST_DATE) DateRangeParam lastDate,
			@OptionalParam(name = AllergyIntolerance.SP_MANIFESTATION) TokenParam manifestation,
			@OptionalParam(name = AllergyIntolerance.SP_ONSET) DateRangeParam onset,
			@OptionalParam(name = AllergyIntolerance.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = AllergyIntolerance.SP_RECORDER) ReferenceParam recorder,
			@OptionalParam(name = AllergyIntolerance.SP_ROUTE) TokenParam route,
			@OptionalParam(name = AllergyIntolerance.SP_SEVERITY) TokenParam severity,
			@OptionalParam(name = AllergyIntolerance.SP_TYPE) TokenParam type,
			@OptionalParam(name = AllergyIntolerance.SP_VERIFICATION_STATUS) TokenParam verificationStatus,
			@OptionalParam(name = ConstantKeys.SP_ENCOUNTER) TokenParam encounter,
			// COMMON PARAMS
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
			@OptionalParam(name = ConstantKeys.SP_PAGE) NumberParam _page, @Sort SortSpec theSort, @Count Integer count,
			@IncludeParam(allow = { "AllergyIntorance:patient", "AllergyIntorance:encounter",
					"*" }) Set<Include> includes)
			throws OperationOutcomeException {
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<IBaseResource> results = new ArrayList<IBaseResource>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = baseDao.search(encounter, asserter, category, clinicalStatus, code, criticality, date,
						identifier, lastDate, manifestation, onset, patient, recorder, route, severity, type,
						verificationStatus,
						// COMMON PARAMS
						resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam, count,
						includes);
				// return results;
			} else
				results = baseDao.search(encounter, asserter, category, clinicalStatus, code, criticality, date,
						identifier, lastDate, manifestation, onset, patient, recorder, route, severity, type,
						verificationStatus,
						// COMMON PARAMS
						resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, null, count, includes);
			final List<IBaseResource> finalResults = results;

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(baseDao.countMatchesAdvancedTotal(encounter, asserter,
							category, clinicalStatus, code, criticality, date, identifier, lastDate, manifestation,
							onset, patient, recorder, route, severity, type, verificationStatus,
							// COMMON PARAMS
							resid, _lastUpdated, _tag, _profile, _query, _security, _content)));
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
			@OptionalParam(name = ConstantKeys.SP_ENCOUNTER) TokenParam encounter,
			@OptionalParam(name = AllergyIntolerance.SP_ASSERTER) ReferenceParam asserter,
			@OptionalParam(name = AllergyIntolerance.SP_CATEGORY) TokenParam category,
			@OptionalParam(name = AllergyIntolerance.SP_CLINICAL_STATUS) TokenParam clinicalStatus,
			@OptionalParam(name = AllergyIntolerance.SP_CODE) TokenParam code,
			@OptionalParam(name = AllergyIntolerance.SP_CRITICALITY) TokenParam criticality,
			@OptionalParam(name = AllergyIntolerance.SP_DATE) DateRangeParam date,
			@OptionalParam(name = AllergyIntolerance.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = AllergyIntolerance.SP_LAST_DATE) DateRangeParam lastDate,
			@OptionalParam(name = AllergyIntolerance.SP_MANIFESTATION) TokenParam manifestation,
			@OptionalParam(name = AllergyIntolerance.SP_ONSET) DateRangeParam onset,
			@OptionalParam(name = AllergyIntolerance.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = AllergyIntolerance.SP_RECORDER) ReferenceParam recorder,
			@OptionalParam(name = AllergyIntolerance.SP_ROUTE) TokenParam route,
			@OptionalParam(name = AllergyIntolerance.SP_SEVERITY) TokenParam severity,
			@OptionalParam(name = AllergyIntolerance.SP_TYPE) TokenParam type,
			@OptionalParam(name = AllergyIntolerance.SP_VERIFICATION_STATUS) TokenParam verificationStatus,
			// COMMON PARAMS
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = baseDao.countMatchesAdvancedTotal(encounter, asserter, category, clinicalStatus, code, criticality,
				date, identifier, lastDate, manifestation, onset, patient, recorder, route, severity, type,
				verificationStatus,
				// COMMON PARAMS
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}
}
