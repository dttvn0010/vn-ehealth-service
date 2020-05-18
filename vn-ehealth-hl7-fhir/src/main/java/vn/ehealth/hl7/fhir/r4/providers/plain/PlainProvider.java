package vn.ehealth.hl7.fhir.r4.providers.plain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.hapi.ctx.DefaultProfileValidationSupport;
import org.hl7.fhir.r4.hapi.validation.CachingValidationSupport;
import org.hl7.fhir.r4.hapi.validation.FhirInstanceValidator;
import org.hl7.fhir.r4.hapi.validation.ValidationSupportChain;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.InstantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.History;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Transaction;
import ca.uhn.fhir.rest.annotation.TransactionParam;
import ca.uhn.fhir.rest.annotation.Validate;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationOptions;
import ca.uhn.fhir.validation.ValidationResult;
import vn.ehealth.hl7.fhir.dao.impl.PlainDao;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeException;

@Component
public class PlainProvider {
	static Logger logger = LoggerFactory.getLogger(PlainProvider.class);

	private static final Logger log = LoggerFactory.getLogger(PlainProvider.class);

	@Autowired
	FhirContext fhirContext;

	@Autowired
	PlainDao baseDao;

	@History
	public IBundleProvider getServerHistory(@OptionalParam(name = "_since") InstantType theSince,
			@OptionalParam(name = "_at") DateRangeParam theAt) throws OperationOutcomeException {
		log.debug("Search Plain Provider called");
		List<IBaseResource> results = new ArrayList<IBaseResource>();
		results = baseDao.history(theSince, theAt);
		// final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x
		// -> x);
		final List<IBaseResource> finalResults = results;

		return new IBundleProvider() {

			@Override
			public Integer size() {
				return finalResults.size();
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

	@SuppressWarnings("unused")
	@Transaction
	public IBundleProvider transaction(@TransactionParam Bundle theInput) {
		for (BundleEntryComponent nextEntry : theInput.getEntry()) {
			// Process entry
		}

		Bundle retVal = new Bundle();
		// Populate return bundle
		return new IBundleProvider() {

			@Override
			public Integer size() {
				// TODO Auto-generated method stub
				return null;
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
				return null;
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
