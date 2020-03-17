package vn.ehealth.hl7.fhir.diagnostic.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Specimen;
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
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.diagnostic.dao.ISpecimen;

@Component
public class SpecimenProvider implements IResourceProvider {
	@Autowired
	FhirContext fhirContext;

	@Autowired
	ISpecimen specimenDao;

	private static final Logger log = LoggerFactory.getLogger(SpecimenProvider.class);

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Specimen.class;
	}

	@Create
	public MethodOutcome createSpecimen(HttpServletRequest theRequest, @ResourceParam Specimen obj) {

		log.debug("Create Specimen Provider called");

		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		Specimen mongoSpecimen = null;
		try {
			mongoSpecimen = specimenDao.create(fhirContext, obj);
			List<String> myString = new ArrayList<>();
			myString.add("Specimen/" + mongoSpecimen.getIdElement());
			method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
					"urn:uuid: " + mongoSpecimen.getId(), IssueSeverity.INFORMATION, IssueType.VALUE, myString));
			method.setId(mongoSpecimen.getIdElement());
			method.setResource(mongoSpecimen);
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
	public Specimen readSpecimen(HttpServletRequest request, @IdParam IdType internalId) {

		Specimen object = specimenDao.read(fhirContext, internalId);
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No Specimen/" + internalId.getIdPart()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		return object;
	}

	@Delete
	public Specimen deleteSpecimen(HttpServletRequest request, @IdParam IdType internalId) {
		Specimen object = specimenDao.remove(fhirContext, internalId);
		if (object == null) {
			log.error("Couldn't delete Specimen" + internalId);
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Specimen is not exit"), OperationOutcome.IssueSeverity.ERROR,
					OperationOutcome.IssueType.NOTFOUND);
		}
		return object;
	}

	@Update
	public MethodOutcome updateSpecimen(@IdParam IdType theId, @ResourceParam Specimen patient) {

		log.debug("Update Specimen Provider called");

		MethodOutcome method = new MethodOutcome();
		method.setCreated(false);
		OperationOutcome opOutcome = new OperationOutcome();
		method.setOperationOutcome(opOutcome);
		Specimen newSpecimen = null;
		try {
			newSpecimen = specimenDao.update(fhirContext, patient, theId);
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
				"urn:uuid: " + newSpecimen.getId(), IssueSeverity.INFORMATION, IssueType.VALUE));
		method.setId(newSpecimen.getIdElement());
		method.setResource(newSpecimen);
		return method;
	}

	/**
	 * @author sonvt
	 * @param request
	 * @param idType
	 * @return read object version
	 */
	@Read(version = true)
	public Specimen readVread(HttpServletRequest request, @IdParam IdType idType) {
		Specimen object = new Specimen();
		if (idType.hasVersionIdPart()) {
			object = specimenDao.readOrVread(fhirContext, idType);
		} else {
			object = specimenDao.read(fhirContext, idType);
		}
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No Specimen/" + idType.getIdPart()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		return object;
	}

	@Search
	public IBundleProvider searchSpecimen(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
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
				results = specimenDao.search(fhirContext, active, resid, _lastUpdated, _tag, _profile, _query,
						_security, _content, _page, sortParam, count);
				// return results;
			} else
				results = specimenDao.search(fhirContext, active, resid, _lastUpdated, _tag, _profile, _query,
						_security, _content, _page, null, count);
			final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(specimenDao.countMatchesAdvancedTotal(fhirContext, active,
							resid, _lastUpdated, _tag, _profile, _query, _security, _content)));
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

	@Operation(name = "$total", idempotent = true)
	public Parameters getTotal(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = specimenDao.countMatchesAdvancedTotal(fhirContext, active, resid, _lastUpdated, _tag, _profile,
				_query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}
}
