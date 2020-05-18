package vn.ehealth.hl7.fhir.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.hapi.ctx.DefaultProfileValidationSupport;
import org.hl7.fhir.r4.hapi.validation.CachingValidationSupport;
import org.hl7.fhir.r4.hapi.validation.FhirInstanceValidator;
import org.hl7.fhir.r4.hapi.validation.ValidationSupportChain;
import org.hl7.fhir.r4.model.CanonicalType;
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
import ca.uhn.fhir.rest.annotation.Validate;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.PatchTypeEnum;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.ResourceVersionConflictException;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationOptions;
import ca.uhn.fhir.validation.ValidationResult;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.factory.ProviderResponseLibrary;

public abstract class BaseController<ENT extends BaseResource, FHIR extends Resource> {

	private static final Logger log = LoggerFactory.getLogger(BaseController.class);

	abstract protected BaseDao<ENT, FHIR> getDao();

	abstract protected List<String> getProfile();

	@Autowired
	protected FhirContext fhirContext;

	@Create
	public MethodOutcome create(HttpServletRequest theRequest, @ResourceParam FHIR object) {
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		FHIR newObj = null;
		try {
			if(getProfile() != null) {
				if (object.hasMeta() && object.getMeta().hasProfile()) {
					for (String item : getProfile()) {
						if (!object.getMeta().hasProfile(item))
							object.getMeta().getProfile().add(new CanonicalType(item));
					}
				} else {
					for (String item : getProfile()) {
						object.getMeta().getProfile().add(new CanonicalType(item));
					}
				}
			}

			newObj = getDao().create(object);
			List<String> myString = new ArrayList<>();
			myString.add("urn:uuid:" + newObj.getIdElement());
			method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
					"urn:uuid:" + newObj.getId(), IssueSeverity.INFORMATION, IssueType.VALUE, myString));
			method.setId(newObj.getIdElement());
			method.setResource(newObj);
		} catch (Exception ex) {
			ProviderResponseLibrary.handleException(method, ex);
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
		String versionId = null;
		if (theId.hasVersionIdPart()) {
			versionId = theId.getVersionIdPart();
		} else if (object.hasMeta() && object.getMeta().hasVersionId()) {
			versionId = object.getMeta().getVersionId();
		}
		if (versionId == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceVersionConflictException("Resource have no version, stop processing!!!"),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.INVALID);
		}
		if (theId.hasVersionIdPart()) {
			old = getDao().readOrVread(theId);
		} else {
			old = getDao().read(theId);
		}
		if (old == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No " + theId.getValue() + " found"),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		} else if (!versionId.equals(old.getMeta().getVersionId())) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Found version " + versionId + ".Expected resource version "
							+ old.getMeta().getVersionId()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.CONFLICT);
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

	@SuppressWarnings("deprecation")
	@Validate
	public MethodOutcome validate(@ResourceParam IBaseResource object,
			@OptionalParam(name = "mode") StringParam theMode,
			@OptionalParam(name = "profile") StringParam theProfile) {
		if (theProfile != null) {
			log.debug("Validating profile: " + theProfile.getValueNotNull());
		}
		// This method returns a MethodOutcome object
		MethodOutcome method = new MethodOutcome();

		// Create a validation support chain
//		ValidationSupportChain validationSupportChain = new ValidationSupportChain(
//		   new DefaultProfileValidationSupport(ctx),
//		   new InMemoryTerminologyServerValidationSupport(ctx),
//		   new CommonCodeSystemsTerminologyService(ctx)
//		);

		// Create a chain that will hold our modules
		ValidationSupportChain supportChain = new ValidationSupportChain();

		DefaultProfileValidationSupport defaultSupport = new DefaultProfileValidationSupport();
		supportChain.addValidationSupport(defaultSupport);

//		// Create a PrePopulatedValidationSupport which can be used to load custom definitions.
//		// In this example we're loading two things, but in a real scenario we might
//		// load many StructureDefinitions, ValueSets, CodeSystems, etc.
//		PrePopulatedValidationSupport prePopulatedSupport = new PrePopulatedValidationSupport();
//		prePopulatedSupport.addStructureDefinition(someStructureDefnition);
//		prePopulatedSupport.addValueSet(someValueSet);
//		supportChain.addValidationSupport(prePopulatedSupport);

//		// Create a module that uses a remote terminology service
//		RemoteTerminologyServiceValidationSupport remoteTermSvc = new RemoteTerminologyServiceValidationSupport(ctx);
//		remoteTermSvc.setBaseUrl("http://hapi.fhir.org/baseR4");
//		supportChain.addValidationSupport(remoteTermSvc);

		// Wrap the chain in a cache to improve performance
		CachingValidationSupport cache = new CachingValidationSupport(supportChain);

		// Ask the context for a validator
		FhirValidator validator = fhirContext.newValidator();

		// Create a validation module and register it
		FhirInstanceValidator module = new FhirInstanceValidator(cache);

		validator.registerValidatorModule(module);

		ValidationOptions options = new ValidationOptions();
		options.addProfileIfNotBlank(theProfile != null ? theProfile.getValueNotNull() : "");

		if (object.getMeta() != null && object.getMeta().getProfile() != null
				&& object.getMeta().getProfile().size() > 0) {
			for (IPrimitiveType<String> item : object.getMeta().getProfile()) {
				options.addProfile(item.toString());
			}
		}

		// Perform the validation
		ValidationResult result = validator.validateWithResult(object, options);

		method.setOperationOutcome(result.getOperationOutcome());

		return method;
	}
}
