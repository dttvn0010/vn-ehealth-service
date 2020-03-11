package vn.ehealth.hl7.fhir.term.providers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.ValueSet;
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
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.term.dao.IValueSet;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Component
public class ValueSetProvider implements IResourceProvider {
    @Autowired
    FhirContext fhirContext;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return ValueSet.class;
    }

    @Autowired
    IValueSet valueSetDao;

    private static final Logger log = LoggerFactory.getLogger(ValueSetProvider.class);

    @Create
    public MethodOutcome create(HttpServletRequest theRequest, @ResourceParam ValueSet object) {
        log.debug("Create ValueSet Provider called");
        // String permissionAccept = TerminologyOauth2Keys.ValueSetOauth2.VALUESET_ADD;
        // OAuth2Util.checkOauth2(theRequest, permissionAccept);
        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();

        method.setOperationOutcome(opOutcome);
        ValueSet mongoObj = null;
        try {
            mongoObj = valueSetDao.create(fhirContext, object);
            List<String> myString = new ArrayList<>();
            myString.add("ValueSet/" + mongoObj.getIdElement());
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
    public ValueSet readValueSet(HttpServletRequest request, @IdParam IdType internalId) {
        log.debug("Read ValueSet Provider called");
        // String permissionAccept = TerminologyOauth2Keys.ValueSetOauth2.VALUESET_VIEW;
        // OAuth2Util.checkOauth2(request, permissionAccept);
        ValueSet object = valueSetDao.read(fhirContext, internalId);
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No ValueSet/" + internalId.getIdPart()),
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
    public ValueSet readVread(HttpServletRequest request, @IdParam IdType idType) {
        ValueSet object = new ValueSet();
        if (idType.hasVersionIdPart()) {
            object = valueSetDao.readOrVread(fhirContext, idType);
        } else {
            object = valueSetDao.read(fhirContext, idType);
        }
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No ValueSet/" + idType.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }

    @Search
    public List<Resource> searchValueSet(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DESCRIPTION) StringParam description,
            @OptionalParam(name = ConstantKeys.SP_EXPANSION) UriParam expansion,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_JURIS) TokenParam jurisdiction,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_PUBLISHER) StringParam publisher,
            @OptionalParam(name = ConstantKeys.SP_REFERENCE) UriParam reference,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
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
        log.debug("Search ValueSet Provider called");
        // String permissionAccept = TerminologyOauth2Keys.ValueSetOauth2.VALUESET_LIST;
        // OAuth2Util.checkOauth2(request, permissionAccept);
        if (count != null && count > 50) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("Total is not gre > 50"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.INFORMATIONAL);
        } else {
            if (theSort != null) {
                String sortParam = theSort.getParamName();
                List<Resource> results = valueSetDao.search(fhirContext, active, date, description, expansion,
                        identifier, jurisdiction, name, publisher, reference, status, title, url, version, resid,
                        _lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam, count);
                return results;
            }
            List<Resource> results = valueSetDao.search(fhirContext, active, date, description, expansion, identifier,
                    jurisdiction, name, publisher, reference, status, title, url, version, resid, _lastUpdated, _tag,
                    _profile, _query, _security, _content, _page, null, count);
            return results;
        }
    }

    @Delete
    public ValueSet deleteValueSet(HttpServletRequest request, @IdParam IdType internalId) {
        log.debug("Delete ValueSet Provider called");
        // String permissionAccept =
        // TerminologyOauth2Keys.ValueSetOauth2.VALUESET_DELETE;
        // OAuth2Util.checkOauth2(request, permissionAccept);
        ValueSet valueSet = valueSetDao.remove(fhirContext, internalId);
        if (valueSet == null) {
            log.error("Couldn't delete valueSet" + internalId);
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("ValueSet is not exit"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.INFORMATIONAL);
        }
        return valueSet;
    }

    @Update
    public MethodOutcome update(HttpServletRequest request, @IdParam IdType theId, @ResourceParam ValueSet object) {

        log.debug("Update ValueSet Provider called");
        // String permissionAccept = TerminologyOauth2Keys.ValueSetOauth2.VALUESET_ADD;
        // OAuth2Util.checkOauth2(request, permissionAccept);
        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);
        ValueSet newValueSet = null;
        try {
            newValueSet = valueSetDao.update(fhirContext, object, theId);
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
                "urn:uuid:" + newValueSet.getId(), IssueSeverity.INFORMATION, IssueType.INFORMATIONAL));
        method.setId(newValueSet.getIdElement());
        method.setResource(newValueSet);
        return method;
    }

    @Operation(name = "$total", idempotent = true)
    public Parameters getTotal(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DESCRIPTION) StringParam description,
            @OptionalParam(name = ConstantKeys.SP_EXPANSION) UriParam expansion,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_JURIS) TokenParam jurisdiction,
            @OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
            @OptionalParam(name = ConstantKeys.SP_PUBLISHER) StringParam publisher,
            @OptionalParam(name = ConstantKeys.SP_REFERENCE) UriParam reference,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
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
        long total = valueSetDao.getTotal(fhirContext, date, description, expansion, identifier, jurisdiction, name,
                publisher, reference, status, title, url, version, resid, _lastUpdated, _tag, _profile, _query,
                _security, _content);
        retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
        return retVal;
    }
}
