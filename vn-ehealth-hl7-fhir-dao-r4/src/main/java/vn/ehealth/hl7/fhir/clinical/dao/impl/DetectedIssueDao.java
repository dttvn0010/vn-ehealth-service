package vn.ehealth.hl7.fhir.clinical.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.DetectedIssue;
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
import vn.ehealth.hl7.fhir.clinical.dao.IDetectedIssue;
import vn.ehealth.hl7.fhir.clinical.dao.transform.DetectedIssueEntityToFHIRDetectedIssue;
import vn.ehealth.hl7.fhir.clinical.entity.DetectedIssueEntity;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;

@Repository
public class DetectedIssueDao implements IDetectedIssue {

    @Autowired
    MongoOperations mongo;

    @Autowired
    DetectedIssueEntityToFHIRDetectedIssue detectedIssueEntityToFHIRDetectedIssue;

    @Override
    public DetectedIssue create(FhirContext fhirContext, DetectedIssue object) {
        DetectedIssueEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewDetectedIssueEntity(object, version, null);
            // save DetectedIssueEntity database
            mongo.save(entity);
            return detectedIssueEntityToFHIRDetectedIssue.transform(entity);
        }
        return null;
    }

    @Override
    public DetectedIssue update(FhirContext fhirContext, DetectedIssue object, IdType idType) {
        DetectedIssueEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, DetectedIssueEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove DetectedIssueEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save DetectedIssueEntity
            int version = entityOld.version + 1;
            if (object != null) {
                DetectedIssueEntity entity = createNewDetectedIssueEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return detectedIssueEntityToFHIRDetectedIssue.transform(entity);
            }
        }
        return null;
    }

    @Override
    public DetectedIssue read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            DetectedIssueEntity entity = mongo.findOne(query, DetectedIssueEntity.class);
            if (entity != null) {
                return detectedIssueEntityToFHIRDetectedIssue.transform(entity);
            }
        }
        return null;
    }

    @Override
    public DetectedIssue remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            DetectedIssueEntity entity = mongo.findOne(query, DetectedIssueEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return detectedIssueEntityToFHIRDetectedIssue.transform(entity);
            }
        }
        return null;
    }

    @Override
    public DetectedIssue readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                DetectedIssueEntity entity = mongo.findOne(query, DetectedIssueEntity.class);
                if (entity != null) {
                    return detectedIssueEntityToFHIRDetectedIssue.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, ReferenceParam author, TokenParam category,
            DateRangeParam date, TokenParam identifier, ReferenceParam implicated, ReferenceParam patient,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, author, category, date, identifier, implicated, patient, resid,
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
        List<DetectedIssueEntity> detectedIssueEntitys = mongo.find(query, DetectedIssueEntity.class);
        if (detectedIssueEntitys != null) {
            for (DetectedIssueEntity item : detectedIssueEntitys) {
                DetectedIssue detectedIssue = detectedIssueEntityToFHIRDetectedIssue.transform(item);
                resources.add(detectedIssue);
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, ReferenceParam author,
            TokenParam category, DateRangeParam date, TokenParam identifier, ReferenceParam implicated,
            ReferenceParam patient, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
            TokenParam _query, TokenParam _security, StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, author, category, date, identifier, implicated, patient, resid,
                _lastUpdated, _tag, _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, DetectedIssueEntity.class);
        return total;
    }

    private DetectedIssueEntity createNewDetectedIssueEntity(DetectedIssue obj, int version, String fhirId) {
        var ent = DetectedIssueEntity.fromDetectedIssue(obj);
        DataConvertUtil.setMetaExt(obj, ent);
        if (fhirId != null && !fhirId.isEmpty()) {
            ent.fhirId = (fhirId);
        } else {
            ent.fhirId = (StringUtil.generateUID());
        }
        
        ent.active = (true);
        ent.version = (version);
        ent.resCreated = (new Date());
        return ent;
    }


    private Criteria setParamToCriteria(TokenParam active, ReferenceParam author, TokenParam category,
            DateRangeParam date, TokenParam identifier, ReferenceParam implicated, ReferenceParam patient,
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
        // author
        if (author != null) {
            if(author.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("author.reference").is(author.getValue()),
                        Criteria.where("author.display").is(author.getValue()));
            }else {
                String[] ref= author.getValue().split("\\|");
                criteria.and("author.identifier.system").is(ref[0]).and("author.identifier.value").is(ref[1]);
            }
        }
        // category
        if (category != null && !category.isEmpty()) {
            criteria.and("category.coding.system").is(category.getSystem()).and("category.coding.code")
                    .is(category.getValue());
        }
        // date
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "date", date);
        }
        // implicated
        if (implicated != null) {
            if(implicated.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("implicated.reference").is(implicated.getValue()),
                        Criteria.where("implicated.display").is(implicated.getValue()));
            }else {
                String[] ref= implicated.getValue().split("\\|");
                criteria.and("implicated.identifier.system").is(ref[0]).and("implicated.identifier.value").is(ref[1]);
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
        return criteria;
    }

}
