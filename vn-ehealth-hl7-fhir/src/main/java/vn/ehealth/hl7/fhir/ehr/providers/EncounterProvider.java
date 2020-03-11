package vn.ehealth.hl7.fhir.ehr.providers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Encounter;
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
import vn.ehealth.hl7.fhir.ehr.dao.IEncounter;

@Component
public class EncounterProvider implements IResourceProvider {
    @Autowired
    FhirContext fhirContext;

    @Autowired
    IEncounter encounterDao;

    private static final Logger log = LoggerFactory.getLogger(EncounterProvider.class);

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Encounter.class;
    }

    @Create
    public MethodOutcome createEncounter(HttpServletRequest theRequest, @ResourceParam Encounter obj) {

        log.debug("Create Encounter Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        Encounter mongoEncounter = null;
        try {
            mongoEncounter = encounterDao.create(fhirContext, obj);
            List<String> myString = new ArrayList<>();
            myString.add("Encounter/" + mongoEncounter.getIdElement());
            method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
                    "urn:uuid: " + mongoEncounter.getId(), IssueSeverity.INFORMATION, IssueType.INCOMPLETE, myString));
            method.setId(mongoEncounter.getIdElement());
            method.setResource(mongoEncounter);
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
    public Encounter readEncounter(HttpServletRequest request, @IdParam IdType internalId) {

        Encounter object = encounterDao.read(fhirContext, internalId);
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Encounter/" + internalId.getIdPart()),
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
    public Encounter readVread(HttpServletRequest request, @IdParam IdType idType) {
        Encounter object = new Encounter();
        if (idType.hasVersionIdPart()) {
            object = encounterDao.readOrVread(fhirContext, idType);
        } else {
            object = encounterDao.read(fhirContext, idType);
        }
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Encounter/" + idType.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }

    @Search
    public List<Resource> searchEncounter(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_APPOINTMENT) ReferenceParam appointment,
            @OptionalParam(name = ConstantKeys.SP_CLASS) TokenParam _class,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DIAGNOSIS) ReferenceParam diagnosis,
            @OptionalParam(name = ConstantKeys.SP_EPISODEOFCARE) ReferenceParam episodeofcare,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_INCOMINGREFERRAL) ReferenceParam incomingreferral,
            @OptionalParam(name = ConstantKeys.SP_LENGHTH) NumberParam length,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_LOCATION_PERIOD) DateRangeParam locationPeriod,
            @OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partOf,
            @OptionalParam(name = ConstantKeys.SP_PARTICIPANT) ReferenceParam participant,
            @OptionalParam(name = ConstantKeys.SP_PARTICIPANT_TYPE) TokenParam participantType,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
            @OptionalParam(name = ConstantKeys.SP_REASON) TokenParam reason,
            @OptionalParam(name = ConstantKeys.SP_SERVICE_PROVIDER) ReferenceParam serviceProvider,
            @OptionalParam(name = ConstantKeys.SP_SPECIAL_ARRANGEMENT) TokenParam specialArrangement,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
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
        if (count != null && count > 50) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("Total is not gre > 50"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.INFORMATIONAL);
        } else {
            if (theSort != null) {
                String sortParam = theSort.getParamName();
                List<Resource> results = encounterDao.search(fhirContext, active, appointment, _class, date, diagnosis,
                        episodeofcare, identifier, incomingreferral, length, location, locationPeriod, partOf,
                        participant, participantType, patient, practitioner, reason, serviceProvider,
                        specialArrangement, status, subject, type, resid, _lastUpdated, _tag, _profile, _query,
                        _security, _content, _page, sortParam, count);
                return results;
            }
            List<Resource> results = encounterDao.search(fhirContext, active, appointment, _class, date, diagnosis,
                    episodeofcare, identifier, incomingreferral, length, location, locationPeriod, partOf, participant,
                    participantType, patient, practitioner, reason, serviceProvider, specialArrangement, status,
                    subject, type, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, null,
                    count);
            return results;
        }
    }

    @Delete
    public Encounter deleteEncounter(HttpServletRequest request, @IdParam IdType internalId) {
        Encounter obj = encounterDao.remove(fhirContext, internalId);
        if (obj == null) {
            log.error("Couldn't delete Encounter" + internalId);
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("Encounter is not exit"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.INFORMATIONAL);
        }
        return obj;
    }

    @Update
    public MethodOutcome update(HttpServletRequest request, @IdParam IdType internalId,
            @ResourceParam Encounter encounter) {

        log.debug("Update Encounter Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);
        Encounter newEncounter = null;
        try {
            newEncounter = encounterDao.update(fhirContext, encounter, internalId);
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
                "urn:uuid: " + newEncounter.getId(), IssueSeverity.INFORMATION, IssueType.INCOMPLETE));
        method.setId(newEncounter.getIdElement());
        method.setResource(newEncounter);
        return method;
    }

    @Operation(name = "$total", idempotent = true)
    public Parameters getTotal(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_APPOINTMENT) ReferenceParam appointment,
            @OptionalParam(name = ConstantKeys.SP_CLASS) TokenParam _class,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DIAGNOSIS) ReferenceParam diagnosis,
            @OptionalParam(name = ConstantKeys.SP_EPISODEOFCARE) ReferenceParam episodeofcare,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_INCOMINGREFERRAL) ReferenceParam incomingreferral,
            @OptionalParam(name = ConstantKeys.SP_LENGHTH) NumberParam length,
            @OptionalParam(name = ConstantKeys.SP_LOCALTION) ReferenceParam location,
            @OptionalParam(name = ConstantKeys.SP_LOCATION_PERIOD) DateRangeParam locationPeriod,
            @OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partOf,
            @OptionalParam(name = ConstantKeys.SP_PARTICIPANT) ReferenceParam participant,
            @OptionalParam(name = ConstantKeys.SP_PARTICIPANT_TYPE) TokenParam participantType,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PRACTITIONER) ReferenceParam practitioner,
            @OptionalParam(name = ConstantKeys.SP_REASON) TokenParam reason,
            @OptionalParam(name = ConstantKeys.SP_SERVICE_PROVIDER) ReferenceParam serviceProvider,
            @OptionalParam(name = ConstantKeys.SP_SPECIAL_ARRANGEMENT) TokenParam specialArrangement,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_TYPE) TokenParam type,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content) {
        Parameters retVal = new Parameters();
        long total = encounterDao.getTotal(fhirContext, active, appointment, _class, date, diagnosis, episodeofcare,
                identifier, incomingreferral, length, location, locationPeriod, partOf, participant, participantType,
                patient, practitioner, reason, serviceProvider, specialArrangement, status, subject, type, resid,
                _lastUpdated, _tag, _profile, _query, _security, _content);
        retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
        return retVal;
    }
}
