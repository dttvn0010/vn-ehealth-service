package vn.ehealth.hl7.fhir.r4.providers.ehr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Encounter;
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
import ca.uhn.fhir.rest.param.DateParam;
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
	EncounterDao baseDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Encounter.class;
	}

	@Override
	protected List<String> getProfile() {
		// TODO Auto-generated method stub
		return null;
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
				results = baseDao.search(fhirContext, appointment, _class, date, diagnosis, episodeofcare, identifier,
						incomingreferral, length, location, locationPeriod, partOf, participant, participantType,
						patient, practitioner, reason, serviceProvider, specialArrangement, status, subject, type,
						resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam, count,
						includes);
				// return results;
			} else
				results = baseDao.search(fhirContext, appointment, _class, date, diagnosis, episodeofcare, identifier,
						incomingreferral, length, location, locationPeriod, partOf, participant, participantType,
						patient, practitioner, reason, serviceProvider, specialArrangement, status, subject, type,
						resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, null, count, includes);
			// final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x
			// -> x);
			final List<IBaseResource> finalResults = results;

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(baseDao.getTotal(fhirContext, appointment, _class, date,
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
			@OperationParam(name = ConstantKeys.SP_APPOINTMENT) ReferenceParam appointment,
			@OperationParam(name = ConstantKeys.SP_CLASS) TokenParam _class,
			@OperationParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OperationParam(name = ConstantKeys.SP_DIAGNOSIS) ReferenceParam diagnosis,
			@OperationParam(name = ConstantKeys.SP_EPISODEOFCARE) ReferenceParam episodeofcare,
			@OperationParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OperationParam(name = ConstantKeys.SP_INCOMINGREFERRAL) ReferenceParam incomingreferral,
			@OperationParam(name = ConstantKeys.SP_LENGHTH) NumberParam length,
			@OperationParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
			@OperationParam(name = ConstantKeys.SP_LOCATION_PERIOD) DateRangeParam locationPeriod,
			@OperationParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partOf,
			@OperationParam(name = ConstantKeys.SP_PARTICIPANT) ReferenceParam participant,
			@OperationParam(name = ConstantKeys.SP_PARTICIPANT_TYPE) TokenParam participantType,
			@OperationParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OperationParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
			@OperationParam(name = ConstantKeys.SP_REASON) TokenParam reason,
			@OperationParam(name = ConstantKeys.SP_SERVICE_PROVIDER) ReferenceParam serviceProvider,
			@OperationParam(name = ConstantKeys.SP_SPECIAL_ARRANGEMENT) TokenParam specialArrangement,
			@OperationParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OperationParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
			@OperationParam(name = ConstantKeys.SP_TYPE) TokenParam type,
			@OperationParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OperationParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OperationParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OperationParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OperationParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OperationParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OperationParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = baseDao.getTotal(fhirContext, appointment, _class, date, diagnosis, episodeofcare, identifier,
				incomingreferral, length, location, locationPeriod, partOf, participant, participantType, patient,
				practitioner, reason, serviceProvider, specialArrangement, status, subject, type, resid, _lastUpdated,
				_tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	@Override
	protected BaseDao<EncounterEntity, Encounter> getDao() {
		return baseDao;
	}

	@Operation(name = "$everything", idempotent = true, bundleType = BundleTypeEnum.SEARCHSET)
	public IBundleProvider getEverything(HttpServletRequest request, @IdParam IdType theId,
			@OperationParam(name = "start") DateParam theStart, @OperationParam(name = "end") DateParam theEnd) {
		List<IBaseResource> results = new ArrayList<IBaseResource>();
		// Populate bundle with matching resources

		results = baseDao.getEverything(theId, theStart, theEnd);
		if (results == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No " + theId.getValue() + " found"),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}

		// return list
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
