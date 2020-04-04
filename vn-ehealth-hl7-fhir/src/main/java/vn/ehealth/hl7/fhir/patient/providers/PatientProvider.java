package vn.ehealth.hl7.fhir.patient.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
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
import vn.ehealth.hl7.fhir.providers.BaseController;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.patient.dao.impl.PatientDao;
import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;

@Component
public class PatientProvider extends BaseController<PatientEntity, Patient> implements IResourceProvider {
	static Logger logger = LoggerFactory.getLogger(PatientProvider.class);

	@Autowired
	PatientDao patientDao;

	private static final Logger log = LoggerFactory.getLogger(PatientProvider.class);

	@Override
	protected BaseDao<PatientEntity, Patient> getDao() {
		return patientDao;
	}

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Patient.class;
	}

	@Search
	public IBundleProvider searchPatient(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS_USE) TokenParam addressUse,
			@OptionalParam(name = ConstantKeys.SP_ANIMAL_BREED) TokenParam animalBreed,
			@OptionalParam(name = ConstantKeys.SP_ANIMAL_SPECIES) TokenParam animalSpecies,
			@OptionalParam(name = ConstantKeys.SP_DECEASED) TokenParam deceased,
			@OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
			@OptionalParam(name = ConstantKeys.SP_GENDER) TokenParam gender,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_LANGUAGE) TokenParam language,
			@OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
			@OptionalParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,

			@OptionalParam(name = ConstantKeys.SP_GENERAL_PRACTITIONER) ReferenceParam generalPractitioner,
			@OptionalParam(name = ConstantKeys.SP_LINK) ReferenceParam link,
			@OptionalParam(name = ConstantKeys.SP_ORGANIZATION) ReferenceParam organization,

			@OptionalParam(name = ConstantKeys.SP_BIRTHDATE) DateRangeParam birthDate,
			@OptionalParam(name = ConstantKeys.SP_DEATH_DATE) DateRangeParam deathDate,

			@OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
			@OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
			@OptionalParam(name = ConstantKeys.SP_FAMILY) StringParam familyName,
			@OptionalParam(name = ConstantKeys.SP_GIVEN) StringParam givenName,
			@OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,

			// Parameters for all resources
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
			// Search result parameters
			@OptionalParam(name = ConstantKeys.SP_PAGE) NumberParam _page, @Sort SortSpec theSort, @Count Integer count)
			throws OperationOutcomeException {
		log.debug("Search Patient Provider called");
		// String permissionAccept = PatientOauth2Keys.PatientOauth2.PATIENT_LIST;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<IBaseResource> results = new ArrayList<IBaseResource>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = patientDao.search(fhirContext, addressUse, animalBreed, animalSpecies, deceased, email,
						gender, identifier, language, phone, telecom, generalPractitioner, link, organization,
						birthDate, deathDate, address, addressCity, addressCountry, addressState, familyName, givenName,
						name, phonetic, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page,
						sortParam, count);
				// return results;
			} else
				results = patientDao.search(fhirContext, addressUse, animalBreed, animalSpecies, deceased, email,
						gender, identifier, language, phone, telecom, generalPractitioner, link, organization,
						birthDate, deathDate, address, addressCity, addressCountry, addressState, familyName, givenName,
						name, phonetic, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, "",
						count);
			// final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x
			// -> x);
			final List<IBaseResource> finalResults = results;

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(patientDao.findMatchesAdvancedTotal(fhirContext, addressUse,
							animalBreed, animalSpecies, deceased, email, gender, identifier, language, phone, telecom,
							generalPractitioner, link, organization, birthDate, deathDate, address, addressCity,
							addressCountry, addressState, familyName, givenName, name, phonetic, resid, _lastUpdated,
							_tag, _profile, _query, _security, _content)));
				}

				@Override
				public Integer preferredPageSize() {
					return finalResults.size();
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
	public Parameters findMatchesAdvancedTotal(HttpServletRequest request,
			@OptionalParam(name = "address-use") TokenParam addressUse,
			@OptionalParam(name = "animal-breed") TokenParam animalBreed,
			@OptionalParam(name = "animal-species") TokenParam animalSpecies,
			@OptionalParam(name = "deceased") TokenParam deceased, @OptionalParam(name = "email") TokenParam email,
			@OptionalParam(name = "gender") TokenParam gender,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = "language") TokenParam language,
			@OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
			@OptionalParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,

			@OptionalParam(name = "general-practitioner") ReferenceParam generalPractitioner,
			@OptionalParam(name = "link") ReferenceParam link,
			@OptionalParam(name = "organization") ReferenceParam organization,

			@OptionalParam(name = ConstantKeys.SP_BIRTHDATE) DateRangeParam birthDate,
			@OptionalParam(name = "death-date") DateRangeParam deathDate,

			@OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
			@OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
			@OptionalParam(name = ConstantKeys.SP_FAMILY) StringParam familyName,
			@OptionalParam(name = ConstantKeys.SP_GIVEN) StringParam givenName,
			@OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,

			// Parameters for all resources
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = "_lastUpdated") DateRangeParam _lastUpdated,
			@OptionalParam(name = "_tag") TokenParam _tag, @OptionalParam(name = "_profile") UriParam _profile,
			@OptionalParam(name = "_query") TokenParam _query, @OptionalParam(name = "_security") TokenParam _security,
			@OptionalParam(name = "_content") StringParam _content
	// Search result parameters
	) {
		Parameters retVal = new Parameters();
		long total = patientDao.findMatchesAdvancedTotal(fhirContext, addressUse, animalBreed, animalSpecies, deceased,
				email, gender, identifier, language, phone, telecom, generalPractitioner, link, organization, birthDate,
				deathDate, address, addressCity, addressCountry, addressState, familyName, givenName, name, phonetic,
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	@Operation(name = "$everything", idempotent = true)
	public IBundleProvider getEverything(@IdParam IdType thePatientId,
			@OperationParam(name = "start") DateParam theStart, @OperationParam(name = "end") DateParam theEnd) {
		Patient patient = null;

		if (thePatientId.hasVersionIdPart()) {
			patient = patientDao.readOrVread(thePatientId);
		} else {
			patient = patientDao.read(thePatientId);
		}
		if (patient == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No Entity/" + thePatientId.getIdPart()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}

		List<IBaseResource> results = new ArrayList<IBaseResource>();
		// Populate bundle with matching resources

		results = patientDao.getEverything(thePatientId, theStart, theEnd);

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
