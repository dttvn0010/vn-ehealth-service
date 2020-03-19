package vn.ehealth.hl7.fhir.schedule.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Schedule;
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
import vn.ehealth.hl7.fhir.schedule.entity.AppointmentEntity;
import vn.ehealth.hl7.fhir.schedule.entity.ScheduleEntity;

@Repository
public class ScheduleDao extends BaseDao<ScheduleEntity, Schedule> {

    @SuppressWarnings("deprecation")
    public List<Resource> search(FhirContext ctx, TokenParam active, TokenParam identifier, ReferenceParam actor,
            DateRangeParam date, TokenParam type, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
            String sortParam, Integer count) {

        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, identifier, actor, date, type, resid, _lastUpdated, _tag,
                _profile, _query, _security, _content);
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
        List<ScheduleEntity> scheduleEntitys = mongo.find(query, ScheduleEntity.class);
        if (scheduleEntitys != null) {
            for (ScheduleEntity item : scheduleEntitys) {
                Schedule schedule = transform(item);
                resources.add(schedule);
            }
        }
        return resources;
    }

    public long findMatchesAdvancedTotal(FhirContext ctx, TokenParam active, TokenParam identifier,
            ReferenceParam actor, DateRangeParam date, TokenParam type, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {

        Criteria criteria = null;
        criteria = setParamToCriteria(active, identifier, actor, date, type, resid, _lastUpdated, _tag, _profile,
                _query, _security, _content);
        long count = 0;
        if (criteria != null) {
            Query qry = Query.query(criteria);
            count = mongo.count(qry, AppointmentEntity.class);
        } else {
            Query query = new Query();
            count = mongo.count(query, AppointmentEntity.class);
        }
        return count;
    }

    private Criteria setParamToCriteria(TokenParam active, TokenParam identifier, ReferenceParam actor,
            DateRangeParam date, TokenParam type, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        Criteria criteria = null;
        criteria = Criteria.where("$where").is("1==1");
        // acive
        if (active != null) {
            criteria.and("active").is(active.getValue());
        }
        // default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        // actor
        if (actor != null) {
            if(actor.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("actor.reference").is(actor.getValue()),
                        Criteria.where("actor.display").is(actor.getValue()));
            }else {
                String[] ref= actor.getValue().split("\\|");
                criteria.and("actor.identifier.system").is(ref[0]).and("actor.identifier.value").is(ref[1]);
            }
        }
        // type
        if (type != null) {
            criteria.and("serviceType").is(type.getValue());
        }
        // date
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "planningHorizon", date);
        }
        return criteria;
    }

    @Override
    protected String getProfile() {
        return "Schedule-v1.0";
    }

    @Override
    protected ScheduleEntity fromFhir(Schedule obj) {
        return ScheduleEntity.fromSchedule(obj);
    }

    @Override
    protected Schedule toFhir(ScheduleEntity ent) {
        return ScheduleEntity.toSchedule(ent);
    }

    @Override
    protected Class<? extends BaseResource> getEntityClass() {
        return ScheduleEntity.class;
    }
}
