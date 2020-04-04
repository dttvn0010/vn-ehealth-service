package vn.ehealth.hl7.fhir.diagnostic.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.ImagingStudy;
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
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.ImagingStudyDao;
import vn.ehealth.hl7.fhir.diagnostic.entity.ImagingStudyEntity;

@Component
public class ImagingStudyProvider extends BaseController<ImagingStudyEntity, ImagingStudy>
		implements IResourceProvider {

	@Autowired
	ImagingStudyDao imagingStudyDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return ImagingStudy.class;
	}

	@Search
	public IBundleProvider searchImagingStudy(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam basedOn,
			@OptionalParam(name = ImagingStudy.SP_BODYSITE) TokenParam bodysite,
			@OptionalParam(name = ImagingStudy.SP_DICOM_CLASS) TokenParam dicomClass,
			@OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
			@OptionalParam(name = ImagingStudy.SP_ENDPOINT) ReferenceParam endpoint,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ImagingStudy.SP_INSTANCE) TokenParam instance,
			@OptionalParam(name = ImagingStudy.SP_INTERPRETER) ReferenceParam interpreter,
			@OptionalParam(name = ImagingStudy.SP_MODALITY) TokenParam modality,
			@OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = ConstantKeys.SP_PERFORMER) ReferenceParam performer,
			@OptionalParam(name = ImagingStudy.SP_REASON) TokenParam reason,
			@OptionalParam(name = ImagingStudy.SP_REFERRER) ReferenceParam referrer,
			@OptionalParam(name = ImagingStudy.SP_SERIES) TokenParam series,
			@OptionalParam(name = ImagingStudy.SP_STARTED) DateRangeParam started,
			@OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
			// COMMON PARAMS
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
			@OptionalParam(name = ConstantKeys.SP_PAGE) NumberParam _page, @Sort SortSpec theSort, @Count Integer count,
			@IncludeParam(allow = { "ImagingStudy:subject", "ImagingStudy:encounter", "*" }) Set<Include> includes)
			throws OperationOutcomeException {
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<IBaseResource> results = new ArrayList<>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = imagingStudyDao.search(fhirContext, resid, _lastUpdated, _tag, _profile, _query, _security,
						_content, _page, sortParam, count, includes);
				// return results;
			} else
				results = imagingStudyDao.search(fhirContext, resid, _lastUpdated, _tag, _profile, _query, _security,
						_content, _page, null, count, includes);
			// final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x
			// -> x);
			final List<IBaseResource> finalResults = results;

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(imagingStudyDao.countMatchesAdvancedTotal(fhirContext, resid,
							_lastUpdated, _tag, _profile, _query, _security, _content)));
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
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = imagingStudyDao.countMatchesAdvancedTotal(fhirContext, resid, _lastUpdated, _tag, _profile, _query,
				_security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	@Override
	protected BaseDao<ImagingStudyEntity, ImagingStudy> getDao() {
		return imagingStudyDao;
	}
}
