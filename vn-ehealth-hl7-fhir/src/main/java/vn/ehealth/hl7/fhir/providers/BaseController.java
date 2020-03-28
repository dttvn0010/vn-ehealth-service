package vn.ehealth.hl7.fhir.providers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.History;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Patch;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.PatchTypeEnum;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.ProviderResponseLibrary;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;

public abstract class BaseController<ENT extends BaseResource, FHIR extends DomainResource> {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);
    abstract protected BaseDao<ENT,FHIR> getDao();
    
    @Autowired protected FhirContext fhirContext;
    
    @Create
    public MethodOutcome create(HttpServletRequest theRequest, @ResourceParam FHIR object) {
        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);
        FHIR mongoObject = null;
        try {
            mongoObject = getDao().create(object);
            List<String> myString = new ArrayList<>();
            myString.add("urn:uuid/" + mongoObject.getIdElement());
            method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
                    "urn:uuid:" + mongoObject.getId(), IssueSeverity.INFORMATION, IssueType.VALUE, myString));
            method.setId(mongoObject.getIdElement());
            method.setResource(mongoObject);
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
    public FHIR read(HttpServletRequest request, @IdParam IdType internalId) {
        var object = getDao().read(internalId);
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Entity/" + internalId.getIdPart()),
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
    public FHIR readVread(HttpServletRequest request, @IdParam IdType idType) {
        FHIR object = null;
        if (idType.hasVersionIdPart()) {
            object = getDao().readOrVread(idType);
        } else {
            object = getDao().read(idType);
        }
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Entity/" + idType.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }
    
    @Delete
    public void delete(HttpServletRequest request, @IdParam IdType internalId) {
        log.debug("Delete Entity called");
        var object = getDao().remove(internalId);
        if (object == null) {
            log.error("Couldn't delete object" + internalId);
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("object is not exit"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.NOTFOUND);

        }
        return;
    }

    @Update
    public MethodOutcome update(HttpServletRequest theRequest, @IdParam IdType theId, @ResourceParam FHIR object) {
        log.debug("Update Object called");
        MethodOutcome method = new MethodOutcome();
        method.setCreated(false);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);
        FHIR newObject = null;
        try {
            newObject = getDao().update(object, theId);
        } catch (Exception ex) {
            ProviderResponseLibrary.handleException(method, ex);
        }
        method.setId(newObject.getIdElement());
        method.setResource(newObject);
        return method;
    }
    
    
    @Patch
    public OperationOutcome patch(@IdParam IdType theId, PatchTypeEnum thePatchType, @ResourceParam String theBody) {
        // Dummy Operations
        if (thePatchType == PatchTypeEnum.JSON_PATCH) {
            // do something
        }
        if (thePatchType == PatchTypeEnum.XML_PATCH) {
            // do something
        }

        OperationOutcome retVal = new OperationOutcome();
        retVal.getText().setDivAsString("<div>OK</div>");
        return retVal;
    }
    

    @History
    public List<FHIR> getInstanceHistory(@IdParam IdType theId, @OptionalParam(name = "_since") InstantType theSince,
            @OptionalParam(name = "_at") DateRangeParam theAt,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Count Integer count) {
    	
    	FHIR object = null;
        if (theId.hasVersionIdPart()) {
            object = getDao().readOrVread(theId);
        } else {
            object = getDao().read(theId);
        }
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Entity/" + theId.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        
        List<FHIR> retVal = new ArrayList<FHIR>();
        retVal = getDao().getHistory(theId, theSince, theAt, _page, count);
        return retVal;
    }

    @History
    public List<FHIR> getResourceHistory(@OptionalParam(name = "_since") InstantType theSince,
            @OptionalParam(name = "_at") DateRangeParam theAt,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Count Integer count) {
        List<FHIR> retVal = new ArrayList<FHIR>();
        retVal = getDao().getHistory(null, theSince, theAt, _page, count);
        return retVal;
    }
}
