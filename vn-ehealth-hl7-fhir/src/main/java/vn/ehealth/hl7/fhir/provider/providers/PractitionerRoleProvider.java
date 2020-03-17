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
import org.hl7.fhir.r4.model.PractitionerRole;
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
import vn.ehealth.hl7.fhir.provider.dao.IPractitionerRole;

/**
 * 
 * @author sonvt
 * @since 2019
 * @version 1.0
 */
@Component
public class PractitionerRoleProvider implements IResourceProvider {
	@Autowired
	FhirContext fhirContext;

	@Autowired
	IPractitionerRole practitionerRoleDao;

	private static final Logger log = LoggerFactory.getLogger(PractitionerRoleProvider.class);

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return PractitionerRole.class;
	}

	@Create
	public MethodOutcome create(HttpServletRequest theRequest, @ResourceParam PractitionerRole obj) {
		log.debug("Create PractitionerRole Provider called");
		// String permissionAccept =
		// TerminologyOauth2Keys.CodeSystemOauth2.CODESYSTEM_ADD;
		// OAuth2Util.checkOauth2(theRequest, permissionAccept);
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();

		method.setOperationOutcome(opOutcome);
		PractitionerRole mongoObj = null;
		try {
			mongoObj = practitionerRoleDao.create(fhirContext, obj);
			List<String> myString = new ArrayList<>();
			myString.add("PractitionerRole/" + mongoObj.getIdElement());
			method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
					"urn:uuid: " + mongoObj.getId(), IssueSeverity.INFORMATION, IssueType.VALUE, myString));
			method.setId(mongoObj.getIdElement());
			method.setResource(mongoObj);
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
	public PractitionerRole readPractitioner(HttpServletRequest request, @IdParam IdType internalId) {
		log.debug("Read Practitioner Provider called");
		// String permissionAccept =
		// TerminologyOauth2Keys.CodeSystemOauth2.CODESYSTEM_VIEW;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		PractitionerRole object = practitionerRoleDao.read(fhirContext, internalId);
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No PractitionerRole/" + internalId.getIdPart()),
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
	public PractitionerRole readVread(HttpServletRequest request, @IdParam IdType idType) {
		PractitionerRole object = new PractitionerRole();
		if (idType.hasVersionIdPart()) {
			object = practitionerRoleDao.readOrVread(fhirContext, idType);
		} else {
			object = practitionerRoleDao.read(fhirContext, idType);
		}
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No PractitionerRole/" + idType.getIdPart()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		return object;
	}

	@Search
	public IBundleProvider searchPractitioner(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
			@OptionalParam(name = ConstantKeys.SP_ENDPOINT) ReferenceParam endpoint,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
			@OptionalParam(name = ConstantKeys.SP_ORGANIZATION) ReferenceParam organization,
			@OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
			@OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
			@OptionalParam(name = ConstantKeys.SP_ROLE) TokenParam role,
			@OptionalParam(name = ConstantKeys.SP_SERVICE) ReferenceParam service,
			@OptionalParam(name = ConstantKeys.SP_SPECIALTY) TokenParam specialty,
			@OptionalParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content,
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
				results = practitionerRoleDao.search(fhirContext, active, date, email, endpoint, identifier, location,
						organization, phone, practitioner, role, service, specialty, telecom, resid, _lastUpdated, _tag,
						_profile, _query, _security, _content, _page, sortParam, count);
			} else
				results = practitionerRoleDao.search(fhirContext, active, date, email, endpoint, identifier, location,
						organization, phone, practitioner, role, service, specialty, telecom, resid, _lastUpdated, _tag,
						_profile, _query, _security, _content, _page, "", count);
			final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(
							practitionerRoleDao.countMatchesAdvancedTotal(fhirContext, active, date, email, endpoint,
									identifier, location, organization, phone, practitioner, role, service, specialty,
									telecom, resid, _lastUpdated, _tag, _profile, _query, _security, _content)));
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
	public PractitionerRole delete(HttpServletRequest request, @IdParam IdType internalId) {
		log.debug("delete PractitionerRole Provider called");
		// String permissionAccept =
		// TerminologyOauth2Keys.CodeSystemOauth2.CODESYSTEM_DELETE;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		PractitionerRole organization = practitionerRoleDao.remove(fhirContext, internalId);
		if (organization == null) {
			log.error("Couldn't remove PractitionerRole" + internalId);
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("PractitionerRole is not exit"), OperationOutcome.IssueSeverity.ERROR,
					OperationOutcome.IssueType.NOTFOUND);
		}
		return organization;
	}

	@Operation(name = "$total", idempotent = true)
	public Parameters findMatchesAdvancedTotal(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
			@OptionalParam(name = ConstantKeys.SP_ENDPOINT) ReferenceParam endpoint,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
			@OptionalParam(name = ConstantKeys.SP_ORGANIZATION) ReferenceParam organization,
			@OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
			@OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
			@OptionalParam(name = ConstantKeys.SP_ROLE) TokenParam role,
			@OptionalParam(name = ConstantKeys.SP_SERVICE) ReferenceParam service,
			@OptionalParam(name = ConstantKeys.SP_SPECIALTY) TokenParam specialty,
			@OptionalParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = practitionerRoleDao.countMatchesAdvancedTotal(fhirContext, active, date, email, endpoint,
				identifier, location, organization, phone, practitioner, role, service, specialty, telecom, resid,
				_lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}
}
