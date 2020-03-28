package vn.ehealth.hl7.fhir.ehr.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Resource;
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
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.ehr.entity.CareTeamEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Repository
public class CareTeamDao extends BaseDao<CareTeamEntity, CareTeam> {
  
    @SuppressWarnings("deprecation")
    public List<Resource> search(FhirContext fhirContext, TokenParam active, TokenParam category,
            ReferenceParam context, DateRangeParam date, ReferenceParam encounter, TokenParam identifier,
            ReferenceParam participant, ReferenceParam patient, TokenParam status, ReferenceParam subject,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, category, context, date, encounter, identifier, participant,
                patient, status, subject, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        Pageable pageableRequest;
        pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                count != null ? count : ConstantKeys.DEFAULT_PAGE_SIZE);
        query.with(pageableRequest);
		if (sortParam != null && !sortParam.equals("")) {
			query.with(new Sort(Sort.Direction.DESC, sortParam));
		} else {
			query.with(new Sort(Sort.Direction.DESC, "resUpdated"));
			query.with(new Sort(Sort.Direction.DESC, "resCreated"));
		}
        List<CareTeamEntity> careTeamEntitys = mongo.find(query, CareTeamEntity.class);
        if (careTeamEntitys != null) {
            for (CareTeamEntity item : careTeamEntitys) {
                CareTeam careTeam = transform(item);
                resources.add(careTeam);
            }
        }
        return resources;
    }

    public long getTotal(FhirContext fhirContext, TokenParam active, TokenParam category, ReferenceParam context,
            DateRangeParam date, ReferenceParam encounter, TokenParam identifier, ReferenceParam participant,
            ReferenceParam patient, TokenParam status, ReferenceParam subject, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, category, context, date, encounter, identifier, participant,
                patient, status, subject, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, CareTeamEntity.class);
        return total;
    }

    private Criteria setParamToCriteria(TokenParam active, TokenParam category, ReferenceParam context,
            DateRangeParam date, ReferenceParam encounter, TokenParam identifier, ReferenceParam participant,
            ReferenceParam patient, TokenParam status, ReferenceParam subject, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        Criteria criteria = null;
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active);
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // param default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        // category
        if (category != null) {
            criteria.and("category.coding.code.myStringValue").is(category.getValue());
        }
        // date
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "period", date);
        }
        // participant
        if (participant != null) {
            if(participant.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("participant.member.reference").is(participant.getValue()),
                        Criteria.where("participant.member.display").is(participant.getValue()));
            }else {
                String[] ref= participant.getValue().split("\\|");
                criteria.and("participant.member.identifier.system").is(ref[0]).and("participant.member.identifier.value").is(ref[1]);
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
        // encounter
        if (encounter != null) {
            if(encounter.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("context.reference").is(encounter.getValue()),
                        Criteria.where("context.display").is(encounter.getValue()));
            }else {
                String[] ref= encounter.getValue().split("\\|");
                criteria.and("context.identifier.system").is(ref[0]).and("context.identifier.value").is(ref[1]);
            }
        }
        return criteria;
    }

    @Override
    protected String getProfile() {
        return "CareTeam-v1.0";
    }

    @Override
    protected Class<? extends DomainResource> getResourceClass() {
        return CareTeam.class;
    }

    @Override
    protected Class<? extends BaseResource> getEntityClass() {
        return CareTeamEntity.class;
    }
}
