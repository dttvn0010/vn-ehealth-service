package vn.ehealth.hl7.fhir.clinical.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
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
import vn.ehealth.hl7.fhir.clinical.dao.IServiceRequest;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

@Component
public class ServiceRequestProvider implements IResourceProvider {

	@Autowired
	FhirContext fhirContext;

	@Autowired
	IServiceRequest baseDao;

	private static final Logger log = LoggerFactory.getLogger(ServiceRequest.class);

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return ServiceRequest.class;
	}

	@Create
	public MethodOutcome create(HttpServletRequest theRequest, @ResourceParam ServiceRequest obj) {

		log.debug("Create ServiceRequest Provider called");

		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		ServiceRequest mongo = null;
		try {
			mongo = baseDao.create(fhirContext, obj);
			List<String> myString = new ArrayList<>();
			myString.add("ServiceRequest/" + mongo.getIdElement());
			method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
					"urn:uuid: " + mongo.getId(), IssueSeverity.INFORMATION, IssueType.VALUE, myString));
			method.setId(mongo.getIdElement());
			method.setResource(mongo);
		} catch (Exception ex) {
			if (ex instanceof OperationOutcomeException) {
				OperationOutcomeException outcomeException = (OperationOutcomeException) ex;
				method.setOperationOutcome(outcomeException.getOutcome());
				// method.setCreated(false);
			} else {
				log.error(ex.getMessage());
				// method.setCreated(false);
				method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome(ex.getMessage()));
			}
		}
		return method;
	}

	@Read
	public ServiceRequest read(HttpServletRequest request, @IdParam IdType internalId) {

		ServiceRequest object = baseDao.read(fhirContext, internalId);
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No ServiceRequest/" + internalId.getIdPart()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		return object;
	}

	@Search
	public IBundleProvider search(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam bassedOn,
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
			@OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content,
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
				results = baseDao.search(fhirContext, active, bassedOn, category, code, context, date, definition,
						encounter, identifier, location, partOf, patient, performer, status, subject, resid,
						_lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam, count);
				// return results;
			} else
				results = baseDao.search(fhirContext, active, bassedOn, category, code, context, date, definition,
						encounter, identifier, location, partOf, patient, performer, status, subject, resid,
						_lastUpdated, _tag, _profile, _query, _security, _content, _page, null, count);
			final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(baseDao.countMatchesAdvancedTotal(fhirContext, active,
							bassedOn, category, code, context, date, definition, encounter, identifier, location,
							partOf, patient, performer, status, subject, resid, _lastUpdated, _tag, _profile, _query,
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

	@Delete
	public ServiceRequest delete(HttpServletRequest request, @IdParam IdType internalId) {
		ServiceRequest obj = baseDao.remove(fhirContext, internalId);
		if (obj == null) {
			log.error("Couldn't delete Procedure" + internalId);
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Object is not exit"), OperationOutcome.IssueSeverity.ERROR,
					OperationOutcome.IssueType.NOTFOUND);
		}
		return obj;
	}

	@Update
	public MethodOutcome update(@IdParam IdType theId, @ResourceParam ServiceRequest input) {

		log.debug("Update Procedure Provider called");

		MethodOutcome method = new MethodOutcome();
		method.setCreated(false);
		OperationOutcome opOutcome = new OperationOutcome();
		method.setOperationOutcome(opOutcome);
		ServiceRequest newObj = null;
		try {
			newObj = baseDao.update(fhirContext, input, theId);
		} catch (Exception ex) {
			if (ex instanceof OperationOutcomeException) {
				OperationOutcomeException outcomeException = (OperationOutcomeException) ex;
				method.setOperationOutcome(outcomeException.getOutcome());
				// method.setCreated(false);
			} else {
				log.error(ex.getMessage());
				// method.setCreated(false);
				method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome(ex.getMessage()));
			}
		}
		method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Update succsess",
				"urn:uuid: " + newObj.getId(), IssueSeverity.INFORMATION, IssueType.VALUE));
		method.setId(newObj.getIdElement());
		method.setResource(newObj);
		return method;
	}

	@Operation(name = "$total", idempotent = true)
	public Parameters getTotal(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam bassedOn,
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
			@OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = baseDao.countMatchesAdvancedTotal(fhirContext, active, bassedOn, category, code, context, date,
				definition, encounter, identifier, location, partOf, patient, performer, status, subject, resid,
				_lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	/**
	 * @author sonvt
	 * @param request
	 * @param idType
	 * @return read object version
	 */
	@Read(version = true)
	public ServiceRequest readVread(HttpServletRequest request, @IdParam IdType idType) {
		ServiceRequest object = new ServiceRequest();
		if (idType.hasVersionIdPart()) {
			object = baseDao.readOrVread(fhirContext, idType);
		} else {
			object = baseDao.read(fhirContext, idType);
		}
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No ServiceRequest/" + idType.getIdPart()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		return object;
	}
}
