package vn.ehealth.hl7.fhir.provider.providers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
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
import ca.uhn.fhir.rest.annotation.Update;
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
import vn.ehealth.hl7.fhir.provider.dao.IHealthcareService;

@Component
public class HealthcareServiceProvider implements IResourceProvider {
    @Autowired
    FhirContext fhirContext;

    @Autowired
    IHealthcareService healthcareServiceDao;

    private static final Logger log = LoggerFactory.getLogger(HealthcareServiceProvider.class);

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return HealthcareService.class;
    }

    @Create
    public MethodOutcome create(HttpServletRequest theRequest, @ResourceParam HealthcareService obj) {
        log.debug("Create HealthcareService Provider called");
        // String permissionAccept =
        // TerminologyOauth2Keys.CodeSystemOauth2.CODESYSTEM_ADD;
        // OAuth2Util.checkOauth2(theRequest, permissionAccept);
        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();

        method.setOperationOutcome(opOutcome);
        HealthcareService mongoObj = null;
        try {
            mongoObj = healthcareServiceDao.create(fhirContext, obj);
            List<String> myString = new ArrayList<>();
            myString.add("HealthcareService/" + mongoObj.getIdElement());
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

    @Update
    public MethodOutcome update(@IdParam IdType theId, @ResourceParam HealthcareService patient) {

        log.debug("Update HealthcareService Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);
        HealthcareService newHealthcareService = null;
        try {
            newHealthcareService = healthcareServiceDao.update(fhirContext, patient, theId);
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
        method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Update succsess",
                "urn:uuid: " + newHealthcareService.getId(), IssueSeverity.INFORMATION, IssueType.INCOMPLETE));
        method.setId(newHealthcareService.getIdElement());
        method.setResource(newHealthcareService);
        return method;
    }

    @Read
    public HealthcareService readHealthcareService(HttpServletRequest request, @IdParam IdType internalId) {
        log.debug("Read HealthcareService Provider called");
        // String permissionAccept =
        // TerminologyOauth2Keys.CodeSystemOauth2.CODESYSTEM_VIEW;
        // OAuth2Util.checkOauth2(request, permissionAccept);
        HealthcareService object = healthcareServiceDao.read(fhirContext, internalId);
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
    public HealthcareService readVread(HttpServletRequest request, @IdParam IdType idType) {
        HealthcareService object = new HealthcareService();
        if (idType.hasVersionIdPart()) {
            object = healthcareServiceDao.readOrVread(fhirContext, idType);
        } else {
            object = healthcareServiceDao.read(fhirContext, idType);
        }
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No HealthcareService/" + idType.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }

    @Search
    public List<Resource> searchHealthcareService(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CHARACTERISTIC) TokenParam characteristic,
            @OptionalParam(name = ConstantKeys.SP_ENDPOINT) ReferenceParam endpoint,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_ORG) ReferenceParam organization,
            @OptionalParam(name = ConstantKeys.SP_PROGRAMNAME) StringParam programname,
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

        log.debug("search HealthcareService Provider called");
        if (count != null && count > 50) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("Total is not gre > 50"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.INFORMATIONAL);
        } else {
            if (theSort != null) {
                String sortParam = theSort.getParamName();
                List<Resource> results = healthcareServiceDao.search(fhirContext, active, category, characteristic,
                        endpoint, identifier, location, name, organization, programname, type, resid, _lastUpdated,
                        _tag, _profile, _query, _security, _content, _page, sortParam, count);
                return results;
            }
            List<Resource> results = healthcareServiceDao.search(fhirContext, active, category, characteristic,
                    endpoint, identifier, location, name, organization, programname, type, resid, _lastUpdated, _tag,
                    _profile, _query, _security, _content, _page, "", count);
            return results;
        }
    }

    @Delete
    public HealthcareService removeHealthcareService(HttpServletRequest request, @IdParam IdType internalId) {
        log.debug("delete HealthcareService Provider called");
        // String permissionAccept =
        // TerminologyOauth2Keys.CodeSystemOauth2.CODESYSTEM_DELETE;
        // OAuth2Util.checkOauth2(request, permissionAccept);
        HealthcareService organization = healthcareServiceDao.remove(fhirContext, internalId);
        if (organization == null) {
            log.error("Couldn't remove HealthcareService" + internalId);
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("HealthcareService is not exit"),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.INFORMATIONAL);
        }
        return organization;
    }

    @Operation(name = "$total", idempotent = true)
    public Parameters findMatchesAdvancedTotal(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CHARACTERISTIC) TokenParam characteristic,
            @OptionalParam(name = ConstantKeys.SP_ENDPOINT) ReferenceParam endpoint,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_ORG) ReferenceParam organization,
            @OptionalParam(name = ConstantKeys.SP_PROGRAMNAME) StringParam programname,
            @OptionalParam(name = ConstantKeys.SP_TYPE) TokenParam type,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content) {
        Parameters retVal = new Parameters();
        long total = healthcareServiceDao.countMatchesAdvancedTotal(fhirContext, active, category, characteristic,
                endpoint, identifier, location, name, organization, programname, type, resid, _lastUpdated, _tag,
                _profile, _query, _security, _content);
        retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
        return retVal;
    }
}
