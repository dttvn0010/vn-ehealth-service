package vn.ehealth.hl7.fhir.clinical.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
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
import vn.ehealth.hl7.fhir.clinical.dao.IGoal;
import vn.ehealth.hl7.fhir.clinical.dao.transform.GoalEntityToFHIRGoal;
import vn.ehealth.hl7.fhir.clinical.entity.GoalEntity;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;

@Repository
public class GoalDao implements IGoal {

    @Autowired
    MongoOperations mongo;

    @Autowired
    GoalEntityToFHIRGoal goalEntityToFHIRGoal;

    @Override
    public Goal create(FhirContext fhirContext, Goal object) {
        GoalEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewGoalEntity(object, version, null);
            // save GoalEntity database
            mongo.save(entity);
            return goalEntityToFHIRGoal.transform(entity);
        }
        return null;
    }

    @Override
    public Goal update(FhirContext fhirContext, Goal object, IdType idType) {
        GoalEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, GoalEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove GoalEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save GoalEntity
            int version = entityOld.version + 1;
            if (object != null) {
                GoalEntity entity = createNewGoalEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return goalEntityToFHIRGoal.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Goal read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            GoalEntity entity = mongo.findOne(query, GoalEntity.class);
            if (entity != null) {
                return goalEntityToFHIRGoal.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Goal remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            GoalEntity entity = mongo.findOne(query, GoalEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return goalEntityToFHIRGoal.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Goal readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                GoalEntity entity = mongo.findOne(query, GoalEntity.class);
                if (entity != null) {
                    return goalEntityToFHIRGoal.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, TokenParam category, TokenParam identifier,
            ReferenceParam patient, DateRangeParam startDate, TokenParam status, ReferenceParam subject,
            DateRangeParam targetDate, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
            String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, category, identifier, patient, startDate, status, subject,
                targetDate, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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
        List<GoalEntity> goalEntitys = mongo.find(query, GoalEntity.class);
        if (goalEntitys != null) {
            for (GoalEntity item : goalEntitys) {
                Goal goal = goalEntityToFHIRGoal.transform(item);
                resources.add(goal);
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, TokenParam category,
            TokenParam identifier, ReferenceParam patient, DateRangeParam startDate, TokenParam status,
            ReferenceParam subject, DateRangeParam targetDate, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, category, identifier, patient, startDate, status, subject,
                targetDate, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, GoalEntity.class);
        return total;
    }

    private GoalEntity createNewGoalEntity(Goal obj, int version, String fhirId) {
        var ent = GoalEntity.fromGoalEntity(obj);
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

    private Criteria setParamToCriteria(TokenParam active, TokenParam category, TokenParam identifier,
            ReferenceParam patient, DateRangeParam startDate, TokenParam status, ReferenceParam subject,
            DateRangeParam targetDate, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
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

        // category
        if (category != null && !category.isEmpty()) {
            criteria.and("category.coding.system").is(category.getSystem()).and("category.coding.code")
                    .is(category.getValue());
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
        // start-date
        if (startDate != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "start", startDate);
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
        // target-date
        if (targetDate != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "target.due", targetDate);
        }
        return criteria;
    }

}
