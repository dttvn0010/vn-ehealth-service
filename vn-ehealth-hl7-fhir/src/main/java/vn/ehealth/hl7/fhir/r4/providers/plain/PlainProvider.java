package vn.ehealth.hl7.fhir.r4.providers.plain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.History;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Transaction;
import ca.uhn.fhir.rest.annotation.TransactionParam;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.impl.PlainDao;

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
		return null;
	}

}
