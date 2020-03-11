package vn.ehealth.hl7.fhir.provider.providers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
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
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
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
import vn.ehealth.hl7.fhir.provider.dao.IOrganization;

@Component
public class OrganizationProvider implements IResourceProvider {
    @Autowired
    FhirContext fhirContext;

    @Autowired
    IOrganization organizationDao;

    private static final Logger log = LoggerFactory.getLogger(OrganizationProvider.class);

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Organization.class;
    }

    @Create
    public MethodOutcome create(HttpServletRequest theRequest, @ResourceParam Organization obj) {
        log.debug("Create Organization Provider called");
        // String permissionAccept =
        // TerminologyOauth2Keys.CodeSystemOauth2.CODESYSTEM_ADD;
        // OAuth2Util.checkOauth2(theRequest, permissionAccept);
        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();

        method.setOperationOutcome(opOutcome);
        Organization mongoObj = null;
        try {
            mongoObj = organizationDao.create(fhirContext, obj);
            List<String> myString = new ArrayList<>();
            myString.add("Organization/" + mongoObj.getIdElement());
            method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
                    "urn:uuid: " + mongoObj.getId(), IssueSeverity.INFORMATION, IssueType.INCOMPLETE, myString));
            method.setId(mongoObj.getIdElement());
            method.setResource(mongoObj);
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
    public Organization readOrganization(HttpServletRequest request, @IdParam IdType internalId) {
        log.debug("Read Organization Provider called");
        // String permissionAccept =
        // TerminologyOauth2Keys.CodeSystemOauth2.CODESYSTEM_VIEW;
        // OAuth2Util.checkOauth2(request, permissionAccept);
        Organization object = organizationDao.read(fhirContext, internalId);
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Location/" + internalId.getIdPart()),
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
    public Organization readVread(HttpServletRequest request, @IdParam IdType idType) {
        Organization object = new Organization();
        if (idType.hasVersionIdPart()) {
            object = organizationDao.readOrVread(fhirContext, idType);
        } else {
            object = organizationDao.read(fhirContext, idType);
        }
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Organization/" + idType.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }

    @Search
    public List<Resource> searchOrganization(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
            @OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
            @OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
            @OptionalParam(name = ConstantKeys.SP_ADDRESS_POSTALCODE) StringParam addressPostalCode,
            @OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
            @OptionalParam(name = ConstantKeys.SP_ADDRESS_USE) TokenParam addressUse,
            @OptionalParam(name = ConstantKeys.SP_ENDPOINT) ReferenceParam endpoint,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partof,
            @OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
            @OptionalParam(name = ConstantKeys.SP_TYPE) TokenParam type,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Sort SortSpec theSort, @Count Integer count)
            throws OperationOutcomeException {

        log.debug("search Organization Provider called");
        if (count != null && count > 50) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("Total is not gre > 50"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.INFORMATIONAL);
        } else {
            if (theSort != null) {
                String sortParam = theSort.getParamName();
                List<Resource> results = organizationDao.search(fhirContext, active, address, addressCity, addressCountry,
                        addressPostalCode, addressState, addressUse, endpoint, identifier, name, partof, phonetic, type,
                        resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam, count);
                return results;
            }
            List<Resource> results = organizationDao.search(fhirContext, active, address, addressCity, addressCountry,
                    addressPostalCode, addressState, addressUse, endpoint, identifier, name, partof, phonetic, type,
                    resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, "", count);
            return results;
        }
    }

    @Delete
    public Organization removeOrganization(HttpServletRequest request, @IdParam IdType internalId) {
        log.debug("delete Organization Provider called");
        // String permissionAccept =
        // TerminologyOauth2Keys.CodeSystemOauth2.CODESYSTEM_DELETE;
        // OAuth2Util.checkOauth2(request, permissionAccept);
        Organization organization = organizationDao.remove(fhirContext, internalId);
        if (organization == null) {
            log.error("Couldn't remove Organization" + internalId);
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("Organization is not exit"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.INFORMATIONAL);
        }
        return organization;
    }

    @Operation(name = "$total", idempotent = true)
    public Parameters findMatchesAdvancedTotal(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
            @OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
            @OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
            @OptionalParam(name = ConstantKeys.SP_ADDRESS_POSTALCODE) StringParam addressPostalCode,
            @OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
            @OptionalParam(name = ConstantKeys.SP_ADDRESS_USE) TokenParam addressUse,
            @OptionalParam(name = ConstantKeys.SP_ENDPOINT) ReferenceParam endpoint,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partof,
            @OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
            @OptionalParam(name = ConstantKeys.SP_TYPE) TokenParam type,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content) {
        Parameters retVal = new Parameters();
        long total = organizationDao.countMatchesAdvancedTotal(fhirContext, active, address, addressCity, addressCountry,
                addressPostalCode, addressState, addressUse, endpoint, identifier, name, partof, phonetic, type, resid,
                _lastUpdated, _tag, _profile, _query, _security, _content);
        retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
        return retVal;
    }
}
