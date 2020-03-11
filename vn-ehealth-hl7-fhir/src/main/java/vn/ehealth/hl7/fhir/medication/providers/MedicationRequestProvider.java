package vn.ehealth.hl7.fhir.medication.providers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationRequest;
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
import vn.ehealth.hl7.fhir.medication.dao.IMedicationRequest;

@Component
public class MedicationRequestProvider implements IResourceProvider {
    @Autowired
    FhirContext fhirContext;

    @Autowired
    IMedicationRequest medicationRequestDao;

    private static final Logger log = LoggerFactory.getLogger(MedicationRequestProvider.class);

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return MedicationRequest.class;
    }

    @Create
    public MethodOutcome createMedicationRequest(HttpServletRequest theRequest, @ResourceParam MedicationRequest obj) {

        log.debug("Create MedicationRequest Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);
        MedicationRequest mongoMedication = null;
        try {
            mongoMedication = medicationRequestDao.create(fhirContext, obj);
            List<String> myString = new ArrayList<>();
            myString.add("MedicationRequest/" + mongoMedication.getIdElement());
            method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
                    "urn:uuid: " + mongoMedication.getId(), IssueSeverity.INFORMATION, IssueType.INCOMPLETE, myString));
            method.setId(mongoMedication.getIdElement());
            method.setResource(mongoMedication);
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
    public MedicationRequest readMedicationRequest(HttpServletRequest request, @IdParam IdType internalId) {

        MedicationRequest object = medicationRequestDao.read(fhirContext, internalId);
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No MedicationRequest/" + internalId.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }

    @Search
    public List<Resource> searchMedicationRequest(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = "category") TokenParam category,
            @OptionalParam(name = "identifier") TokenParam identifier,
            @OptionalParam(name = "intent") TokenParam intent,
            @OptionalParam(name = "priority") TokenParam priority,
            @OptionalParam(name = "status") TokenParam status,
            @OptionalParam(name = "subject") ReferenceParam subject,
            @OptionalParam(name = "requester") ReferenceParam requester,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = "medication") ReferenceParam medication,
            @OptionalParam(name = "context") ReferenceParam context,
            @OptionalParam(name = "intended-dispenser") ReferenceParam intendedDispenser,    
            @OptionalParam(name = "authoredon") DateRangeParam authoredon,
            @OptionalParam(name = "date") DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Sort SortSpec theSort, @Count Integer count)
            throws OperationOutcomeException {
        if (count != null && count > 50) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("Total is not gre > 50"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.INFORMATIONAL);
        } else {
            if (theSort != null) {
                String sortParam = theSort.getParamName();
                List<Resource> results = medicationRequestDao.search(fhirContext, active, code, category, identifier, intent, priority, status,
                        subject, requester, patient, medication, context, intendedDispenser, authoredon, date, resid, _lastUpdated, _tag, _profile,
                        _query, _security, _content, _page, sortParam, count);
                return results;
            }
            List<Resource> results = medicationRequestDao.search(fhirContext, active, code, category, identifier, intent, priority, status,
                    subject, requester, patient, medication, context, intendedDispenser, authoredon, date, resid, _lastUpdated, _tag, _profile,
                    _query, _security, _content, _page, null, count);
            return results;
        }
    }

    @Delete
    public MedicationRequest deleteMedicationRequest(HttpServletRequest request, @IdParam IdType internalId) {
        MedicationRequest obj = medicationRequestDao.remove(fhirContext, internalId);
        if (obj == null) {
            log.error("Couldn't delete MedicationRequest" + internalId);
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("MedicationRequest is not exit"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.INFORMATIONAL);
        }
        return obj;
    }

    @Update
    public MethodOutcome updateMedicationRequest(@IdParam IdType theId, @ResourceParam MedicationRequest patient) {

        log.debug("Update MedicationRequest Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);
        MedicationRequest newMedicationRequest = null;
        try {
            newMedicationRequest = medicationRequestDao.update(fhirContext, patient, theId);
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
                "urn:uuid: " + newMedicationRequest.getId(), IssueSeverity.INFORMATION, IssueType.INCOMPLETE));
        method.setId(newMedicationRequest.getIdElement());
        method.setResource(newMedicationRequest);
        return method;
    }

    @Operation(name = "$total", idempotent = true)
    public Parameters getTotal(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = "category") TokenParam category,
            @OptionalParam(name = "identifier") TokenParam identifier,
            @OptionalParam(name = "intent") TokenParam intent,
            @OptionalParam(name = "priority") TokenParam priority,
            @OptionalParam(name = "status") TokenParam status,
            @OptionalParam(name = "subject") ReferenceParam subject,
            @OptionalParam(name = "requester") ReferenceParam requester,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = "medication") ReferenceParam medication,
            @OptionalParam(name = "context") ReferenceParam context,
            @OptionalParam(name = "intended-dispenser") ReferenceParam intendedDispenser,
            @OptionalParam(name = "authoredon") DateRangeParam authoredon,
            @OptionalParam(name = "date") DateRangeParam date,
            // dung chung
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content) {
        Parameters retVal = new Parameters();
        long total = medicationRequestDao.countMatchesAdvancedTotal(fhirContext, active, code, category, identifier, intent, priority, status,
                subject, requester, patient, medication, context, intendedDispenser, authoredon, date, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
        return retVal;
    }
}
