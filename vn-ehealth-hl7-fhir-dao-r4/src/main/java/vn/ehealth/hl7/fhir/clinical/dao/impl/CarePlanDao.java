package vn.ehealth.hl7.fhir.clinical.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
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
import vn.ehealth.hl7.fhir.clinical.dao.ICarePlan;
import vn.ehealth.hl7.fhir.clinical.dao.transform.CarePlanEntityToFHIRCarePlan;
import vn.ehealth.hl7.fhir.clinical.entity.CarePlanEntity;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;

@Repository
public class CarePlanDao implements ICarePlan {

    @Autowired
    MongoOperations mongo;

    @Autowired
    CarePlanEntityToFHIRCarePlan carePlanEntityToFHIRCarePlan;

    @Override
    public CarePlan create(FhirContext fhirContext, CarePlan object) {
        CarePlanEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewCarePlanEntity(object, version, null);
            // save CarePlanEntity database
            mongo.save(entity);
            return carePlanEntityToFHIRCarePlan.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "carePlan", key = "#idType")
    public CarePlan update(FhirContext fhirContext, CarePlan object, IdType idType) {
        CarePlanEntity entityOld = null;
        String fhirId = "";
        if (idType != null) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, CarePlanEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove CarePlanEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save CarePlanEntity
            int version = entityOld.version + 1;
            if (object != null) {
                CarePlanEntity entity = createNewCarePlanEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return carePlanEntityToFHIRCarePlan.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "carePlan", key = "#idType")
    public CarePlan read(FhirContext fhirContext, IdType idType) {
        if (idType != null) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            CarePlanEntity entity = mongo.findOne(query, CarePlanEntity.class);
            if (entity != null) {
                return carePlanEntityToFHIRCarePlan.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "carePlan", key = "#idType")
    public CarePlan remove(FhirContext fhirContext, IdType idType) {
        if (idType != null) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            CarePlanEntity entity = mongo.findOne(query, CarePlanEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return carePlanEntityToFHIRCarePlan.transform(entity);
            }
        }
        return null;
    }

    @Override
    public CarePlan readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                CarePlanEntity entity = mongo.findOne(query, CarePlanEntity.class);
                if (entity != null) {
                    return carePlanEntityToFHIRCarePlan.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, TokenParam activityCode,
            DateRangeParam activityDate, ReferenceParam activityReference, ReferenceParam basedOn,
            ReferenceParam careTeam, TokenParam category, ReferenceParam condition, ReferenceParam context,
            DateRangeParam date, ReferenceParam definition, ReferenceParam encounter, ReferenceParam goal,
            TokenParam identifier, TokenParam intent, ReferenceParam partOf, ReferenceParam patient,
            ReferenceParam performer, ReferenceParam replaces, TokenParam status, ReferenceParam subject,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, activityCode, activityDate, activityReference, basedOn, careTeam,
                category, condition, context, date, definition, encounter, goal, identifier, intent, partOf, patient,
                performer, replaces, status, subject, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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
        List<CarePlanEntity> carePlanEntitys = mongo.find(query, CarePlanEntity.class);
        if (carePlanEntitys != null) {
            for (CarePlanEntity item : carePlanEntitys) {
                CarePlan carePlan = carePlanEntityToFHIRCarePlan.transform(item);
                resources.add(carePlan);
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, TokenParam activityCode,
            DateRangeParam activityDate, ReferenceParam activityReference, ReferenceParam basedOn,
            ReferenceParam careTeam, TokenParam category, ReferenceParam condition, ReferenceParam context,
            DateRangeParam date, ReferenceParam definition, ReferenceParam encounter, ReferenceParam goal,
            TokenParam identifier, TokenParam intent, ReferenceParam partOf, ReferenceParam patient,
            ReferenceParam performer, ReferenceParam replaces, TokenParam status, ReferenceParam subject,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, activityCode, activityDate, activityReference, basedOn, careTeam,
                category, condition, context, date, definition, encounter, goal, identifier, intent, partOf, patient,
                performer, replaces, status, subject, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, CarePlanEntity.class);
        return total;
    }

    private CarePlanEntity createNewCarePlanEntity(CarePlan obj, int version, String fhirId) {
        var ent = CarePlanEntity.fromCarePlan(obj);
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

    private Criteria setParamToCriteria(TokenParam active, TokenParam activityCode, DateRangeParam activityDate,
            ReferenceParam activityReference, ReferenceParam basedOn, ReferenceParam careTeam, TokenParam category,
            ReferenceParam condition, ReferenceParam context, DateRangeParam date, ReferenceParam definition,
            ReferenceParam encounter, ReferenceParam goal, TokenParam identifier, TokenParam intent,
            ReferenceParam partOf, ReferenceParam patient, ReferenceParam performer, ReferenceParam replaces,
            TokenParam status, ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
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

        // activity-code
        if (activityCode != null) {
            /** not write **/
        }
        // activity-date
        if (activityDate != null) {
            /** not write **/
        }
        // activity-reference
        if (activityReference != null) {
            /** not write **/
        }
        // based-on
        if (basedOn != null) {
            if(basedOn.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("basedOn.reference").is(basedOn.getValue()),
                        Criteria.where("basedOn.display").is(basedOn.getValue()));
            }else {
                String[] ref= basedOn.getValue().split("\\|");
                criteria.and("basedOn.identifier.system").is(ref[0]).and("basedOn.identifier.value").is(ref[1]);
            }
        }
        // care-team
        if (careTeam != null) {
            if(careTeam.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("careTeam.reference").is(careTeam.getValue()),
                        Criteria.where("careTeam.display").is(careTeam.getValue()));
            }else {
                String[] ref= careTeam.getValue().split("\\|");
                criteria.and("careTeam.identifier.system").is(ref[0]).and("careTeam.identifier.value").is(ref[1]);
            }
        }
        // category
        if (category != null && !category.isEmpty()) {
            criteria.and("category.coding.system").is(category.getSystem()).and("category.coding.code")
                    .is(category.getValue());
        }
        // condition
        if (condition != null) {
            if(condition.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("addresses.reference").is(condition.getValue()),
                        Criteria.where("addresses.display").is(basedOn.getValue()));
            }else {
                String[] ref= condition.getValue().split("\\|");
                criteria.and("addresses.identifier.system").is(ref[0]).and("addresses.identifier.value").is(ref[1]);
            }
        }
        // context
        if (context != null) {
            if(context.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("context.reference").is(context.getValue()),
                        Criteria.where("context.display").is(basedOn.getValue()));
            }else {
                String[] ref= context.getValue().split("\\|");
                criteria.and("context.identifier.system").is(ref[0]).and("context.identifier.value").is(ref[1]);
            }
        }
        // date
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "date", date);
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
        // definition
        if (encounter != null) {
            if(encounter.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("context.reference").is(encounter.getValue()),
                        Criteria.where("context.display").is(encounter.getValue()));
            }else {
                String[] ref= encounter.getValue().split("\\|");
                criteria.and("context.identifier.system").is(ref[0]).and("context.identifier.value").is(ref[1]);
            }
        }
        // goal
        if (goal != null) {
            if(goal.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("goal.reference").is(goal.getValue()),
                        Criteria.where("goal.display").is(goal.getValue()));
            }else {
                String[] ref= goal.getValue().split("\\|");
                criteria.and("goal.identifier.system").is(ref[0]).and("goal.identifier.value").is(ref[1]);
            }
        }
        // intent
        if (intent != null) {
            criteria.and("intent").is(intent.getValue());
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
        // replaces
        if (replaces != null) {
            if(replaces.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("replaces.reference").is(replaces.getValue()),
                        Criteria.where("replaces.display").is(replaces.getValue()));
            }else {
                String[] ref= replaces.getValue().split("\\|");
                criteria.and("replaces.identifier.system").is(ref[0]).and("replaces.identifier.value").is(ref[1]);
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
}
