package vn.ehealth.hl7.fhir.r4.providers.ehr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Encounter;
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
import vn.ehealth.hl7.fhir.controller.BaseController;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.ehr.entity.EncounterEntity;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeException;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeFactory;

@Component
public class EncounterProvider extends BaseController<EncounterEntity, Encounter> implements IResourceProvider {

	@Autowired
	EncounterDao encounterDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Encounter.class;
	}

	@Search
	public IBundleProvider searchEncounter(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_APPOINTMENT) ReferenceParam appointment,
			@OptionalParam(name = ConstantKeys.SP_CLASS) TokenParam _class,
			@OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OptionalParam(name = ConstantKeys.SP_DIAGNOSIS) ReferenceParam diagnosis,
			@OptionalParam(name = ConstantKeys.SP_EPISODEOFCARE) ReferenceParam episodeofcare,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_INCOMINGREFERRAL) ReferenceParam incomingreferral,
			@OptionalParam(name = ConstantKeys.SP_LENGHTH) NumberParam length,
			@OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
			@OptionalParam(name = ConstantKeys.SP_LOCATION_PERIOD) DateRangeParam locationPeriod,
			@OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partOf,
			@OptionalParam(name = ConstantKeys.SP_PARTICIPANT) ReferenceParam participant,
			@OptionalParam(name = ConstantKeys.SP_PARTICIPANT_TYPE) TokenParam participantType,
			@OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
			@OptionalParam(name = ConstantKeys.SP_REASON) TokenParam reason,
			@OptionalParam(name = ConstantKeys.SP_SERVICE_PROVIDER) ReferenceParam serviceProvider,
			@OptionalParam(name = ConstantKeys.SP_SPECIAL_ARRANGEMENT) TokenParam specialArrangement,
			@OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
			@OptionalParam(name = ConstantKeys.SP_TYPE) TokenParam type,
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
			@OptionalParam(name = ConstantKeys.SP_PAGE) NumberParam _page, @Sort SortSpec theSort, @Count Integer count,
			@IncludeParam(allow = { "Encounter:subject", "Encounter:diagnosis", "*" }) Set<Include> includes)
			throws OperationOutcomeException {
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<IBaseResource> results = new ArrayList<>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = encounterDao.search(fhirContext, appointment, _class, date, diagnosis, episodeofcare,
						identifier, incomingreferral, length, location, locationPeriod, partOf, participant,
						participantType, patient, practitioner, reason, serviceProvider, specialArrangement, status,
						subject, type, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page,
						sortParam, count, includes);
				// return results;
			} else
				results = encounterDao.search(fhirContext, appointment, _class, date, diagnosis, episodeofcare,
						identifier, incomingreferral, length, location, locationPeriod, partOf, participant,
						participantType, patient, practitioner, reason, serviceProvider, specialArrangement, status,
						subject, type, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, null,
						count, includes);
			// final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x
			// -> x);
			final List<IBaseResource> finalResults = results;

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(encounterDao.getTotal(fhirContext, appointment, _class, date,
							diagnosis, episodeofcare, identifier, incomingreferral, length, location, locationPeriod,
							partOf, participant, participantType, patient, practitioner, reason, serviceProvider,
							specialArrangement, status, subject, type, resid, _lastUpdated, _tag, _profile, _query,
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
			@OptionalParam(name = ConstantKeys.SP_APPOINTMENT) ReferenceParam appointment,
			@OptionalParam(name = ConstantKeys.SP_CLASS) TokenParam _class,
			@OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OptionalParam(name = ConstantKeys.SP_DIAGNOSIS) ReferenceParam diagnosis,
			@OptionalParam(name = ConstantKeys.SP_EPISODEOFCARE) ReferenceParam episodeofcare,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_INCOMINGREFERRAL) ReferenceParam incomingreferral,
			@OptionalParam(name = ConstantKeys.SP_LENGHTH) NumberParam length,
			@OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
			@OptionalParam(name = ConstantKeys.SP_LOCATION_PERIOD) DateRangeParam locationPeriod,
			@OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partOf,
			@OptionalParam(name = ConstantKeys.SP_PARTICIPANT) ReferenceParam participant,
			@OptionalParam(name = ConstantKeys.SP_PARTICIPANT_TYPE) TokenParam participantType,
			@OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
			@OptionalParam(name = ConstantKeys.SP_REASON) TokenParam reason,
			@OptionalParam(name = ConstantKeys.SP_SERVICE_PROVIDER) ReferenceParam serviceProvider,
			@OptionalParam(name = ConstantKeys.SP_SPECIAL_ARRANGEMENT) TokenParam specialArrangement,
			@OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
			@OptionalParam(name = ConstantKeys.SP_TYPE) TokenParam type,
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = encounterDao.getTotal(fhirContext, appointment, _class, date, diagnosis, episodeofcare, identifier,
				incomingreferral, length, location, locationPeriod, partOf, participant, participantType, patient,
				practitioner, reason, serviceProvider, specialArrangement, status, subject, type, resid, _lastUpdated,
				_tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	@Override
	protected BaseDao<EncounterEntity, Encounter> getDao() {
		return encounterDao;
	}
}
