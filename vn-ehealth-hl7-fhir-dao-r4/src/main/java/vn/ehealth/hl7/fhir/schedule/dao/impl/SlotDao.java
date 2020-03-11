package vn.ehealth.hl7.fhir.schedule.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Slot;
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
import vn.ehealth.hl7.fhir.schedule.dao.ISlot;
import vn.ehealth.hl7.fhir.schedule.dao.transform.SlotEntityToFHIRSlot;
import vn.ehealth.hl7.fhir.schedule.entity.SlotEntity;

@Repository
public class SlotDao implements ISlot {

    @Autowired
    MongoOperations mongo;

    @Autowired
    SlotEntityToFHIRSlot slotEntityToFHIRSlot;

    @Override
    public Slot create(FhirContext fhirContext, Slot object) {
        SlotEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewSlotEntity(object, version, null);
            // save SlotEntity database
            mongo.save(entity);
            return slotEntityToFHIRSlot.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "slot", key = "#idType")
    public Slot update(FhirContext fhirContext, Slot object, IdType idType) {
        SlotEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, SlotEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove SlotEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save SlotEntity
            int version = entityOld.version + 1;
            if (object != null) {
                SlotEntity entity = createNewSlotEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return slotEntityToFHIRSlot.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "slot", key = "#idType")
    public Slot read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            SlotEntity entity = mongo.findOne(query, SlotEntity.class);
            if (entity != null) {
                return slotEntityToFHIRSlot.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "slot", key = "#idType")
    public Slot remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            SlotEntity entity = mongo.findOne(query, SlotEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return slotEntityToFHIRSlot.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Slot readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                SlotEntity entity = mongo.findOne(query, SlotEntity.class);
                if (entity != null) {
                    return slotEntityToFHIRSlot.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
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
                Slot slot = slotEntityToFHIRSlot.transform(item);
                resources.add(slot);
            }
        }
        return resources;
    }

    @Override
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

    private SlotEntity createNewSlotEntity(Slot obj, int version, String fhirId) {
        var ent = SlotEntity.fromSlot(obj);
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
}
