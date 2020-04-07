package vn.ehealth.hl7.fhir.r4.providers.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.providers.BaseController;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeException;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.provider.dao.impl.LocationDao;
import vn.ehealth.hl7.fhir.provider.entity.LocationEntity;

@Component
public class LocationProvider extends BaseController<LocationEntity, Location> implements IResourceProvider {
	@Autowired
	LocationDao locationDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Location.class;
	}

	@Search
	public IBundleProvider searchLocation(HttpServletRequest request,
			@OptionalParam(name = "address") StringParam address,
			@OptionalParam(name = "address-city") StringParam addressCity,
			@OptionalParam(name = "address-country") StringParam addressCountry,
			@OptionalParam(name = "address-state") StringParam addressState,
			@OptionalParam(name = "endpoint") ReferenceParam endpoint,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = "name") StringParam name, @OptionalParam(name = "near") TokenParam near,
			@OptionalParam(name = "near-distance") QuantityParam nearDistance,
			@OptionalParam(name = "operational-status") TokenParam operationalStatus,
			@OptionalParam(name = "organization") ReferenceParam organization,
			@OptionalParam(name = "partof") ReferenceParam partof, @OptionalParam(name = "status") TokenParam status,
			@OptionalParam(name = "type") TokenParam type,			
			// COMMON
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
			@OptionalParam(name = ConstantKeys.SP_PAGE) NumberParam _page, @Sort SortSpec theSort, @Count Integer count)
			throws OperationOutcomeException {
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<Resource> results = new ArrayList<Resource>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = locationDao.search(fhirContext, address, addressCity, addressCountry, addressState, endpoint,
						identifier, name, near, nearDistance, operationalStatus, organization, partof, status, type,
						resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam, count);
			} else
				results = locationDao.search(fhirContext, address, addressCity, addressCountry, addressState, endpoint,
						identifier, name, near, nearDistance, operationalStatus, organization, partof, status, type,
						resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, "", count);
			final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(locationDao.findMatchesAdvancedTotal(fhirContext, address,
							addressCity, addressCountry, addressState, endpoint, identifier, name, near, nearDistance,
							operationalStatus, organization, partof, status, type, resid, _lastUpdated, _tag, _profile,
							_query, _security, _content)));
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
	public Parameters findMatchesAdvancedTotal(HttpServletRequest request,
			@OptionalParam(name = "address") StringParam address,
			@OptionalParam(name = "address-city") StringParam addressCity,
			@OptionalParam(name = "address-country") StringParam addressCountry,
			@OptionalParam(name = "address-state") StringParam addressState,
			@OptionalParam(name = "endpoint") ReferenceParam endpoint,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = "name") StringParam name, @OptionalParam(name = "near") TokenParam near,
			@OptionalParam(name = "near-distance") QuantityParam nearDistance,
			@OptionalParam(name = "operational-status") TokenParam operationalStatus,
			@OptionalParam(name = "organization") ReferenceParam organization,
			@OptionalParam(name = "partof") ReferenceParam partof, @OptionalParam(name = "status") TokenParam status,
			@OptionalParam(name = "type") TokenParam type,
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = locationDao.findMatchesAdvancedTotal(fhirContext, address, addressCity, addressCountry,
				addressState, endpoint, identifier, name, near, nearDistance, operationalStatus, organization, partof,
				status, type, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	@Override
	protected BaseDao<LocationEntity, Location> getDao() {
		return locationDao;
	}
}
