package vn.ehealth.hl7.fhir.r4.providers.diagnostic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.valueset.BundleTypeEnum;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
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
import vn.ehealth.hl7.fhir.controller.BaseController;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.DiagnosticReportDao;
import vn.ehealth.hl7.fhir.diagnostic.entity.DiagnosticReportEntity;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeException;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeFactory;

@Component
public class DiagnosticReportProvider extends BaseController<DiagnosticReportEntity, DiagnosticReport>
		implements IResourceProvider {

	@Autowired
	DiagnosticReportDao baseDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return DiagnosticReport.class;
	}

	@Override
	protected List<String> getProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Search
	public IBundleProvider searchDiagnosticReport(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam basedOn,
			@OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
			@OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
			@OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
			@OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
			@OptionalParam(name = ConstantKeys.SP_DIAGNOSIS) TokenParam diagnosis,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_IMAGE) ReferenceParam image,
			@OptionalParam(name = ConstantKeys.SP_ISSUED) DateRangeParam issued,
			@OptionalParam(name = DiagnosticReport.SP_MEDIA) ReferenceParam media,
			@OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = ConstantKeys.SP_PERFORMER) ReferenceParam performer,
			@OptionalParam(name = ConstantKeys.SP_RESULT) ReferenceParam result,
			@OptionalParam(name = DiagnosticReport.SP_RESULTS_INTERPRETER) ReferenceParam resultsInterpreter,
			@OptionalParam(name = ConstantKeys.SP_SPECIMEN) ReferenceParam specimen,
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
			@IncludeParam(allow = { "DiagnosticReport:subject", "DiagnosticReport:encounter",
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
				results = baseDao.search(fhirContext, basedOn, category, code, context, date, diagnosis, encounter,
						identifier, image, issued, patient, performer, result, specimen, status, subject, resid,
						_lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam, count, includes);
				// return results;
			} else
				results = baseDao.search(fhirContext, basedOn, category, code, context, date, diagnosis, encounter,
						identifier, image, issued, patient, performer, result, specimen, status, subject, resid,
						_lastUpdated, _tag, _profile, _query, _security, _content, _page, null, count, includes);
			// final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x
			// -> x);
			final List<IBaseResource> finalResults = results;

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(baseDao.countMatchesAdvancedTotal(fhirContext, basedOn,
							category, code, context, date, diagnosis, encounter, identifier, image, issued, patient,
							performer, result, specimen, status, subject, resid, _lastUpdated, _tag, _profile, _query,
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
			@OperationParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam basedOn,
			@OperationParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
			@OperationParam(name = ConstantKeys.SP_CODE) TokenParam code,
			@OperationParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam conetext,
			@OperationParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OperationParam(name = ConstantKeys.SP_DIAGNOSIS) TokenParam diagnosis,
			@OperationParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
			@OperationParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OperationParam(name = ConstantKeys.SP_IMAGE) ReferenceParam image,
			@OperationParam(name = ConstantKeys.SP_ISSUED) DateRangeParam issued,
			@OperationParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OperationParam(name = ConstantKeys.SP_PERFORMER) ReferenceParam performer,
			@OperationParam(name = ConstantKeys.SP_RESULT) ReferenceParam result,
			@OperationParam(name = ConstantKeys.SP_SPECIMEN) ReferenceParam specimen,
			@OperationParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OperationParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
			@OperationParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OperationParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OperationParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OperationParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OperationParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OperationParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OperationParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = baseDao.countMatchesAdvancedTotal(fhirContext, basedOn, category, code, conetext, date, diagnosis,
				encounter, identifier, image, issued, patient, performer, result, specimen, status, subject, resid,
				_lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	@Operation(name = "$document", idempotent = true, bundleType = BundleTypeEnum.DOCUMENT)
	public IBundleProvider generate(HttpServletRequest request, @IdParam IdType theId,
			@OperationParam(name = ConstantKeys.SP_PERSIST) StringParam persist) {
		List<IBaseResource> results = new ArrayList<IBaseResource>();
		// Populate bundle with matching resources
		System.out.println("========================="  + persist);
		results = baseDao.generate(theId);
		if (results == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No " + theId.getValue() + " found"),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}

		final List<IBaseResource> finalResults = results;

		return new IBundleProvider() {

			@Override
			public Integer size() {
				return finalResults.size();
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

	@Override
	protected BaseDao<DiagnosticReportEntity, DiagnosticReport> getDao() {
		return baseDao;
	}
}
