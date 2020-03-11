package vn.ehealth.hl7.fhir.diagnostic.providers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
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
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.diagnostic.dao.IObservation;

@Component
public class ObservationProvider implements IResourceProvider {
    @Autowired
    FhirContext fhirContext;

    @Autowired
    IObservation observationDao;

    private static final Logger log = LoggerFactory.getLogger(ObservationProvider.class);

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Observation.class;
    }

    @Create
    public MethodOutcome createObservation(HttpServletRequest theRequest, @ResourceParam Observation obj) {

        log.debug("Create Observation Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        Observation mongoObservation = null;
        try {
            mongoObservation = observationDao.create(fhirContext, obj);
            List<String> myString = new ArrayList<>();
            myString.add("Observation/" + mongoObservation.getIdElement());
            method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
                    "urn:uuid: " + mongoObservation.getId(), IssueSeverity.INFORMATION, IssueType.INCOMPLETE,
                    myString));
            method.setId(mongoObservation.getIdElement());
            method.setResource(mongoObservation);
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
    public Observation readObservation(HttpServletRequest request, @IdParam IdType internalId) {

        Observation object = observationDao.read(fhirContext, internalId);
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Observation/" + internalId.getIdPart()),
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
    public Observation readVread(HttpServletRequest request, @IdParam IdType idType) {
        Observation object = new Observation();
        if (idType.hasVersionIdPart()) {
            object = observationDao.readOrVread(fhirContext, idType);
        } else {
            object = observationDao.read(fhirContext, idType);
        }
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Observation/" + idType.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }

    @Search
    public List<Resource> searchObservation(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam basedOn,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenOrListParam code,
            @OptionalParam(name = ConstantKeys.SP_COMBO_CODE) TokenParam comboCode,
            @OptionalParam(name = ConstantKeys.SP_COMBO_DATA_ABSENT_REASON) TokenParam comboDataAbsentReason,
            @OptionalParam(name = ConstantKeys.SP_COMBO_CODE_VALUE_CONCEPT) TokenParam comboValueConcept,
            @OptionalParam(name = ConstantKeys.SP_COMPONENT_CODE) TokenParam componentCode,
            @OptionalParam(name = ConstantKeys.SP_COMPONENT_DATA_ABSENT_REASON) TokenParam componentDataAbsentReason,
            @OptionalParam(name = ConstantKeys.SP_COMPONENT_VALUE_CONCEPT) TokenParam componentValueConcept,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam conetext,
            @OptionalParam(name = ConstantKeys.SP_DATA_ABSENT_REASON) TokenParam dataAbsentReason,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DEVICE) ReferenceParam device,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_METHOD) TokenParam method,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PERFORMER) ReferenceParam performer,
            @OptionalParam(name = ConstantKeys.SP_RELATED_TARGET) ReferenceParam relatedTarget,
            @OptionalParam(name = ConstantKeys.SP_RELATED_TYPE) TokenParam relatedType,
            @OptionalParam(name = ConstantKeys.SP_SPECIMEN) ReferenceParam specimen,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_VALUE_CONCEPT) TokenParam valueConcept,
            @OptionalParam(name = ConstantKeys.SP_VALUE_DATE) DateRangeParam valueDate,
            @OptionalParam(name = ConstantKeys.SP_VALUE_STRING) StringParam valueString,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Sort SortSpec theSort, @Count Integer count)
            throws OperationOutcomeException {
        if (count != null && count > 100) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("Total is not gre > 100"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.INFORMATIONAL);
        } else {
            var codeitem = new TokenParam(); 
            if (code !=null && code.getValuesAsQueryTokens().size() > 0)
                codeitem = code.getValuesAsQueryTokens().get(0);
            if (theSort != null) {
                String sortParam = theSort.getParamName();
                List<Resource> results = observationDao.search(fhirContext, active, basedOn, category, codeitem, comboCode,
                        comboDataAbsentReason, comboValueConcept, componentCode, componentDataAbsentReason,
                        componentValueConcept, conetext, dataAbsentReason, date, device, encounter, identifier, method,
                        patient, performer, relatedTarget, relatedType, specimen, status, subject, valueConcept,
                        valueDate, valueString, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page,
                        sortParam, count);
                return results;
            }
            List<Resource> results = observationDao.search(fhirContext, active, basedOn, category, codeitem, comboCode,
                    comboDataAbsentReason, comboValueConcept, componentCode, componentDataAbsentReason,
                    componentValueConcept, conetext, dataAbsentReason, date, device, encounter, identifier, method,
                    patient, performer, relatedTarget, relatedType, specimen, status, subject, valueConcept, valueDate,
                    valueString, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, null, count);
            return results;
        }
    }

    @Delete
    public Observation deleteObservation(HttpServletRequest request, @IdParam IdType internalId) {
        Observation obj = observationDao.remove(fhirContext, internalId);
        if (obj == null) {
            log.error("Couldn't delete Observation" + internalId);
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("Observation is not exit"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.INFORMATIONAL);
        }
        return obj;
    }

    @Update
    public MethodOutcome updateObservation(@IdParam IdType theId, @ResourceParam Observation patient) {

        log.debug("Update Observation Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);
        Observation newObservation = null;
        try {
            newObservation = observationDao.update(fhirContext, patient, theId);
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
                "urn:uuid: " + newObservation.getId(), IssueSeverity.INFORMATION, IssueType.INCOMPLETE));
        method.setId(newObservation.getIdElement());
        method.setResource(newObservation);
        return method;
    }

    @Operation(name = "$total", idempotent = true)
    public Parameters getTotal(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam basedOn,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = ConstantKeys.SP_COMBO_CODE) TokenParam comboCode,
            @OptionalParam(name = ConstantKeys.SP_COMBO_DATA_ABSENT_REASON) TokenParam comboDataAbsentReason,
            @OptionalParam(name = ConstantKeys.SP_COMBO_CODE_VALUE_CONCEPT) TokenParam comboValueConcept,
            @OptionalParam(name = ConstantKeys.SP_COMPONENT_CODE) TokenParam componentCode,
            @OptionalParam(name = ConstantKeys.SP_COMPONENT_DATA_ABSENT_REASON) TokenParam componentDataAbsentReason,
            @OptionalParam(name = ConstantKeys.SP_COMPONENT_VALUE_CONCEPT) TokenParam componentValueConcept,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam conetext,
            @OptionalParam(name = ConstantKeys.SP_DATA_ABSENT_REASON) TokenParam dataAbsentReason,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DEVICE) ReferenceParam device,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_METHOD) TokenParam method,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PERFORMER) ReferenceParam performer,
            @OptionalParam(name = ConstantKeys.SP_RELATED_TARGET) ReferenceParam relatedTarget,
            @OptionalParam(name = ConstantKeys.SP_RELATED_TYPE) TokenParam relatedType,
            @OptionalParam(name = ConstantKeys.SP_SPECIMEN) ReferenceParam specimen,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_VALUE_CONCEPT) TokenParam valueConcept,
            @OptionalParam(name = ConstantKeys.SP_VALUE_DATE) DateRangeParam valueDate,
            @OptionalParam(name = ConstantKeys.SP_VALUE_STRING) StringParam valueString,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content) {
        Parameters retVal = new Parameters();
        long total = observationDao.countMatchesAdvancedTotal(fhirContext, active, basedOn, category, code, comboCode,
                comboDataAbsentReason, comboValueConcept, componentCode, componentDataAbsentReason,
                componentValueConcept, conetext, dataAbsentReason, date, device, encounter, identifier, method, patient,
                performer, relatedTarget, relatedType, specimen, status, subject, valueConcept, valueDate, valueString,
                resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
        return retVal;
    }
}
