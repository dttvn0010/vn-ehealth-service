package vn.ehealth.hl7.fhir.schedule.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.schedule.dao.ISchedule;
import vn.ehealth.hl7.fhir.schedule.dao.transform.ScheduleEntityToFHIRSchedule;
import vn.ehealth.hl7.fhir.schedule.entity.AppointmentEntity;
import vn.ehealth.hl7.fhir.schedule.entity.ScheduleEntity;

@Repository
public class ScheduleDao implements ISchedule {

    @Autowired
    MongoOperations mongo;

    @Autowired
    ScheduleEntityToFHIRSchedule scheduleEntityToFHIRSchedule;

    @Override
    public Schedule create(FhirContext fhirContext, Schedule object) {
        ScheduleEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewScheduleEntity(object, version, null);
            // save ScheduleEntity database
            mongo.save(entity);
            return scheduleEntityToFHIRSchedule.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "schedule", key = "#idType")
    public Schedule update(FhirContext fhirContext, Schedule object, IdType idType) {
        ScheduleEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, ScheduleEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove ScheduleEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save ScheduleEntity
            int version = entityOld.version + 1;
            if (object != null) {
                ScheduleEntity entity = createNewScheduleEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return scheduleEntityToFHIRSchedule.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "schedule", key = "#idType")
    public Schedule read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ScheduleEntity entity = mongo.findOne(query, ScheduleEntity.class);
            if (entity != null) {
                return scheduleEntityToFHIRSchedule.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "schedule", key = "#idType")
    public Schedule remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ScheduleEntity entity = mongo.findOne(query, ScheduleEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return scheduleEntityToFHIRSchedule.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Schedule readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                ScheduleEntity entity = mongo.findOne(query, ScheduleEntity.class);
                if (entity != null) {
                    return scheduleEntityToFHIRSchedule.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
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
                count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
        query.with(pageableRequest);
        if (!sortParam.equals("")) {
            query.with(new Sort(Sort.Direction.ASC, sortParam));
        }
        List<ScheduleEntity> scheduleEntitys = mongo.find(query, ScheduleEntity.class);
        if (scheduleEntitys != null) {
            for (ScheduleEntity item : scheduleEntitys) {
                Schedule schedule = scheduleEntityToFHIRSchedule.transform(item);
                resources.add(schedule);
            }
        }
        return resources;
    }

    @Override
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

    private ScheduleEntity createNewScheduleEntity(Schedule obj, int version, String fhirId) {
        var ent = ScheduleEntity.fromSchedule(obj);
        DataConvertUtil.setMetaExt(obj, ent);
        if (fhirId != null && !fhirId.isEmpty()) {
            ent.fhir_id = (fhirId);
        } else {
            ent.fhir_id = (StringUtil.generateUID());
        }
        
        ent.active = (true);
        ent.version = (version);
        ent.resCreated = (new Date());
        return ent;
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
}
