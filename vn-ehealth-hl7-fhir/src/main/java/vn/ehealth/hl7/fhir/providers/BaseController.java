package vn.ehealth.hl7.fhir.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.History;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Patch;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.PatchTypeEnum;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.ProviderResponseLibrary;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;

public abstract class BaseController<ENT extends BaseResource, FHIR extends Resource> {

	private static final Logger log = LoggerFactory.getLogger(BaseController.class);

	abstract protected BaseDao<ENT, FHIR> getDao();

	@Autowired
	protected FhirContext fhirContext;

	@Create
	public MethodOutcome create(HttpServletRequest theRequest, @ResourceParam FHIR object) {
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		FHIR newObj = null;
		try {
			newObj = getDao().create(object);
			List<String> myString = new ArrayList<>();
			myString.add("urn:uuid:" + newObj.getIdElement());
			method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
					"urn:uuid:" + newObj.getId(), IssueSeverity.INFORMATION, IssueType.VALUE, myString));
			method.setId(newObj.getIdElement());
			method.setResource(newObj);
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
	public FHIR read(HttpServletRequest request, @IdParam IdType theId) {
		var object = getDao().read(theId);
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No " + theId.getValue() + " found"),
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
	public FHIR readVread(HttpServletRequest request, @IdParam IdType theId) {
		FHIR object = null;
		if (theId.hasVersionIdPart()) {
			object = getDao().readOrVread(theId);
		} else {
			object = getDao().read(theId);
		}
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No " + theId.getValue() + " found"),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		return object;
	}

	@Delete
	public void delete(HttpServletRequest request, @IdParam IdType idType) {
		log.debug("Delete Entity called");
		var object = getDao().remove(idType);
		if (object == null) {
			log.error("Couldn't delete object " + idType.getValue());
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No " + idType.getValue() + " found"),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);

		}
		return;
	}

	@Update
	public MethodOutcome update(HttpServletRequest theRequest, @IdParam IdType theId, @ResourceParam FHIR object) {
		log.debug("Update Object called");
		MethodOutcome method = new MethodOutcome();
		method.setCreated(false);

		FHIR old = null;
		if (theId.hasVersionIdPart()) {
			old = getDao().readOrVread(theId);
		} else {
			old = getDao().read(theId);
		}
		if (old == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No " + theId.getValue() + " found"),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}

		FHIR newObject = null;
		try {
			newObject = getDao().update(object, theId);
			List<String> myString = new ArrayList<>();
			myString.add("urn:uuid:" + newObject.getIdElement());
			method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Update succsess",
					"urn:uuid:" + newObject.getId(), IssueSeverity.INFORMATION, IssueType.VALUE, myString));
			method.setId(newObject.getIdElement());
			method.setResource(newObject);
		} catch (Exception ex) {
			ProviderResponseLibrary.handleException(method, ex);
		}
		return method;
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

	@History
	public IBundleProvider getInstanceHistory(@IdParam IdType theId,
			@OptionalParam(name = "_since") InstantType theSince, @OptionalParam(name = "_at") DateRangeParam theAt,
			@OptionalParam(name = ConstantKeys.SP_PAGE) NumberParam _page, @Count Integer count) {

		FHIR object = null;
		if (theId.hasVersionIdPart()) {
			object = getDao().readOrVread(theId);
		} else {
			object = getDao().read(theId);
		}
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No " + theId.getValue() + " found"),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}

		List<IBaseResource> results = new ArrayList<>();
		results = getDao().getHistory(theId, theSince, theAt, _page, count);
		final List<IBaseResource> finalResults = results;

		return new IBundleProvider() {

			@Override
			public Integer size() {
				return getDao().countHistory(theId, theSince, theAt);
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

	@History
	public IBundleProvider getResourceHistory(@OptionalParam(name = "_since") InstantType theSince,
			@OptionalParam(name = "_at") DateRangeParam theAt,
			@OptionalParam(name = ConstantKeys.SP_PAGE) NumberParam _page, @Count Integer count) {
		List<IBaseResource> results = new ArrayList<>();
		results = getDao().getHistory(null, theSince, theAt, _page, count);
		final List<IBaseResource> finalResults = results;

		return new IBundleProvider() {

			@Override
			public Integer size() {
				return getDao().countHistory(null, theSince, theAt);
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
