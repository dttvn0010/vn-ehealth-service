package vn.ehealth.hl7.fhir.term.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ConceptMap;
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

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
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
import vn.ehealth.hl7.fhir.providers.BaseController;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.term.dao.impl.ConceptMapDao;
import vn.ehealth.hl7.fhir.term.entity.ConceptMapEntity;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Component
public class ConceptMapProvider extends BaseController<ConceptMapEntity, ConceptMap> implements IResourceProvider {
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return ConceptMap.class;
	}

	@Autowired
	ConceptMapDao conceptMapDao;

	private static final Logger log = LoggerFactory.getLogger(ConceptMapProvider.class);


	@Search
	public IBundleProvider searchConceptMap(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OptionalParam(name = ConstantKeys.SP_DEPENDSON) UriParam dependson,
			@OptionalParam(name = ConstantKeys.SP_DESCRIPTION) StringParam description,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_JURIS) TokenParam jurisdiction,
			@OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OptionalParam(name = ConstantKeys.SP_OTHER) UriParam other,
			@OptionalParam(name = ConstantKeys.SP_PRODUCT) UriParam product,
			@OptionalParam(name = ConstantKeys.SP_PUBLISHER) StringParam publisher,
			@OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
			@OptionalParam(name = ConstantKeys.SP_SOURCE_SYSTEM) UriParam source,
			@OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OptionalParam(name = ConstantKeys.SP_TARGET) UriParam target,
			@OptionalParam(name = ConstantKeys.SP_TITLE) StringParam title,
			@OptionalParam(name = ConstantKeys.SP_URL) UriParam url,
			@OptionalParam(name = ConstantKeys.SP_VERSION) TokenParam version,
			// Parameters for all resources
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = "_lastUpdated") DateRangeParam _lastUpdated,
			@OptionalParam(name = "_tag") TokenParam _tag, @OptionalParam(name = "_profile") UriParam _profile,
			@OptionalParam(name = "_query") TokenParam _query, @OptionalParam(name = "_security") TokenParam _security,
			@OptionalParam(name = "_content") StringParam _content,
			// Search result parameters
			@OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Sort SortSpec theSort, @Count Integer count)
			throws OperationOutcomeException {
		log.debug("search ConceptMap Provider called");
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<Resource> results = new ArrayList<Resource>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = conceptMapDao.search(fhirContext, active, date, dependson, description, identifier,
						jurisdiction, name, other, product, publisher, code, source, status, target, title, url,
						version, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam,
						count);
			} else
				results = conceptMapDao.search(fhirContext, active, date, dependson, description, identifier,
						jurisdiction, name, other, product, publisher, code, source, status, target, title, url,
						version, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, null, count);
			final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(conceptMapDao.findMatchesAdvancedTotal(fhirContext, active,
							date, dependson, description, identifier, jurisdiction, name, other, product, publisher,
							code, source, status, target, title, url, version, resid, _lastUpdated, _tag, _profile,
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

	@Delete
	public ConceptMap delete(HttpServletRequest request, @IdParam IdType internalId) {
		log.debug("delete ConceptMap Provider called");
		// String permissionAccept =
		// TerminologyOauth2Keys.ConceptMapOauth2.C_MAP_DELETE;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		ConceptMap conceptMap = conceptMapDao.remove(fhirContext, internalId);
		if (conceptMap == null) {
			log.error("Couldn't delete ConceptMap" + internalId);
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("ConceptMap is not exit"), OperationOutcome.IssueSeverity.ERROR,
					OperationOutcome.IssueType.NOTFOUND);
		}
		return conceptMap;
	}

	@Update
	public MethodOutcome update(HttpServletRequest request, @IdParam IdType idType, @ResourceParam ConceptMap object) {

		log.debug("update ConceptMap Provider called");
		// String permissionAccept = TerminologyOauth2Keys.ConceptMapOauth2.C_MAP_ADD;
		// OAuth2Util.checkOauth2(request, permissionAccept);
		MethodOutcome method = new MethodOutcome();
		method.setCreated(false);
		OperationOutcome opOutcome = new OperationOutcome();
		method.setOperationOutcome(opOutcome);
		ConceptMap newConceptMap = null;
		try {
			newConceptMap = conceptMapDao.update(fhirContext, object, idType);
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
				"urn:uuid:" + newConceptMap.getId(), IssueSeverity.INFORMATION, IssueType.VALUE));
		method.setId(newConceptMap.getIdElement());
		method.setResource(newConceptMap);
		return method;
	}

	@Operation(name = "$translate", idempotent = true)
	public Parameters findMatchesAdvanced(@OperationParam(name = "code") TokenParam code,
			@OperationParam(name = "system") UriParam system, @OperationParam(name = "version") StringParam version,
			@OperationParam(name = "source") UriParam source, @OperationParam(name = "coding") Coding coding,
			@OperationParam(name = "target") UriParam target, @OperationParam(name = "reverse") StringParam reverse) {

		Parameters retVal = new Parameters();
		retVal = conceptMapDao.getTranslateParams(code, system, version, source, coding, target, reverse);
		return retVal;
	}

	@Operation(name = "$closure", idempotent = true)
	public ConceptMap getClosureParams(@OperationParam(name = "name") StringParam name,
			@OperationParam(name = "version") StringParam version, @OperationParam(name = "concept") Coding concept) {
		ConceptMap retVal = new ConceptMap();
		retVal = conceptMapDao.getClosureParams(name, version, concept);
		return retVal;
	}

	@Operation(name = "$total", idempotent = true)
	public Parameters findMatchesAdvancedTotal(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
			@OptionalParam(name = ConstantKeys.SP_DEPENDSON) UriParam dependson,
			@OptionalParam(name = ConstantKeys.SP_DESCRIPTION) StringParam description,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_JURIS) TokenParam jurisdiction,
			@OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OptionalParam(name = ConstantKeys.SP_OTHER) UriParam other,
			@OptionalParam(name = ConstantKeys.SP_PRODUCT) UriParam product,
			@OptionalParam(name = ConstantKeys.SP_PUBLISHER) StringParam publisher,
			@OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
			@OptionalParam(name = ConstantKeys.SP_SOURCE_SYSTEM) UriParam source,
			@OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OptionalParam(name = ConstantKeys.SP_TARGET) UriParam target,
			@OptionalParam(name = ConstantKeys.SP_TITLE) StringParam title,
			@OptionalParam(name = ConstantKeys.SP_URL) UriParam url,
			@OptionalParam(name = ConstantKeys.SP_VERSION) TokenParam version,
			// Parameters for all resources
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = "_lastUpdated") DateRangeParam _lastUpdated,
			@OptionalParam(name = "_tag") TokenParam _tag, @OptionalParam(name = "_profile") UriParam _profile,
			@OptionalParam(name = "_query") TokenParam _query, @OptionalParam(name = "_security") TokenParam _security,
			@OptionalParam(name = "_content") StringParam _content) {
		Parameters retVal = new Parameters();
		long total = conceptMapDao.findMatchesAdvancedTotal(fhirContext, active, date, dependson, description,
				identifier, jurisdiction, name, other, product, publisher, code, source, status, target, title, url,
				version, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

    @Override
    protected BaseDao<ConceptMapEntity, ConceptMap> getDao() {
        return conceptMapDao;
    }
}
