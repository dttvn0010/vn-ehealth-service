package vn.ehealth.hl7.fhir.provider.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Parameters;
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
import vn.ehealth.hl7.fhir.provider.dao.IDevice;

@Component
public class DeviceProvider implements IResourceProvider {
	@Autowired
	FhirContext fhirContext;

	@Autowired
	IDevice deviceDao;

	private static final Logger log = LoggerFactory.getLogger(DeviceProvider.class);

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Device.class;
	}

	@Create
	public MethodOutcome createDevice(HttpServletRequest theRequest, @ResourceParam Device obj) {

		log.debug("Create Device Provider called");

		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		method.setOperationOutcome(opOutcome);
		Device mongoDevice = null;
		try {
			mongoDevice = deviceDao.create(fhirContext, obj);
			List<String> myString = new ArrayList<>();
			myString.add("Device/" + mongoDevice.getIdElement());
			method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
					"urn:uuid: " + mongoDevice.getId(), IssueSeverity.INFORMATION, IssueType.VALUE, myString));
			method.setId(mongoDevice.getIdElement());
			method.setResource(mongoDevice);
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
	public Device readDevice(HttpServletRequest request, @IdParam IdType internalId) {

		Device object = deviceDao.read(fhirContext, internalId);
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No Device/" + internalId.getIdPart()),
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
	public Device readVread(HttpServletRequest request, @IdParam IdType idType) {
		Device object = new Device();
		if (idType.hasVersionIdPart()) {
			object = deviceDao.readOrVread(fhirContext, idType);
		} else {
			object = deviceDao.read(fhirContext, idType);
		}
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No Device/" + idType.getIdPart()),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		return object;
	}

	@Search
	public IBundleProvider searchDevice(HttpServletRequest request,
			@OptionalParam(name = "device-name") StringParam deviceName,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = "location") ReferenceParam location,
			@OptionalParam(name = "manufacturer") StringParam manufacturer,
			@OptionalParam(name = "model") StringParam model,
			@OptionalParam(name = "organization") ReferenceParam organization,
			@OptionalParam(name = "patient") ReferenceParam patient,
			@OptionalParam(name = "udi-carrier") StringParam udiCarrier,
			@OptionalParam(name = "udi-di") StringParam udiDi, @OptionalParam(name = "url") UriParam url,
			@OptionalParam(name = "status") TokenParam status, @OptionalParam(name = "type") TokenParam type,
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
				results = deviceDao.search(fhirContext, deviceName, identifier, location, manufacturer, model,
						organization, patient, udiCarrier, udiDi, url, status, type, resid, _lastUpdated, _tag,
						_profile, _query, _security, _content, _page, sortParam, count);
			} else
				results = deviceDao.search(fhirContext, deviceName, identifier, location, manufacturer, model,
						organization, patient, udiCarrier, udiDi, url, status, type, resid, _lastUpdated, _tag,
						_profile, _query, _security, _content, _page, "", count);
			final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(deviceDao.findMatchesAdvancedTotal(fhirContext, deviceName,
							identifier, location, manufacturer, model, organization, patient, udiCarrier, udiDi, url,
							status, type, resid, _lastUpdated, _tag, _profile, _query, _security, _content)));
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
	public Device deleteDevice(HttpServletRequest request, @IdParam IdType internalId) {
		Device obj = deviceDao.remove(fhirContext, internalId);
		if (obj == null) {
			log.error("Couldn't delete Device" + internalId);
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Device is not exit"), OperationOutcome.IssueSeverity.ERROR,
					OperationOutcome.IssueType.NOTFOUND);
		}
		return obj;
	}

	@Update
	public MethodOutcome updateDevice(@IdParam IdType theId, @ResourceParam Device patient) {

		log.debug("Update Device Provider called");

		MethodOutcome method = new MethodOutcome();
		method.setCreated(false);
		OperationOutcome opOutcome = new OperationOutcome();
		method.setOperationOutcome(opOutcome);
		Device newDevice = null;
		try {
			newDevice = deviceDao.update(fhirContext, patient, theId);
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
				"urn:uuid: " + newDevice.getId(), IssueSeverity.INFORMATION, IssueType.VALUE));
		method.setId(newDevice.getIdElement());
		method.setResource(newDevice);
		return method;
	}

	@Operation(name = "$total", idempotent = true)
	public Parameters findMatchesAdvancedTotal(HttpServletRequest request,
			@OptionalParam(name = "device-name") StringParam deviceName,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = "location") ReferenceParam location,
			@OptionalParam(name = "manufacturer") StringParam manufacturer,
			@OptionalParam(name = "model") StringParam model,
			@OptionalParam(name = "organization") ReferenceParam organization,
			@OptionalParam(name = "patient") ReferenceParam patient,
			@OptionalParam(name = "udi-carrier") StringParam udiCarrier,
			@OptionalParam(name = "udi-di") StringParam udiDi, @OptionalParam(name = "url") UriParam url,
			@OptionalParam(name = "status") TokenParam status, @OptionalParam(name = "type") TokenParam type,
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = deviceDao.findMatchesAdvancedTotal(fhirContext, deviceName, identifier, location, manufacturer,
				model, organization, patient, udiCarrier, udiDi, url, status, type, resid, _lastUpdated, _tag, _profile,
				_query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}
}
