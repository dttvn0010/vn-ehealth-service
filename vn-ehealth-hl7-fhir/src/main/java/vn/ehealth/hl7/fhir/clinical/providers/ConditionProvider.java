package vn.ehealth.hl7.fhir.clinical.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Condition;
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
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.clinical.dao.ICondition;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

@Component
public class ConditionProvider implements IResourceProvider {
    @Autowired
    FhirContext fhirContext;

    @Autowired
    ICondition conditionDao;

    private static final Logger log = LoggerFactory.getLogger(ConditionProvider.class);

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Condition.class;
    }

    @Create
    public MethodOutcome createCondition(HttpServletRequest theRequest, @ResourceParam Condition obj) {

        log.debug("Create Condition Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        Condition mongoCondition = null;
        try {
            mongoCondition = conditionDao.create(fhirContext, obj);
            List<String> myString = new ArrayList<>();
            myString.add("Condition/" + mongoCondition.getIdElement());
            method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
                    "urn:uuid: " + mongoCondition.getId(), IssueSeverity.INFORMATION, IssueType.VALUE, myString));
            method.setId(mongoCondition.getIdElement());
            method.setResource(mongoCondition);
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
    public Condition readCondition(HttpServletRequest request, @IdParam IdType internalId) {

        Condition object = conditionDao.read(fhirContext, internalId);
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Condition/" + internalId.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }

    @Delete
    public Condition deleteCondition(HttpServletRequest request, @IdParam IdType internalId) {
        Condition object = conditionDao.remove(fhirContext, internalId);
        if (object == null) {
            log.error("Couldn't delete Condition" + internalId);
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("Condition is not exit"), OperationOutcome.IssueSeverity.ERROR,
                    OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }

    @Update
    public MethodOutcome updateCondition(@IdParam IdType theId, @ResourceParam Condition patient) {

        log.debug("Update Condition Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(false);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);
        Condition newCondition = null;
        try {
            newCondition = conditionDao.update(fhirContext, patient, theId);
        } catch (Exception ex) {
            if (ex instanceof OperationOutcomeException) {
                OperationOutcomeException outcomeException = (OperationOutcomeException) ex;
                method.setOperationOutcome(outcomeException.getOutcome());
                //method.setCreated(false);
            } else {
                log.error(ex.getMessage());
                //method.setCreated(false);
                method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome(ex.getMessage()));
            }
        }
        method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Update succsess",
                "urn:uuid: " + newCondition.getId(), IssueSeverity.INFORMATION, IssueType.VALUE));
        method.setId(newCondition.getIdElement());
        method.setResource(newCondition);
        return method;
    }

    /**
     * @author sonvt
     * @param request
     * @param idType
     * @return read object version
     */
    @Read(version = true)
    public Condition readVread(HttpServletRequest request, @IdParam IdType idType) {
        Condition object = new Condition();
        if (idType.hasVersionIdPart()) {
            object = conditionDao.readOrVread(fhirContext, idType);
        } else {
            object = conditionDao.read(fhirContext, idType);
        }
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Condition/" + idType.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }

    @Search
    public IBundleProvider searchCondition(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_AGE) QuantityParam abatementAge,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_BOOLEAN) TokenParam abatementBoolean,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_DATE) DateRangeParam abatementDate,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_STRING) TokenParam abatementString,
            @OptionalParam(name = ConstantKeys.SP_ASSERTED_DATE) DateRangeParam assertedDate,
            @OptionalParam(name = ConstantKeys.SP_ASSERTER) ReferenceParam asserter,
            @OptionalParam(name = ConstantKeys.SP_BODY_SITE) TokenParam bodySite,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CLINICAL_STATUS) TokenParam clinicalStatus,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_EVIDENCE) TokenParam evidence,
            @OptionalParam(name = ConstantKeys.SP_EVIDENCE_DETAIL) ReferenceParam evidenceDetail,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_ONSET_AGE) QuantityParam onsetAge,
            @OptionalParam(name = ConstantKeys.SP_ONSET_DATE) DateRangeParam onseDate,
            @OptionalParam(name = ConstantKeys.SP_ONSET_INFO) StringParam onsetInfo,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_SEVERITY) TokenParam severity,
            @OptionalParam(name = ConstantKeys.SP_STAGE) TokenParam stage,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_VERIFICATION_STATUS) TokenParam verificationStatus,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
            @OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, @Sort SortSpec theSort, @Count Integer count)
            throws OperationOutcomeException {
    	if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
            		new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
        } else {
        	List<Resource> results = new ArrayList<Resource>();
            if (theSort != null) {
                String sortParam = theSort.getParamName();
                results = conditionDao.search(fhirContext, active, abatementAge, abatementBoolean,
                        abatementDate, abatementString, assertedDate, asserter, bodySite, category, clinicalStatus,
                        code, context, encounter, evidence, evidenceDetail, identifier, onsetAge, onseDate, onsetInfo,
                        patient, severity, stage, subject, verificationStatus, resid, _lastUpdated, _tag, _profile,
                        _query, _security, _content, _page, sortParam, count);
                //return results;
            } else 
            	results = conditionDao.search(fhirContext, active, abatementAge, abatementBoolean,
                    abatementDate, abatementString, assertedDate, asserter, bodySite, category, clinicalStatus, code,
                    context, encounter, evidence, evidenceDetail, identifier, onsetAge, onseDate, onsetInfo, patient,
                    severity, stage, subject, verificationStatus, resid, _lastUpdated, _tag, _profile, _query,
                    _security, _content, _page, null, count);
            
            final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);
            
            return new IBundleProvider() {
                
                @Override
                public Integer size() {
                	return Integer.parseInt(String.valueOf(
                			conditionDao.countMatchesAdvancedTotal(fhirContext, active, abatementAge, abatementBoolean,
                	                abatementDate, abatementString, assertedDate, asserter, bodySite, category, clinicalStatus, code,
                	                context, encounter, evidence, evidenceDetail, identifier, onsetAge, onseDate, onsetInfo, patient,
                	                severity, stage, subject, verificationStatus, resid, _lastUpdated, _tag, _profile, _query, _security,
                	                _content)));
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
                    // TODO Auto-generated method stub
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

    @Operation(name = "$total", idempotent = true)
    public Parameters getTotal(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_AGE) QuantityParam abatementAge,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_BOOLEAN) TokenParam abatementBoolean,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_DATE) DateRangeParam abatementDate,
            @OptionalParam(name = ConstantKeys.SP_ABATEMENT_STRING) TokenParam abatementString,
            @OptionalParam(name = ConstantKeys.SP_ASSERTED_DATE) DateRangeParam assertedDate,
            @OptionalParam(name = ConstantKeys.SP_ASSERTER) ReferenceParam asserter,
            @OptionalParam(name = ConstantKeys.SP_BODY_SITE) TokenParam bodySite,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CLINICAL_STATUS) TokenParam clinicalStatus,
            @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_EVIDENCE) TokenParam evidence,
            @OptionalParam(name = ConstantKeys.SP_EVIDENCE_DETAIL) ReferenceParam evidenceDetail,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_ONSET_AGE) QuantityParam onsetAge,
            @OptionalParam(name = ConstantKeys.SP_ONSET_DATE) DateRangeParam onseDate,
            @OptionalParam(name = ConstantKeys.SP_ONSET_INFO) StringParam onsetInfo,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_SEVERITY) TokenParam severity,
            @OptionalParam(name = ConstantKeys.SP_STAGE) TokenParam stage,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_VERIFICATION_STATUS) TokenParam verificationStatus,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
        Parameters retVal = new Parameters();
        long total = conditionDao.countMatchesAdvancedTotal(fhirContext, active, abatementAge, abatementBoolean,
                abatementDate, abatementString, assertedDate, asserter, bodySite, category, clinicalStatus, code,
                context, encounter, evidence, evidenceDetail, identifier, onsetAge, onseDate, onsetInfo, patient,
                severity, stage, subject, verificationStatus, resid, _lastUpdated, _tag, _profile, _query, _security,
                _content);
        retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
        return retVal;
    }
}
