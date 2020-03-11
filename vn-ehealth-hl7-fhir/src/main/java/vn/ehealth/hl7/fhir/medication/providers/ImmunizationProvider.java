package vn.ehealth.hl7.fhir.medication.providers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Immunization;
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
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.medication.dao.IImmunization;

@Component
public class ImmunizationProvider implements IResourceProvider {
    @Autowired
    FhirContext fhirContext;

    @Autowired
    IImmunization immunizationDao;

    private static final Logger log = LoggerFactory.getLogger(ImmunizationProvider.class);

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Immunization.class;
    }

    @Create
    public MethodOutcome createImmunization(HttpServletRequest theRequest, @ResourceParam Immunization obj) {

        log.debug("Create Immunization Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);
        Immunization mongoImmunization = null;
        try {
            mongoImmunization = immunizationDao.create(fhirContext, obj);
            List<String> myString = new ArrayList<>();
            myString.add("Immunization/" + mongoImmunization.getIdElement());
            method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
                    "urn:uuid: " + mongoImmunization.getId(), IssueSeverity.INFORMATION, IssueType.INCOMPLETE,
                    myString));
            method.setId(mongoImmunization.getIdElement());
            method.setResource(mongoImmunization);
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
    public Immunization readImmunization(HttpServletRequest request, @IdParam IdType internalId) {

        Immunization object = immunizationDao.read(fhirContext, internalId);
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Immunization/" + internalId.getIdPart()),
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
    public Immunization readVread(HttpServletRequest request, @IdParam IdType idType) {
        Immunization object = new Immunization();
        if (idType.hasVersionIdPart()) {
            object = immunizationDao.readOrVread(fhirContext, idType);
        } else {
            object = immunizationDao.read(fhirContext, idType);
        }
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Immunization/" + idType.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }

    @Search
    public List<Resource> searchImmunization(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DOSE_SEQUENCE) NumberParam doseSequence,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_LOT_NUMBER) StringParam lotNumber,
            @OptionalParam(name = ConstantKeys.SP_MANUFACTURER) ReferenceParam manufacturer,
            @OptionalParam(name = ConstantKeys.SP_NOTGIVEN) TokenParam notgiven,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
            @OptionalParam(name = ConstantKeys.SP_REACTION) ReferenceParam reaction,
            @OptionalParam(name = ConstantKeys.SP_REACTION_DATE) DateRangeParam reactionDate,
            @OptionalParam(name = ConstantKeys.SP_REASON) TokenParam reason,
            @OptionalParam(name = ConstantKeys.SP_REASON_NOT_GIVEN) TokenParam reasonNotGiven,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_VACCINE_CODE) TokenParam vaccineCode,
            // Common
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
                List<Resource> results = immunizationDao.search(fhirContext, active, date, doseSequence, identifier,
                        location, lotNumber, manufacturer, notgiven, patient, practitioner, reaction, reactionDate,
                        reason, reasonNotGiven, status, vaccineCode, resid, _lastUpdated, _tag, _profile, _query,
                        _security, _content, _page, sortParam, count);
                return results;
            }
            List<Resource> results = immunizationDao.search(fhirContext, active, date, doseSequence, identifier,
                    location, lotNumber, manufacturer, notgiven, patient, practitioner, reaction, reactionDate, reason,
                    reasonNotGiven, status, vaccineCode, resid, _lastUpdated, _tag, _profile, _query, _security,
                    _content, _page, null, count);
            return results;
        }
    }

    @Delete
    public Immunization deleteImmunization(HttpServletRequest request, @IdParam IdType internalId) {
        Immunization object = immunizationDao.remove(fhirContext, internalId);
        if (object == null) {
            log.error("Couldn't delete Immunization" + internalId);
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("Immunization is not exit"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.INFORMATIONAL);
        }
        return object;
    }

    @Update
    public MethodOutcome updateImmunization(@IdParam IdType theId, @ResourceParam Immunization patient) {

        log.debug("Update Immunization Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);
        Immunization newImmunization = null;
        try {
            newImmunization = immunizationDao.update(fhirContext, patient, theId);
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
                "urn:uuid: " + newImmunization.getId(), IssueSeverity.INFORMATION, IssueType.INCOMPLETE));
        method.setId(newImmunization.getIdElement());
        method.setResource(newImmunization);
        return method;
    }

    @Operation(name = "$total", idempotent = true)
    public Parameters getTotal(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DOSE_SEQUENCE) NumberParam doseSequence,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_LOT_NUMBER) StringParam lotNumber,
            @OptionalParam(name = ConstantKeys.SP_MANUFACTURER) ReferenceParam manufacturer,
            @OptionalParam(name = ConstantKeys.SP_NOTGIVEN) TokenParam notgiven,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
            @OptionalParam(name = ConstantKeys.SP_REACTION) ReferenceParam reaction,
            @OptionalParam(name = ConstantKeys.SP_REACTION_DATE) DateRangeParam reactionDate,
            @OptionalParam(name = ConstantKeys.SP_REASON) TokenParam reason,
            @OptionalParam(name = ConstantKeys.SP_REASON_NOT_GIVEN) TokenParam reasonNotGiven,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_VACCINE_CODE) TokenParam vaccineCode,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content) {
        Parameters retVal = new Parameters();
        long total = immunizationDao.countMatchesAdvancedTotal(fhirContext, active, date, doseSequence, identifier,
                location, lotNumber, manufacturer, notgiven, patient, practitioner, reaction, reactionDate, reason,
                reasonNotGiven, status, vaccineCode, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
        return retVal;
    }
}
