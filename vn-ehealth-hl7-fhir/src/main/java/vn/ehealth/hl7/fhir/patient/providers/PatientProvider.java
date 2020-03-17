package vn.ehealth.hl7.fhir.patient.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.At;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.History;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Patch;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Since;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.PatchTypeEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.ProviderResponseLibrary;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.patient.dao.IPatient;
import vn.ehealth.hl7.fhir.user.entity.PermissionEntity;
import vn.ehealth.hl7.fhir.util.PermissionUtil;

@Component
public class PatientProvider implements IResourceProvider {
	static Logger logger = LoggerFactory.getLogger(PatientProvider.class);
	@Autowired
	FhirContext fhirContext;

	@Autowired
	IPatient patientDao;

	private static final Logger log = LoggerFactory.getLogger(PatientProvider.class);

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Patient.class;
	}

	@Create
	public MethodOutcome createPatient(HttpServletRequest theRequest, @ResourceParam Patient patient) {
		log.debug("Create Patient Provider called");
		Date cal = new Date();
		// OAuth2Util.checkOauth2(theRequest, PermissionEntity.Values.PATIENT_ADD);
		// PermissionUtil.checkPermission(PermissionEntity.Values.PATIENT_ADD);

		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		method.setOperationOutcome(opOutcome);
		Patient mongoPatient = null;
		try {
			mongoPatient = patientDao.create(fhirContext, patient);
			Date cal2 = new Date();
			System.out
					.println("-------------------dao end------------------" + (cal2.getTime() - cal.getTime()) + " ms");
			List<String> myString = new ArrayList<>();
			myString.add("Patient/" + mongoPatient.getIdElement());
			method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
					"urn:uuid: " + mongoPatient.getId(), IssueSeverity.INFORMATION, IssueType.VALUE, myString));
			method.setId(mongoPatient.getIdElement());
			method.setResource(mongoPatient);
		} catch (Exception ex) {
			if (ex instanceof OperationOutcomeException) {
				OperationOutcomeException outcomeException = (OperationOutcomeException) ex;
				method.setOperationOutcome(outcomeException.getOutcome());
				method.setCreated(false);
			} else {
				log.error(ex.getMessage());
				method.setCreated(false);
				method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome(ex.getMessage()));
			}
		}
		Date cal1 = new Date();
		System.out
				.println("-------------------create end------------------" + (cal1.getTime() - cal.getTime()) + " ms");
		return method;
	}

	@Read
	public Patient readPatient(HttpServletRequest request, @IdParam IdType internalId) {
		log.debug("Read Patient Provider called");
		Date cal = new Date();
		// String permissionAccept = PatientOauth2Keys.PatientOauth2.PATIENT_VIEW;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		Patient patient = patientDao.read(fhirContext, internalId);
		if (patient == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No Patient/" + internalId.getIdPart()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		Date cal1 = new Date();
		System.out.println("-------------------read end------------------" + (cal1.getTime() - cal.getTime()) + " ms");
		return patient;
	}

	/**
	 * @author sonvt
	 * @param request
	 * @param idType
	 * @return read object version
	 */
	@Read(version = true)
	public Patient readVread(HttpServletRequest request, @IdParam IdType idType) {
		Date cal = new Date();
		// PermissionUtil.checkPermission(PermissionEntity.Values.PATIENT_VIEW);

		Patient object = new Patient();
		if (idType.hasVersionIdPart()) {
			object = patientDao.readOrVread(fhirContext, idType);
		} else {
			object = patientDao.read(fhirContext, idType);
		}
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No Patient/" + idType.getIdPart()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		Date cal1 = new Date();
		System.out.println(
				"-------------------readVread end------------------" + (cal1.getTime() - cal.getTime()) + " ms");
		return object;
	}

	@Search
	public IBundleProvider searchPatient(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
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
			@OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Sort SortSpec theSort, @Count Integer count)
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
			Date cal = new Date();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = patientDao.search(fhirContext, active, addressUse, animalBreed, animalSpecies, deceased,
						email, gender, identifier, language, phone, telecom, generalPractitioner, link, organization,
						birthDate, deathDate, address, addressCity, addressCountry, addressState, familyName, givenName,
						name, phonetic, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page,
						sortParam, count);
				// return results;
			} else
				results = patientDao.search(fhirContext, active, addressUse, animalBreed, animalSpecies, deceased,
						email, gender, identifier, language, phone, telecom, generalPractitioner, link, organization,
						birthDate, deathDate, address, addressCity, addressCountry, addressState, familyName, givenName,
						name, phonetic, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, "",
						count);
			Date cal1 = new Date();
			System.out.println(
					"-------------------search end------------------" + (cal1.getTime() - cal.getTime()) + " ms");
			final List<IBaseResource> finalResults = results;

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(patientDao.findMatchesAdvancedTotal(fhirContext, active,
							addressUse, animalBreed, animalSpecies, deceased, email, gender, identifier, language,
							phone, telecom, generalPractitioner, link, organization, birthDate, deathDate, address,
							addressCity, addressCountry, addressState, familyName, givenName, name, phonetic, resid,
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

	@Delete
	public Patient delete(HttpServletRequest request, @IdParam IdType internalId) {
		Date cal = new Date();
		log.debug("Delete Patient Provider called");
		// String permissionAccept = PatientOauth2Keys.PatientOauth2.PATIENT_DELETE;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		Patient patient = patientDao.remove(fhirContext, internalId);
		if (patient == null) {
			log.error("Couldn't delete patient" + internalId);
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("patient is not exit"), OperationOutcome.IssueSeverity.ERROR,
					OperationOutcome.IssueType.NOTFOUND);

		}
		Date cal1 = new Date();
		System.out
				.println("-------------------delete end------------------" + (cal1.getTime() - cal.getTime()) + " ms");
		return patient;
	}

	@Update
	public MethodOutcome update(HttpServletRequest theRequest, @IdParam IdType theId, @ResourceParam Patient patient) {
		Date cal = new Date();
		log.debug("Update Patient Provider called");
		// String permissionAccept = PatientOauth2Keys.PatientOauth2.PATIENT_ADD;
		// OAuth2Util.checkOauth2(theRequest, permissionAccept);
		MethodOutcome method = new MethodOutcome();
		method.setCreated(false);
		OperationOutcome opOutcome = new OperationOutcome();
		method.setOperationOutcome(opOutcome);
		Patient newPatient = null;
		try {
			newPatient = patientDao.update(fhirContext, patient, theId);
		} catch (Exception ex) {
			ProviderResponseLibrary.handleException(method, ex);
		}
		method.setId(newPatient.getIdElement());
		method.setResource(newPatient);
		Date cal1 = new Date();
		System.out
				.println("-------------------update end------------------" + (cal1.getTime() - cal.getTime()) + " ms");
		return method;
	}

	@Operation(name = "$total", idempotent = true)
	public Parameters findMatchesAdvancedTotal(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
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
		Date cal = new Date();
		Parameters retVal = new Parameters();
		long total = patientDao.findMatchesAdvancedTotal(fhirContext, active, addressUse, animalBreed, animalSpecies,
				deceased, email, gender, identifier, language, phone, telecom, generalPractitioner, link, organization,
				birthDate, deathDate, address, addressCity, addressCountry, addressState, familyName, givenName, name,
				phonetic, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		Date cal1 = new Date();
		System.out.println("-------------------total end------------------" + (cal1.getTime() - cal.getTime()) + " ms");
		return retVal;
	}

	@History
	public List<Patient> getInstanceHistory(@IdParam IdType theId, @OptionalParam(name = "_since") InstantType theSince,
			@OptionalParam(name = "_at") DateRangeParam theAt,
			@OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Count Integer count) {
		Date cal = new Date();
		List<Patient> retVal = new ArrayList<Patient>();
		retVal = patientDao.getHistory(theId, theSince, theAt, _page, count);
		Date cal1 = new Date();
		System.out
				.println("-------------------history end------------------" + (cal1.getTime() - cal.getTime()) + " ms");
		return retVal;
	}

	@History
	public List<Patient> getResourceHistory(@OptionalParam(name = "_since") InstantType theSince,
			@OptionalParam(name = "_at") DateRangeParam theAt,
			@OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Count Integer count) {
		Date cal = new Date();
		List<Patient> retVal = new ArrayList<Patient>();
		retVal = patientDao.getHistory(null, theSince, theAt, _page, count);
		Date cal1 = new Date();
		System.out
				.println("-------------------history end------------------" + (cal1.getTime() - cal.getTime()) + " ms");
		return retVal;
	}

	@Patch
	public OperationOutcome patch(@IdParam IdType theId, PatchTypeEnum thePatchType, @ResourceParam String theBody) {
		// Dummy Operations
		if (thePatchType == PatchTypeEnum.JSON_PATCH) {
			// do something
		}
		if (thePatchType == PatchTypeEnum.XML_PATCH) {
			// do something
		}

		OperationOutcome retVal = new OperationOutcome();
		retVal.getText().setDivAsString("<div>OK</div>");
		return retVal;
	}

	@Operation(name = "$everything", idempotent = true)
	public Bundle getEverything(@IdParam IdType thePatientId, @OperationParam(name = "start") DateParam theStart,
			@OperationParam(name = "end") DateParam theEnd) {

		Bundle retVal = new Bundle();
		// Populate bundle with matching resources
		return retVal;
	}
}
