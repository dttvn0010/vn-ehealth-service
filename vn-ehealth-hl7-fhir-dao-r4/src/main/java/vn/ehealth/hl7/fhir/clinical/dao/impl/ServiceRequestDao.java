package vn.ehealth.hl7.fhir.clinical.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.clinical.entity.ProcedureEntity;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.diagnostic.entity.ServiceRequestEntity;

@Repository
public class ServiceRequestDao extends BaseDao<ServiceRequestEntity, ServiceRequest>{

	@SuppressWarnings("deprecation")
    public List<Resource> search(FhirContext fhirContext, TokenParam active, ReferenceParam bassedOn,
			TokenParam category, TokenParam code, ReferenceParam context, DateRangeParam date,
			ReferenceParam definition, ReferenceParam encounter, TokenParam identifier, ReferenceParam location,
			ReferenceParam partOf, ReferenceParam patient, ReferenceParam performer, TokenParam status,
			ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content, StringParam _page, String sortParam,
			Integer count) {
		List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, bassedOn, category, code, context, date, definition, encounter,
                identifier, location, partOf, patient, performer, status, subject, resid, _lastUpdated, _tag, _profile,
                _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        Pageable pageableRequest;
        pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
        query.with(pageableRequest);
        if (sortParam != null && !sortParam.equals("")) {
            query.with(new Sort(Sort.Direction.ASC, sortParam));
        }
        List<ServiceRequestEntity> entitys = mongo.find(query, ServiceRequestEntity.class);
        if (entitys != null) {
            for (ServiceRequestEntity item : entitys) {
            	ServiceRequest obj = transform(item);
                resources.add(obj);
            }
        }
        return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, ReferenceParam bassedOn,
			TokenParam category, TokenParam code, ReferenceParam context, DateRangeParam date,
			ReferenceParam definition, ReferenceParam encounter, TokenParam identifier, ReferenceParam location,
			ReferenceParam partOf, ReferenceParam patient, ReferenceParam performer, TokenParam status,
			ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content) {
		long total = 0;
        Criteria criteria = setParamToCriteria(active, bassedOn, category, code, context, date, definition, encounter,
                identifier, location, partOf, patient, performer, status, subject, resid, _lastUpdated, _tag, _profile,
                _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, ProcedureEntity.class);
        return total;
	}
	
	private Criteria setParamToCriteria(TokenParam active, ReferenceParam bassedOn, TokenParam category,
            TokenParam code, ReferenceParam context, DateRangeParam date, ReferenceParam definition,
            ReferenceParam encounter, TokenParam identifier, ReferenceParam location, ReferenceParam partOf,
            ReferenceParam patient, ReferenceParam performer, TokenParam status, ReferenceParam subject,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content) {
        Criteria criteria = null;
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active);
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // set param default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        // based-on
        if (bassedOn != null) {
            if(bassedOn.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("bassedOn.reference").is(bassedOn.getValue()),
                        Criteria.where("bassedOn.display").is(bassedOn.getValue()));
            }else {
                String[] ref= bassedOn.getValue().split("\\|");
                criteria.and("bassedOn.identifier.system").is(ref[0]).and("bassedOn.identifier.value").is(ref[1]);
            }
        }
        // category
        if (category != null) {
            criteria.and("category.coding.code.myStringValue").is(category.getValue());
        }
        // code
        if (code != null) {
            criteria.and("code.coding.code.myStringValue").is(code.getValue());
        }
		/*
		 * // context if (context != null) { if(context.getValue().indexOf("|")==-1) {
		 * criteria.orOperator(Criteria.where("context.reference").is(context.getValue()
		 * ), Criteria.where("context.display").is(context.getValue())); }else {
		 * String[] ref= context.getValue().split("\\|");
		 * criteria.and("context.identifier.system").is(ref[0]).and(
		 * "context.identifier.value").is(ref[1]); } }
		 */
        // date
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "performed", date);
        }
        // definition
        if (definition != null) {
            if(definition.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("definition.reference").is(definition.getValue()),
                        Criteria.where("definition.display").is(definition.getValue()));
            }else {
                String[] ref= definition.getValue().split("\\|");
                criteria.and("definition.identifier.system").is(ref[0]).and("definition.identifier.value").is(ref[1]);
            }
        }
        // encounter
        if (encounter != null) {
            if(encounter.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("encounter.reference").is(encounter.getValue()),
                        Criteria.where("encounter.display").is(encounter.getValue()));
            }else {
                String[] ref= encounter.getValue().split("\\|");
                criteria.and("encounter.identifier.system").is(ref[0]).and("encounter.identifier.value").is(ref[1]);
            }
        }
        // location
        if (location != null) {
            if(location.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("location.location.reference").is(location.getValue()),
                        Criteria.where("location.location.display").is(location.getValue()));
            }else {
                String[] ref= location.getValue().split("\\|");
                criteria.and("location.location.identifier.system").is(ref[0]).and("location.location.identifier.value").is(ref[1]);
            }
        }
        // part-of
        if (partOf != null) {
            if(partOf.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("partOf.reference").is(partOf.getValue()),
                        Criteria.where("partOf.display").is(partOf.getValue()));
            }else {
                String[] ref= partOf.getValue().split("\\|");
                criteria.and("partOf.identifier.system").is(ref[0]).and("partOf.identifier.value").is(ref[1]);
            }
        }
        // patient
        if (patient != null) {
            if(patient.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("subject.reference").is(patient.getValue()),
                        Criteria.where("subject.display").is(patient.getValue()));
            }else {
                String[] ref= patient.getValue().split("\\|");
                criteria.and("subject.identifier.system").is(ref[0]).and("subject.identifier.value").is(ref[1]);
            }
        }
        // performer
        if (performer != null) {
            if(performer.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("performer.reference").is(performer.getValue()),
                        Criteria.where("performer.display").is(performer.getValue()));
            }else {
                String[] ref= performer.getValue().split("\\|");
                criteria.and("performer.identifier.system").is(ref[0]).and("performer.identifier.value").is(ref[1]);
            }
        }
        // status
        if (status != null) {
            criteria.and("status").is(status.getValue());
        }
        // subject
        if (subject != null) {
            if(subject.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("subject.reference").is(subject.getValue()),
                        Criteria.where("subject.display").is(subject.getValue()));
            }else {
                String[] ref= subject.getValue().split("\\|");
                criteria.and("subject.identifier.system").is(ref[0]).and("subject.identifier.value").is(ref[1]);
            }
        }
        return criteria;
    }
	
	public ServiceRequest getByReport(IdType reportIdType) {
	    if(reportIdType != null && reportIdType.hasIdPart()) {
	        return findOne(Map.of("basedOn.reference", reportIdType.getIdPart()));	        
	    }
	    return null;
	}

    @Override
    protected String getProfile() {
        return "ServiceRequest-v1.0";
    }

    @Override
    protected ServiceRequestEntity fromFhir(ServiceRequest obj) {
        return ServiceRequestEntity.fromServiceRequest(obj);
    }

    @Override
    protected ServiceRequest toFhir(ServiceRequestEntity ent) {
        return ServiceRequestEntity.toServiceRequest(ent);
    }

    @Override
    protected Class<? extends BaseResource> getEntityClass() {
        return ServiceRequestEntity.class;
    }
}
