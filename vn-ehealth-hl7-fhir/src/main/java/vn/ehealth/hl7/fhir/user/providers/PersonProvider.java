package vn.ehealth.hl7.fhir.user.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Person;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.user.dao.IPerson;

@Component
public class PersonProvider implements IResourceProvider {
	@Autowired
	FhirContext fhirContext;

	@Autowired
	IPerson personDao;

	private static final Logger log = LoggerFactory.getLogger(PersonProvider.class);

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Person.class;
	}

	@Create
	public MethodOutcome createPerson(HttpServletRequest theRequest, @ResourceParam Person obj) {

		log.debug("Create Person Provider called");

		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		method.setOperationOutcome(opOutcome);
		Person mongoPerson = null;
		try {
			mongoPerson = personDao.create(fhirContext, obj);
			List<String> myString = new ArrayList<>();
			myString.add("Person/" + mongoPerson.getIdElement());
			method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
					"urn:uuid: " + mongoPerson.getId(), IssueSeverity.INFORMATION, IssueType.VALUE, myString));
			method.setId(mongoPerson.getIdElement());
			method.setResource(mongoPerson);
		} catch (Exception ex) {
			if (ex instanceof OperationOutcomeException) {
				OperationOutcomeException outcomeException = (OperationOutcomeException) ex;
				method.setOperationOutcome(outcomeException.getOutcome());
			} else {
				log.error(ex.getMessage());
				method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome(ex.getMessage()));
			}
		}
		return method;
	}

	@Read
	public Person readPerson(HttpServletRequest request, @IdParam IdType internalId) {

		Person object = personDao.read(fhirContext, internalId);
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No Person/" + internalId.getIdPart()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		return object;
	}

	/**
	 * @author sonvt
	 * @param request
	 * @param idType
	 * @return read object version
	 */
	@Read(version = true)
	public Person readVread(HttpServletRequest request, @IdParam IdType idType) {
		Person object = new Person();
		if (idType.hasVersionIdPart()) {
			object = personDao.readOrVread(fhirContext, idType);
		} else {
			object = personDao.read(fhirContext, idType);
		}
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No Person/" + idType.getIdPart()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		return object;
	}

	@Search
	public IBundleProvider searchPerson(HttpServletRequest request, @OptionalParam(name = "active") TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
			@OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
			@OptionalParam(name = ConstantKeys.SP_BIRTHDATE) DateRangeParam birthDate,
			@OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
			@OptionalParam(name = ConstantKeys.SP_GENDER) StringParam gender,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
			@OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
			@OptionalParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,
			// Parameters for all resources
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = "_lastUpdated") DateRangeParam _lastUpdated,
			@OptionalParam(name = "_tag") TokenParam _tag, @OptionalParam(name = "_profile") UriParam _profile,
			@OptionalParam(name = "_query") TokenParam _query, @OptionalParam(name = "_security") TokenParam _security,
			@OptionalParam(name = "_content") StringParam _content,
			@OptionalParam(name = "organization") ReferenceParam managingOrg,
			// Search result parameters
			@OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Sort SortSpec theSort, @Count Integer count)
			throws OperationOutcomeException {
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<Resource> results = new ArrayList<Resource>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = personDao.search(fhirContext, active, address, addressCity, addressCountry, addressState,
						birthDate, email, gender, identifier, name, patient, phone, phonetic, telecom, resid,
						_lastUpdated, _tag, _profile, _query, _security, _content, managingOrg, _page, sortParam,
						count);
			} else
				results = personDao.search(fhirContext, active, address, addressCity, addressCountry, addressState,
						birthDate, email, gender, identifier, name, patient, phone, phonetic, telecom, resid,
						_lastUpdated, _tag, _profile, _query, _security, _content, managingOrg, _page, "", count);
			final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(personDao.findMatchesAdvancedTotal(fhirContext, active,
							address, addressCity, addressCountry, addressState, birthDate, email, gender, identifier,
							name, patient, phone, phonetic, telecom, resid, _lastUpdated, _tag, _profile, _query,
							_security, _content, managingOrg)));
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

	@Delete
	public Person delete(HttpServletRequest request, @IdParam IdType internalId) {
		Person obj = personDao.remove(fhirContext, internalId);
		if (obj == null) {
			log.error("Couldn't delete Person" + internalId);
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Person is not exit"), OperationOutcome.IssueSeverity.ERROR,
					OperationOutcome.IssueType.NOTFOUND);
		}
		return obj;
	}

	@Update
	public MethodOutcome update(@IdParam IdType theId, @ResourceParam Person obj) {

		log.debug("Update Person Provider called");

		MethodOutcome method = new MethodOutcome();
		method.setCreated(false);
		OperationOutcome opOutcome = new OperationOutcome();
		method.setOperationOutcome(opOutcome);
		Person newPerson = null;
		try {
			newPerson = personDao.update(fhirContext, obj, theId);
		} catch (Exception ex) {
			if (ex instanceof OperationOutcomeException) {
				OperationOutcomeException outcomeException = (OperationOutcomeException) ex;
				method.setOperationOutcome(outcomeException.getOutcome());
			} else {
				log.error(ex.getMessage());
				method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome(ex.getMessage()));
			}
		}
		method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Update succsess",
				"urn:uuid: " + newPerson.getId(), IssueSeverity.INFORMATION, IssueType.VALUE));
		method.setId(newPerson.getIdElement());
		method.setResource(newPerson);
		return method;
	}

	@Operation(name = "$total", idempotent = true)
	public Parameters findMatchesAdvancedTotal(HttpServletRequest request,
			@OptionalParam(name = "active") TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
			@OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
			@OptionalParam(name = ConstantKeys.SP_BIRTHDATE) DateRangeParam birthDate,
			@OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
			@OptionalParam(name = ConstantKeys.SP_GENDER) StringParam gender,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
			@OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
			@OptionalParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,
			// Parameters for all resources
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = "_lastUpdated") DateRangeParam _lastUpdated,
			@OptionalParam(name = "_tag") TokenParam _tag, @OptionalParam(name = "_profile") UriParam _profile,
			@OptionalParam(name = "_query") TokenParam _query, @OptionalParam(name = "_security") TokenParam _security,
			@OptionalParam(name = "_content") StringParam _content,
			@OptionalParam(name = "organization") ReferenceParam managingOrg) {
		Parameters retVal = new Parameters();
		long total = personDao.findMatchesAdvancedTotal(fhirContext, active, address, addressCity, addressCountry,
				addressState, birthDate, email, gender, identifier, name, patient, phone, phonetic, telecom, resid,
				_lastUpdated, _tag, _profile, _query, _security, _content, managingOrg);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}
}
