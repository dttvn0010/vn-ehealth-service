package vn.ehealth.hl7.fhir.clinical.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.providers.BaseController;
import vn.ehealth.hl7.fhir.clinical.dao.impl.CarePlanDao;
import vn.ehealth.hl7.fhir.clinical.entity.CarePlanEntity;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeException;
import vn.ehealth.hl7.fhir.core.common.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.dao.BaseDao;

@Component
public class CarePlanProvider extends BaseController<CarePlanEntity, CarePlan> implements IResourceProvider {
    
    @Autowired
    CarePlanDao carePlanDao;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return CarePlan.class;
    }

    @Search
    public IBundleProvider searchCarePlan(HttpServletRequest request,
            @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
            @OptionalParam(name = ConstantKeys.SP_ACTIVITY_CODE) TokenParam activityCode,
            @OptionalParam(name = ConstantKeys.SP_ACTIVITY_DATE) DateRangeParam activityDate,
            @OptionalParam(name = ConstantKeys.SP_ACTIVITY_REFERENCE) ReferenceParam activityReference,
            @OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam basedOn,
            @OptionalParam(name = ConstantKeys.SP_CARE_TEAM) ReferenceParam careTeam,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CONDITION) ReferenceParam condition,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DEFINITION) ReferenceParam definition,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_GOAL) ReferenceParam goal,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_INTENT) TokenParam intent,
            @OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partOf,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PERFORMER) ReferenceParam performer,
            @OptionalParam(name = ConstantKeys.SP_REPLACES) ReferenceParam replaces,
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
                results = carePlanDao.search(fhirContext, active, activityCode, activityDate,
                        activityReference, basedOn, careTeam, category, condition, context, date, definition, encounter,
                        goal, identifier, intent, partOf, patient, performer, replaces, status, subject, resid,
                        _lastUpdated, _tag, _profile, _query, _security, _content, _page, sortParam, count);
            } else 
            	results = carePlanDao.search(fhirContext, active, activityCode, activityDate,
                    activityReference, basedOn, careTeam, category, condition, context, date, definition, encounter,
                    goal, identifier, intent, partOf, patient, performer, replaces, status, subject, resid,
                    _lastUpdated, _tag, _profile, _query, _security, _content, _page, null, count);
            final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);
            
            return new IBundleProvider() {
                
                @Override
                public Integer size() {
                	return Integer.parseInt(String.valueOf(
                			carePlanDao.countMatchesAdvancedTotal(
                					fhirContext, active, activityCode, activityDate,
                	                activityReference, basedOn, careTeam, category, condition, context, date, definition, encounter, goal,
                	                identifier, intent, partOf, patient, performer, replaces, status, subject, resid, _lastUpdated, _tag,
                	                _profile, _query, _security, _content)));
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
            @OptionalParam(name = ConstantKeys.SP_ACTIVITY_CODE) TokenParam activityCode,
            @OptionalParam(name = ConstantKeys.SP_ACTIVITY_DATE) DateRangeParam activityDate,
            @OptionalParam(name = ConstantKeys.SP_ACTIVITY_REFERENCE) ReferenceParam activityReference,
            @OptionalParam(name = ConstantKeys.SP_BASED_ON) ReferenceParam basedOn,
            @OptionalParam(name = ConstantKeys.SP_CARE_TEAM) ReferenceParam careTeam,
            @OptionalParam(name = ConstantKeys.SP_CATEGORY) TokenParam category,
            @OptionalParam(name = ConstantKeys.SP_CONDITION) ReferenceParam condition,
            @OptionalParam(name = ConstantKeys.SP_CONTEXT) ReferenceParam context,
            @OptionalParam(name = ConstantKeys.SP_DATE) DateRangeParam date,
            @OptionalParam(name = ConstantKeys.SP_DEFINITION) ReferenceParam definition,
            @OptionalParam(name = ConstantKeys.SP_ENCOUNTER) ReferenceParam encounter,
            @OptionalParam(name = ConstantKeys.SP_GOAL) ReferenceParam goal,
            @OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = ConstantKeys.SP_INTENT) TokenParam intent,
            @OptionalParam(name = ConstantKeys.SP_PARTOF) ReferenceParam partOf,
            @OptionalParam(name = ConstantKeys.SP_PATIENT) ReferenceParam patient,
            @OptionalParam(name = ConstantKeys.SP_PERFORMER) ReferenceParam performer,
            @OptionalParam(name = ConstantKeys.SP_REPLACES) ReferenceParam replaces,
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
        long total = carePlanDao.countMatchesAdvancedTotal(fhirContext, active, activityCode, activityDate,
                activityReference, basedOn, careTeam, category, condition, context, date, definition, encounter, goal,
                identifier, intent, partOf, patient, performer, replaces, status, subject, resid, _lastUpdated, _tag,
                _profile, _query, _security, _content);
        retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
        return retVal;
    }

    @Override
    protected BaseDao<CarePlanEntity, CarePlan> getDao() {
        return carePlanDao;
    }
}
