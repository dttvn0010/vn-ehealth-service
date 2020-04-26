package vn.ehealth.hl7.fhir.r4.providers.terminology;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
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
import vn.ehealth.hl7.fhir.factory.ProviderResponseLibrary;
import vn.ehealth.hl7.fhir.term.dao.impl.CodeSystemDao;
import vn.ehealth.hl7.fhir.term.entity.CodeSystemEntity;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Component
public class CodeSystemProvider extends BaseController<CodeSystemEntity, CodeSystem> implements IResourceProvider {

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return CodeSystem.class;
	}

	@Autowired
	CodeSystemDao codeSystemDao;

	@Override
	protected List<String> getProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	private static final Logger log = LoggerFactory.getLogger(CodeSystemProvider.class);

	@Create
	public MethodOutcome create(HttpServletRequest theRequest, @ResourceParam CodeSystem object) {
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		CodeSystem newObj = null;
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
	public CodeSystem read(HttpServletRequest request, @IdParam IdType theId) {
		var object = getDao().read(theId);
		if (object == null) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("No " + theId.getValue() + " found"),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
		}
		return object;
	}
	
	@Search
	public IBundleProvider searchCodeSystem(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) TokenParam contentMode,
			@OptionalParam(name = ConstantKeys.SP_DESCRIPTION) StringParam description,
			@OptionalParam(name = ConstantKeys.SP_JURIS) TokenParam jurisdiction,
			@OptionalParam(name = ConstantKeys.SP_LANGUAGE) TokenParam language,
			@OptionalParam(name = ConstantKeys.SP_PUBLISHER) StringParam publisher,
			@OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OptionalParam(name = ConstantKeys.SP_SYSTEM) UriParam system,
			@OptionalParam(name = ConstantKeys.SP_TITLE) StringParam title,
			@OptionalParam(name = ConstantKeys.SP_URL) UriParam url,
			@OptionalParam(name = ConstantKeys.SP_VERSION) TokenParam version,
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
		log.debug("search CodeSystem Provider called");
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<Resource> results = new ArrayList<Resource>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = codeSystemDao.search(fhirContext, date, identifier, name, code, contentMode, description,
						jurisdiction, language, publisher, status, system, title, url, version, resid, _lastUpdated,
						_tag, _profile, _query, _security, _content, _page, sortParam, count);
			} else
				results = codeSystemDao.search(fhirContext, date, identifier, name, code, contentMode, description,
						jurisdiction, language, publisher, status, system, title, url, version, resid, _lastUpdated,
						_tag, _profile, _query, _security, _content, _page, null, count);
			final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return (int) codeSystemDao.findMatchesAdvancedTotal(fhirContext, date, identifier, name, code,
							contentMode, description, jurisdiction, language, publisher, status, system, title,
							url, version, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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

	@Operation(name = "$lookup", idempotent = true)
	public Parameters lookUp(@OperationParam(name = "code") StringParam code,
			@OperationParam(name = "system") UriParam system, @OperationParam(name = "version") StringParam version,
			@OperationParam(name = "coding") TokenParam coding, @OperationParam(name = "date") DateRangeParam date,
			@OperationParam(name = "displayLanguage") TokenParam displayLanguage,
			@OperationParam(name = "property") TokenParam property) {

		Parameters retVal = new Parameters();
		retVal = codeSystemDao.lookUp(code, system, version, coding, date, displayLanguage, property);
		return retVal;
	}
	
	@Operation(name = "$find-matches")
	public Parameters findMatches(@ResourceParam Parameters params) {
		
		Parameters retVal = new Parameters();
		retVal = codeSystemDao.findMatches(params);
		return retVal; 
	}
	

	@Operation(name = "$total", idempotent = true)
	public Parameters findMatchesAdvancedTotal(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) TokenParam contentMode,
			@OptionalParam(name = ConstantKeys.SP_DESCRIPTION) StringParam description,
			@OptionalParam(name = ConstantKeys.SP_JURIS) TokenParam jurisdiction,
			@OptionalParam(name = ConstantKeys.SP_LANGUAGE) TokenParam language,
			@OptionalParam(name = ConstantKeys.SP_PUBLISHER) StringParam publisher,
			@OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OptionalParam(name = ConstantKeys.SP_SYSTEM) UriParam system,
			@OptionalParam(name = ConstantKeys.SP_TITLE) StringParam title,
			@OptionalParam(name = ConstantKeys.SP_URL) UriParam url,
			@OptionalParam(name = ConstantKeys.SP_VERSION) TokenParam version,
			// COMMON
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
		Parameters retVal = new Parameters();
		long total = codeSystemDao.findMatchesAdvancedTotal(fhirContext, date, identifier, name, code, contentMode,
				description, jurisdiction, language, publisher, status, system, title, url, version, resid,
				_lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	@Override
	protected BaseDao<CodeSystemEntity, CodeSystem> getDao() {
		return codeSystemDao;
	}
}
