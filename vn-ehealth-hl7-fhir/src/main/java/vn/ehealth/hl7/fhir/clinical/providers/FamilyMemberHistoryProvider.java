package vn.ehealth.hl7.fhir.clinical.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
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
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.clinical.dao.impl.FamilyMemberHistoryDao;
import vn.ehealth.hl7.fhir.clinical.entity.FamilyMemberHistoryEntity;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.providers.BaseController;

@Component
public class FamilyMemberHistoryProvider extends BaseController<FamilyMemberHistoryEntity, FamilyMemberHistory>
		implements IResourceProvider {

	@Autowired
	FamilyMemberHistoryDao baseDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return FamilyMemberHistory.class;
	}

	@Override
	protected BaseDao<FamilyMemberHistoryEntity, FamilyMemberHistory> getDao() {
		return baseDao;
	}

	@Search
	public IBundleProvider search(HttpServletRequest request,
			@OptionalParam(name = FamilyMemberHistory.SP_CODE) TokenParam code,
			@OptionalParam(name = FamilyMemberHistory.SP_DATE) DateRangeParam date,
			@OptionalParam(name = FamilyMemberHistory.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = FamilyMemberHistory.SP_INSTANTIATES_CANONICAL) ReferenceParam instantiatesCanonical,
			@OptionalParam(name = FamilyMemberHistory.SP_INSTANTIATES_URI) UriParam instantiatesUri,
			@OptionalParam(name = FamilyMemberHistory.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = FamilyMemberHistory.SP_RELATIONSHIP) TokenParam relationship,
			@OptionalParam(name = ConstantKeys.SP_SEX) TokenParam gender,
			@OptionalParam(name = FamilyMemberHistory.SP_STATUS) TokenParam status,

			// Common
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
			@OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Sort SortSpec theSort, @Count Integer count,
			@IncludeParam(allow = { "FamilyMemberHistory:patient", "*" }) Set<Include> includes)
			throws OperationOutcomeException {
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<IBaseResource> results = new ArrayList<IBaseResource>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = baseDao.search(code, date, identifier, instantiatesCanonical, instantiatesUri, patient,
						relationship, gender, status,
						// Common
						resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam, count,
						includes);
			} else
				results = baseDao.search(code, date, identifier, instantiatesCanonical, instantiatesUri, patient,
						relationship, gender, status,
						// Common
						resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, null, count, includes);
			final List<IBaseResource> finalResults = results;

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(baseDao.countMatchesAdvancedTotal(code, date, identifier,
							instantiatesCanonical, instantiatesUri, patient, relationship, gender, status,
							// Common
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
			@OptionalParam(name = FamilyMemberHistory.SP_CODE) TokenParam code,
			@OptionalParam(name = FamilyMemberHistory.SP_DATE) DateRangeParam date,
			@OptionalParam(name = FamilyMemberHistory.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = FamilyMemberHistory.SP_INSTANTIATES_CANONICAL) ReferenceParam instantiatesCanonical,
			@OptionalParam(name = FamilyMemberHistory.SP_INSTANTIATES_URI) UriParam instantiatesUri,
			@OptionalParam(name = FamilyMemberHistory.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = FamilyMemberHistory.SP_RELATIONSHIP) TokenParam relationship,
			@OptionalParam(name = FamilyMemberHistory.SP_GENDER) TokenParam gender,
			@OptionalParam(name = FamilyMemberHistory.SP_STATUS) TokenParam status,

			// Common
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = baseDao.countMatchesAdvancedTotal(code, date, identifier, instantiatesCanonical, instantiatesUri,
				patient, relationship, gender, status,
				// Common
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}
}
