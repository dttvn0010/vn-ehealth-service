package vn.ehealth.hl7.fhir.patient.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.RelatedPerson;
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
import vn.ehealth.hl7.fhir.patient.dao.IRelatedPerson;

@Component
public class RelatedPersonProvider implements IResourceProvider {
	@Autowired
	FhirContext fhirContext;

	@Autowired
	IRelatedPerson relatedPersonDao;

	private static final Logger log = LoggerFactory.getLogger(RelatedPersonProvider.class);

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return RelatedPerson.class;
	}

	@Create
	public MethodOutcome createRelatedPerson(HttpServletRequest theRequest, @ResourceParam RelatedPerson obj) {

		log.debug("Create RelatedPerson Provider called");
		// String permissionAccept = PatientOauth2Keys.RelatedPersonOauth2.RELATED_ADD;
		// OAuth2Util.checkOauth2(theRequest, permissionAccept);
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		method.setOperationOutcome(opOutcome);
		RelatedPerson mongoRelatedPerson = null;
		try {
			mongoRelatedPerson = relatedPersonDao.create(fhirContext, obj);
			List<String> myString = new ArrayList<>();
			myString.add("RelatedPerson/" + mongoRelatedPerson.getIdElement());
			method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
					"urn:uuid: " + mongoRelatedPerson.getId(), IssueSeverity.INFORMATION, IssueType.VALUE, myString));
			method.setId(mongoRelatedPerson.getIdElement());
			method.setResource(mongoRelatedPerson);
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
		return method;
	}

	@Read
	public RelatedPerson readRelatedPerson(HttpServletRequest request, @IdParam IdType internalId) {
		log.debug("Read RelatedPerson Provider called");
		// String permissionAccept = PatientOauth2Keys.RelatedPersonOauth2.RELATED_VIEW;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		// StopWatch watch = new StopWatch();
		// watch.start();
		RelatedPerson object = relatedPersonDao.read(fhirContext, internalId);
		// watch.stop();
		// System.out.println("Time Elapsed RelatedPerson: " + watch.getTime());
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No RelatedPerson/" + internalId.getIdPart()),
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
	public RelatedPerson readVread(HttpServletRequest request, @IdParam IdType idType) {
		RelatedPerson object = new RelatedPerson();
		if (idType.hasVersionIdPart()) {
			object = relatedPersonDao.readOrVread(fhirContext, idType);
		} else {
			object = relatedPersonDao.read(fhirContext, idType);
		}
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No RelatedPerson/" + idType.getIdPart()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		return object;
	}

	@SuppressWarnings("unchecked")
	@Search
	public IBundleProvider search(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
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
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content,
			// Search result parameters
			@OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Sort SortSpec theSort, @Count Integer count)
			throws OperationOutcomeException {
		log.debug("Search RelatedPerson Provider called");
		// String permissionAccept = PatientOauth2Keys.RelatedPersonOauth2.RELATED_LIST;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<Resource> results = new ArrayList<Resource>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = relatedPersonDao.search(fhirContext, active, address, addressCity, addressCountry,
						addressState, birthDate, email, gender, identifier, name, patient, phone, phonetic, telecom,
						resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam, count);
			} else
				results = relatedPersonDao.search(fhirContext, active, address, addressCity, addressCountry,
						addressState, birthDate, email, gender, identifier, name, patient, phone, phonetic, telecom,
						resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, "", count);

			final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);
			// return finalResults;

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(relatedPersonDao.findMatchesAdvancedTotal(fhirContext,
							active, address, addressCity, addressCountry, addressState, birthDate, email, gender,
							identifier, name, patient, phone, phonetic, telecom, resid, _lastUpdated, _tag, _profile,
							_query, _security, _content)));
				}

				@Override
				public Integer preferredPageSize() {
					return null;
				}

				@Override
				public String getUuid() {
					return null;
				}

				@Override
				public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
					return finalResults;
				}

				@Override
				public IPrimitiveType<Date> getPublished() {
					return null;
				}
			};

		}
	}

	@Delete
	public RelatedPerson delete(HttpServletRequest request, @IdParam IdType internalId) {
		log.debug("Delete RelatedPerson Provider called");
		// String permissionAccept =
		// PatientOauth2Keys.RelatedPersonOauth2.RELATED_DELETE;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		RelatedPerson obj = relatedPersonDao.remove(fhirContext, internalId);
		if (obj == null) {
			log.error("Couldn't delete RelatedPerson" + internalId);
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("RelatedPerson is not exit"), OperationOutcome.IssueSeverity.ERROR,
					OperationOutcome.IssueType.NOTFOUND);
		}
		return obj;
	}

	@Update
	public MethodOutcome update(HttpServletRequest request, @IdParam IdType theId,
			@ResourceParam RelatedPerson patient) {

		log.debug("Update RelatedPerson Provider called");
		// String permissionAccept = PatientOauth2Keys.RelatedPersonOauth2.RELATED_ADD;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		MethodOutcome method = new MethodOutcome();
		method.setCreated(false);
		OperationOutcome opOutcome = new OperationOutcome();
		method.setOperationOutcome(opOutcome);
		RelatedPerson newRelatedPerson = null;
		try {
			newRelatedPerson = relatedPersonDao.update(fhirContext, patient, theId);
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
		method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Update succsess",
				"urn:uuid: " + newRelatedPerson.getId(), IssueSeverity.INFORMATION, IssueType.VALUE));
		method.setId(newRelatedPerson.getIdElement());
		method.setResource(newRelatedPerson);
		return method;
	}

	@Operation(name = "$total", idempotent = true)
	public Parameters getTotal(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam active,
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
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = relatedPersonDao.findMatchesAdvancedTotal(fhirContext, active, address, addressCity,
				addressCountry, addressState, birthDate, email, gender, identifier, name, patient, phone, phonetic,
				telecom, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}
}
