package vn.ehealth.hl7.fhir.r4.providers.patient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.controller.BaseController;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeException;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.patient.dao.impl.RelatedPersonDao;
import vn.ehealth.hl7.fhir.patient.entity.RelatedPersonEntity;

@Component
public class RelatedPersonProvider extends BaseController<RelatedPersonEntity, RelatedPerson>
		implements IResourceProvider {
	@Autowired
	RelatedPersonDao relatedPersonDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return RelatedPerson.class;
	}

	@Override
	protected List<String> getProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Search
	public IBundleProvider search(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
			@OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
			@OptionalParam(name = ConstantKeys.SP_BIRTHDATE) DateRangeParam birthDate,
			@OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
			@OptionalParam(name = ConstantKeys.SP_GENDER) StringParam gender,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
			@OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
			@OptionalParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,
			// COMMON
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
			// Search result parameters
			@OptionalParam(name = ConstantKeys.SP_PAGE) NumberParam _page, @Sort SortSpec theSort, @Count Integer count)
			throws OperationOutcomeException {
		// log.debug("Search RelatedPerson Provider called");
		// String permissionAccept = PatientOauth2Keys.RelatedPersonOauth2.RELATED_LIST;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<Resource> results = new ArrayList<Resource>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = relatedPersonDao.search(fhirContext, address, addressCity, addressCountry, addressState,
						birthDate, email, gender, identifier, name, patient, phone, phonetic, telecom, resid,
						_lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam, count);
			} else
				results = relatedPersonDao.search(fhirContext, address, addressCity, addressCountry, addressState,
						birthDate, email, gender, identifier, name, patient, phone, phonetic, telecom, resid,
						_lastUpdated, _tag, _profile, _query, _security, _content, _page, "", count);

			final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);
			// return finalResults;

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(
							relatedPersonDao.findMatchesAdvancedTotal(fhirContext, address, addressCity, addressCountry,
									addressState, birthDate, email, gender, identifier, name, patient, phone, phonetic,
									telecom, resid, _lastUpdated, _tag, _profile, _query, _security, _content)));
				}

				@Override
				public Integer preferredPageSize() {
					return null;
				}

				@Override
				public String getUuid() {
					return null;
				}

				@Override
				public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
					return finalResults;
				}

				@Override
				public IPrimitiveType<Date> getPublished() {
					return null;
				}
			};

		}
	}

	@Operation(name = "$total", idempotent = true)
	public Parameters getTotal(HttpServletRequest request,
			@OperationParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
			@OperationParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
			@OperationParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
			@OperationParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
			@OperationParam(name = ConstantKeys.SP_BIRTHDATE) DateRangeParam birthDate,
			@OperationParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
			@OperationParam(name = ConstantKeys.SP_GENDER) StringParam gender,
			@OperationParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OperationParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OperationParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
			@OperationParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
			@OperationParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
			@OperationParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,
			@OperationParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OperationParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OperationParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OperationParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OperationParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OperationParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OperationParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = relatedPersonDao.findMatchesAdvancedTotal(fhirContext, address, addressCity, addressCountry,
				addressState, birthDate, email, gender, identifier, name, patient, phone, phonetic, telecom, resid,
				_lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	@Override
	protected BaseDao<RelatedPersonEntity, RelatedPerson> getDao() {
		return relatedPersonDao;
	}
}
