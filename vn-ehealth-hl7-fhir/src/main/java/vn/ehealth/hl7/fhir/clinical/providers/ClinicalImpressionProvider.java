package vn.ehealth.hl7.fhir.clinical.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.ClinicalImpression;
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
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.clinical.dao.IClinicalImpression;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

@Component
public class ClinicalImpressionProvider implements IResourceProvider {
    @Autowired
    FhirContext fhirContext;

    @Autowired
    IClinicalImpression clinicalImpressionDao;

    private static final Logger log = LoggerFactory.getLogger(ClinicalImpressionProvider.class);

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return ClinicalImpression.class;
    }

    @Create
    public MethodOutcome createClinicalImpression(HttpServletRequest theRequest,
            @ResourceParam ClinicalImpression obj) {

        log.debug("Create ClinicalImpression Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        ClinicalImpression mongoClinicalImpression = null;
        try {
            mongoClinicalImpression = clinicalImpressionDao.create(fhirContext, obj);
            List<String> myString = new ArrayList<>();
            myString.add("ClinicalImpression/" + mongoClinicalImpression.getIdElement());
            method.setOperationOutcome(OperationOutcomeFactory.createOperationOutcome("Create succsess",
                    "urn:uuid: " + mongoClinicalImpression.getId(), IssueSeverity.INFORMATION, IssueType.VALUE,
                    myString));
            method.setId(mongoClinicalImpression.getIdElement());
            method.setResource(mongoClinicalImpression);
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
    public ClinicalImpression readClinicalImpression(HttpServletRequest request, @IdParam IdType internalId) {
        ClinicalImpression object = clinicalImpressionDao.read(fhirContext, internalId);
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No ClinicalImpression/" + internalId.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }

    @Delete
    public ClinicalImpression deleteClinicalImpression(HttpServletRequest request, @IdParam IdType internalId) {
        ClinicalImpression object = clinicalImpressionDao.remove(fhirContext, internalId);
        if (object == null) {
            log.error("Couldn't delete ClinicalImpression" + internalId);
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("ClinicalImpression is not exit"),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }

    @Update
    public MethodOutcome updateClinicalImpression(@IdParam IdType theId, @ResourceParam ClinicalImpression patient) {

        log.debug("Update ClinicalImpression Provider called");

        MethodOutcome method = new MethodOutcome();
        method.setCreated(false);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);
        ClinicalImpression newClinicalImpression = null;
        try {
            newClinicalImpression = clinicalImpressionDao.update(fhirContext, patient, theId);
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
                "urn:uuid: " + newClinicalImpression.getId(), IssueSeverity.INFORMATION, IssueType.VALUE));
        method.setId(newClinicalImpression.getIdElement());
        method.setResource(newClinicalImpression);
        return method;
    }

    /**
     * @author sonvt
     * @param request
     * @param idType
     * @return read object version
     */
    @Read(version = true)
    public ClinicalImpression readVread(HttpServletRequest request, @IdParam IdType idType) {
        ClinicalImpression object = new ClinicalImpression();
        if (idType.hasVersionIdPart()) {
            object = clinicalImpressionDao.readOrVread(fhirContext, idType);
        } else {
            object = clinicalImpressionDao.read(fhirContext, idType);
        }
        if (object == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No ClinicalImpression/" + idType.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }
        return object;
    }

    @Search
    public IBundleProvider searchClinicalImpression(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_ACTION) ReferenceParam action,
            @OptionalParam(name = ConstantKeys.SP_ASSESSOR) ReferenceParam assessor,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_FINDING_CODE) TokenParam findingCode,
            @OptionalParam(name = ConstantKeys.SP_FINDING_REF) ReferenceParam findingRef,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_INVESTIGATION) ReferenceParam investigation,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PREVIOUS) ReferenceParam previous,
            @OptionalParam(name = ConstantKeys.SP_PROBLEM) ReferenceParam problem,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
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
                results = clinicalImpressionDao.search(fhirContext, active, action, assessor, context,
                        date, findingCode, findingRef, identifier, investigation, patient, previous, problem, status,
                        subject, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam,
                        count);
            } else 
            	results = clinicalImpressionDao.search(fhirContext, active, action, assessor, context, date,
                    findingCode, findingRef, identifier, investigation, patient, previous, problem, status, subject,
                    resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, null, count);
            final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);
            
            return new IBundleProvider() {
                
                @Override
                public Integer size() {
                	return Integer.parseInt(String.valueOf(
                			clinicalImpressionDao.countMatchesAdvancedTotal(fhirContext, active, action, assessor, context,
                	                date, findingCode, findingRef, identifier, investigation, patient, previous, problem, status, subject,
                	                resid, _lastUpdated, _tag, _profile, _query, _security, _content)));
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
            @OptionalParam(name = ConstantKeys.SP_ACTION) ReferenceParam action,
            @OptionalParam(name = ConstantKeys.SP_ASSESSOR) ReferenceParam assessor,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_FINDING_CODE) TokenParam findingCode,
            @OptionalParam(name = ConstantKeys.SP_FINDING_REF) ReferenceParam findingRef,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_INVESTIGATION) ReferenceParam investigation,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PREVIOUS) ReferenceParam previous,
            @OptionalParam(name = ConstantKeys.SP_PROBLEM) ReferenceParam problem,
            @OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
            @OptionalParam(name = ConstantKeys.SP_SUBJECT) ReferenceParam subject,
            @OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
            @OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
            @OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
            @OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
            @OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
            @OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
            @OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content) {
        Parameters retVal = new Parameters();
        long total = clinicalImpressionDao.countMatchesAdvancedTotal(fhirContext, active, action, assessor, context,
                date, findingCode, findingRef, identifier, investigation, patient, previous, problem, status, subject,
                resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
        return retVal;
    }
}
