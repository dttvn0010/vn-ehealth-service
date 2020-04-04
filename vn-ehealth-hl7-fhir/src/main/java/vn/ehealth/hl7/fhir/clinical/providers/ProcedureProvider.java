package vn.ehealth.hl7.fhir.clinical.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Procedure;
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
import vn.ehealth.hl7.fhir.clinical.dao.impl.ProcedureDao;
import vn.ehealth.hl7.fhir.clinical.entity.ProcedureEntity;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;

@Component
public class ProcedureProvider extends BaseController<ProcedureEntity, Procedure> implements IResourceProvider {

	@Autowired
	ProcedureDao procedureDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Procedure.class;
	}

	@Search
	public IBundleProvider searchProcedure(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam basedOn,
			@OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
			@OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
			@OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
			@OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
			@OptionalParam(name = ConstantKeys.SP_DEFINITION) ReferenceParam definition,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = Procedure.SP_INSTANTIATES_CANONICAL) ReferenceParam instantiatesCanonical,
			@OptionalParam(name = Procedure.SP_INSTANTIATES_URI) ReferenceParam instantiatesUri,
			@OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
			@OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partOf,
			@OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = ConstantKeys.SP_PERFORMER) ReferenceParam performer,
			@OptionalParam(name = Procedure.SP_REASON_CODE) TokenParam reasonCode,
			@OptionalParam(name = Procedure.SP_REASON_REFERENCE) ReferenceParam reasonReference,
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
			@IncludeParam(allow = { "Procedure:subject", "Procedure:encounter", "*" }) Set<Include> includes)
			throws OperationOutcomeException {
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<IBaseResource> results = new ArrayList<IBaseResource>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = procedureDao.search(basedOn, category, code, context, date, definition, encounter, identifier,
						location, partOf, patient, performer, status, subject, resid, _lastUpdated, _tag, _profile,
						_query, _security, _content, _page, sortParam, count, includes);
				// return results;
			} else
				results = procedureDao.search(basedOn, category, code, context, date, definition, encounter, identifier,
						location, partOf, patient, performer, status, subject, resid, _lastUpdated, _tag, _profile,
						_query, _security, _content, _page, null, count, includes);
			final List<IBaseResource> finalResults = results;

			return new IBundleProvider() {
				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(
							procedureDao.countMatchesAdvancedTotal(fhirContext, basedOn, category, code, context, date,
									definition, encounter, identifier, location, partOf, patient, performer, status,
									subject, resid, _lastUpdated, _tag, _profile, _query, _security, _content)));
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
			@OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam basedOn,
			@OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
			@OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
			@OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
			@OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OptionalParam(name = ConstantKeys.SP_DEFINITION) ReferenceParam definition,
			@OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
			@OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partOf,
			@OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = ConstantKeys.SP_PERFORMER) ReferenceParam performer,
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
		long total = procedureDao.countMatchesAdvancedTotal(fhirContext, basedOn, category, code, context, date,
				definition, encounter, identifier, location, partOf, patient, performer, status, subject, resid,
				_lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	@Override
	protected BaseDao<ProcedureEntity, Procedure> getDao() {
		return procedureDao;
	}
}
