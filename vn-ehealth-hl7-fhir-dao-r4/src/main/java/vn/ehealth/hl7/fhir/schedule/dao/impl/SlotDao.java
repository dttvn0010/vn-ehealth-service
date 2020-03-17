package vn.ehealth.hl7.fhir.schedule.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Slot;
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
import vn.ehealth.hl7.fhir.schedule.entity.SlotEntity;

@Repository
public class SlotDao extends BaseDao<SlotEntity, Slot> {

    @SuppressWarnings("deprecation")
    public List<Resource> search(FhirContext fhirContext, TokenParam active, TokenParam status, TokenParam identifier,
            ReferenceParam schedule, DateRangeParam date, TokenParam slotType, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content, StringParam _page, String sortParam, Integer count) {

        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(fhirContext, active, status, identifier, schedule, date, slotType, resid,
                _lastUpdated, _tag, _profile, _query, _security, _content);
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
        List<SlotEntity> slotEntitys = mongo.find(query, SlotEntity.class);
        if (slotEntitys != null) {
            for (SlotEntity item : slotEntitys) {
                Slot slot = transform(item);
                resources.add(slot);
            }
        }
        return resources;
    }

    public long findMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, TokenParam status,
            TokenParam identifier, ReferenceParam schedule, DateRangeParam date, TokenParam slotType, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(fhirContext, active, status, identifier, schedule, date, slotType, resid,
                _lastUpdated, _tag, _profile, _query, _security, _content);

        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, SlotEntity.class);
        return total;
    }

    private Criteria setParamToCriteria(FhirContext fhirContext, TokenParam active, TokenParam status,
            TokenParam identifier, ReferenceParam schedule, DateRangeParam date, TokenParam slotType, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        Criteria criteria = null;
        criteria = Criteria.where("$where").is("1==1");
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active.getValue());
        } else {
            criteria = Criteria.where("active").is(true);
        }
        if (status != null) {
            criteria.and("status").is(status.getValue());
        }
        // default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        if (schedule != null) {
            criteria.and("schedule.reference.myStringValue").is(schedule.getValue());
        }
        if (slotType != null) {
            criteria.and("serviceType").is(slotType.getValue());
        }
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "start", date);
        }
        return criteria;
    }

    @Override
    protected String getProfile() {
        return "Slot-v1.0";
    }

    @Override
    protected SlotEntity fromFhir(Slot obj) {
        return SlotEntity.fromSlot(obj);
    }

    @Override
    protected Slot toFhir(SlotEntity ent) {
        return SlotEntity.toSlot(ent);
    }

    @Override
    protected Class<? extends BaseResource> getEntityClass() {
        return SlotEntity.class;
    }
}
