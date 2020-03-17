package vn.ehealth.hl7.fhir.provider.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Practitioner;
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
import vn.ehealth.hl7.fhir.provider.dao.IPractitioner;

@Component
public class PractitionerProvider implements IResourceProvider {
	@Autowired
	FhirContext fhirContext;

	@Autowired
	IPractitioner practitionerDao;

	private static final Logger log = LoggerFactory.getLogger(PractitionerProvider.class);

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Practitioner.class;
	}

	@Create
	public MethodOutcome create(HttpServletRequest theRequest, @ResourceParam Practitioner obj) {
		log.debug("Create Practitioner Provider called");
		// String permissionAccept =
		// TerminologyOauth2Keys.CodeSystemOauth2.CODESYSTEM_ADD;
		// OAuth2Util.checkOauth2(theRequest, permissionAccept);
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();

		method.setOperationOutcome(opOutcome);
		Practitioner mongoObj = null;
		try {
			mongoObj = practitionerDao.create(fhirContext, obj);
			List<String> myString = new ArrayList<>();
			myString.add("Practitioner/" + mongoObj.getIdElement());
			method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
					"urn:uuid: " + mongoObj.getId(), IssueSeverity.INFORMATION, IssueType.VALUE, myString));
			method.setId(mongoObj.getIdElement());
			method.setResource(mongoObj);
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
	public Practitioner readPractitioner(HttpServletRequest request, @IdParam IdType internalId) {
		log.debug("Read Practitioner Provider called");
		// String permissionAccept =
		// TerminologyOauth2Keys.CodeSystemOauth2.CODESYSTEM_VIEW;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		Practitioner object = practitionerDao.read(fhirContext, internalId);
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No Practitioner/" + internalId.getIdPart()),
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
	public Practitioner readVread(HttpServletRequest request, @IdParam IdType idType) {
		Practitioner object = new Practitioner();
		if (idType.hasVersionIdPart()) {
			object = practitionerDao.readOrVread(fhirContext, idType);
		} else {
			object = practitionerDao.read(fhirContext, idType);
		}
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No Practitioner/" + idType.getIdPart()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		return object;
	}

	@Search
	public IBundleProvider searchPractitioner(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
			@OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS_POSTALCODE) StringParam addressPostalCode,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS_USE) TokenParam addressUse,
			@OptionalParam(name = ConstantKeys.SP_COMMUNICATION) TokenParam communication,
			@OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
			@OptionalParam(name = ConstantKeys.SP_FAMILY) StringParam family,
			@OptionalParam(name = ConstantKeys.SP_GENDER) TokenParam gender,
			@OptionalParam(name = ConstantKeys.SP_GIVEN) StringParam given,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
			@OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
			@OptionalParam(name = ConstantKeys.SP_TOKEN) TokenParam telecom,
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content,
			@OptionalParam(name = "managingOrg") ReferenceParam managingOrg,
			@OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Sort SortSpec theSort, @Count Integer count)
			throws OperationOutcomeException {

		log.debug("search Practitioner Provider called");
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<Resource> results = new ArrayList<Resource>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = practitionerDao.search(fhirContext, active, address, addressCity, addressCountry,
						addressPostalCode, addressState, addressUse, communication, email, family, gender, given,
						identifier, name, phone, phonetic, telecom, resid, _lastUpdated, _tag, _profile, _query,
						_security, _content, managingOrg, _page, sortParam, count);
			} else
				results = practitionerDao.search(fhirContext, active, address, addressCity, addressCountry,
						addressPostalCode, addressState, addressUse, communication, email, family, gender, given,
						identifier, name, phone, phonetic, telecom, resid, _lastUpdated, _tag, _profile, _query,
						_security, _content, managingOrg, _page, "", count);
			final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(practitionerDao.countMatchesAdvancedTotal(fhirContext,
							active, address, addressCity, addressCountry, addressPostalCode, addressState, addressUse,
							communication, email, family, gender, given, identifier, name, phone, phonetic, telecom,
							resid, _lastUpdated, _tag, _profile, _query, _security, _content, managingOrg)));
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
	public Practitioner delete(HttpServletRequest request, @IdParam IdType internalId) {
		log.debug("delete Practitioner Provider called");
		// String permissionAccept =
		// TerminologyOauth2Keys.CodeSystemOauth2.CODESYSTEM_DELETE;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		Practitioner organization = practitionerDao.remove(fhirContext, internalId);
		if (organization == null) {
			log.error("Couldn't remove Practitioner" + internalId);
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Practitioner is not exit"), OperationOutcome.IssueSeverity.ERROR,
					OperationOutcome.IssueType.NOTFOUND);
		}
		return organization;
	}

	@Operation(name = "$total", idempotent = true)
	public Parameters findMatchesAdvancedTotal(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
			@OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS_POSTALCODE) StringParam addressPostalCode,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS_USE) TokenParam addressUse,
			@OptionalParam(name = ConstantKeys.SP_COMMUNICATION) TokenParam communication,
			@OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
			@OptionalParam(name = ConstantKeys.SP_FAMILY) StringParam family,
			@OptionalParam(name = ConstantKeys.SP_GENDER) TokenParam gender,
			@OptionalParam(name = ConstantKeys.SP_GIVEN) StringParam given,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
			@OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
			@OptionalParam(name = ConstantKeys.SP_TOKEN) TokenParam telecom,
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content,
			@OptionalParam(name = "managingOrg") ReferenceParam managingOrg) {
		Parameters retVal = new Parameters();
		long total = practitionerDao.countMatchesAdvancedTotal(fhirContext, active, address, addressCity,
				addressCountry, addressPostalCode, addressState, addressUse, communication, email, family, gender,
				given, identifier, name, phone, phonetic, telecom, resid, _lastUpdated, _tag, _profile, _query,
				_security, _content, managingOrg);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}
}
