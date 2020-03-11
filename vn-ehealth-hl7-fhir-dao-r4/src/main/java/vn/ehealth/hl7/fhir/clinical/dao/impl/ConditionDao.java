package vn.ehealth.hl7.fhir.clinical.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.Condition;
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
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.clinical.dao.ICondition;
import vn.ehealth.hl7.fhir.clinical.dao.transform.ConditionEntityToFHIRCondition;
import vn.ehealth.hl7.fhir.clinical.entity.ConditionEntity;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;

@Repository
public class ConditionDao implements ICondition {

    @Autowired
    MongoOperations mongo;

    @Autowired
    ConditionEntityToFHIRCondition conditionEntityToFHIRCondition;

    @Override
    public Condition create(FhirContext fhirContext, Condition object) {
        ConditionEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewConditionEntity(object, version, null);
            // save ConditionEntity database
            mongo.save(entity);
            return conditionEntityToFHIRCondition.transform(entity);
        }
        return null;
    }

    @Override
    public Condition update(FhirContext fhirContext, Condition object, IdType idType) {
        ConditionEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, ConditionEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove ConditionEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save ConditionEntity
            int version = entityOld.version + 1;
            if (object != null) {
                ConditionEntity entity = createNewConditionEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return conditionEntityToFHIRCondition.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Condition read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ConditionEntity entity = mongo.findOne(query, ConditionEntity.class);
            if (entity != null) {
                return conditionEntityToFHIRCondition.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Condition remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ConditionEntity entity = mongo.findOne(query, ConditionEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return conditionEntityToFHIRCondition.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Condition readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                ConditionEntity entity = mongo.findOne(query, ConditionEntity.class);
                if (entity != null) {
                    return conditionEntityToFHIRCondition.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, QuantityParam abatementAge,
            TokenParam abatementBoolean, DateRangeParam abatementDate, TokenParam abatementString,
            DateRangeParam assertedDate, ReferenceParam asserter, TokenParam bodySite, TokenParam category,
            TokenParam clinicalStatus, TokenParam code, ReferenceParam context, ReferenceParam Condition,
            TokenParam evidence, ReferenceParam evidenceDetail, TokenParam identifier, QuantityParam onsetAge,
            DateRangeParam onsetDate, StringParam onsetInfo, ReferenceParam patient, TokenParam severity,
            TokenParam stage, ReferenceParam subject, TokenParam verificationStatus, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, abatementAge, abatementBoolean, abatementDate, abatementString,
                assertedDate, asserter, bodySite, category, clinicalStatus, code, context, Condition, evidence,
                evidenceDetail, identifier, onsetAge, onsetDate, onsetInfo, patient, severity, stage, subject,
                verificationStatus, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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
        List<ConditionEntity> conditionEntitys = mongo.find(query, ConditionEntity.class);
        if (conditionEntitys != null) {
            for (ConditionEntity item : conditionEntitys) {
                Condition condition = conditionEntityToFHIRCondition.transform(item);
                resources.add(condition);
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, QuantityParam abatementAge,
            TokenParam abatementBoolean, DateRangeParam abatementDate, TokenParam abatementString,
            DateRangeParam assertedDate, ReferenceParam asserter, TokenParam bodySite, TokenParam category,
            TokenParam clinicalStatus, TokenParam code, ReferenceParam context, ReferenceParam Condition,
            TokenParam evidence, ReferenceParam evidenceDetail, TokenParam identifier, QuantityParam onsetAge,
            DateRangeParam onsetDate, StringParam onsetInfo, ReferenceParam patient, TokenParam severity,
            TokenParam stage, ReferenceParam subject, TokenParam verificationStatus, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, abatementAge, abatementBoolean, abatementDate, abatementString,
                assertedDate, asserter, bodySite, category, clinicalStatus, code, context, Condition, evidence,
                evidenceDetail, identifier, onsetAge, onsetDate, onsetInfo, patient, severity, stage, subject,
                verificationStatus, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, ConditionEntity.class);
        return total;
    }

    private ConditionEntity createNewConditionEntity(Condition obj, int version, String fhirId) {
        var ent = ConditionEntity.fromCondition(obj);
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

    private Criteria setParamToCriteria(TokenParam active, QuantityParam abatementAge, TokenParam abatementBoolean,
            DateRangeParam abatementDate, TokenParam abatementString, DateRangeParam assertedDate,
            ReferenceParam asserter, TokenParam bodySite, TokenParam category, TokenParam clinicalStatus,
            TokenParam code, ReferenceParam context, ReferenceParam encounter, TokenParam evidence,
            ReferenceParam evidenceDetail, TokenParam identifier, QuantityParam onsetAge, DateRangeParam onsetDate,
            StringParam onsetInfo, ReferenceParam patient, TokenParam severity, TokenParam stage,
            ReferenceParam subject, TokenParam verificationStatus, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
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
        // abatementAge
        if (abatementAge != null) {
            criteria = DatabaseUtil.setQuantityToCriteria(criteria, "abatement", abatementAge);
        }
        // abatement-boolean
        if (abatementBoolean != null) {
            criteria.and("abatement.myStringValue").is(abatementBoolean.getValue());
        }
        // abatement-date
        if (abatementDate != null) {
            criteria = DatabaseUtil.setDatetimePeriodToCriteria(criteria, "abatement", abatementDate);
        }
        // abatement-string
        if (abatementString != null) {
            criteria.and("abatement.myStringValue").is(abatementString.getValue());
        }
        // asserted-date
        if (assertedDate != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "assertedDate", assertedDate);
        }
        // asserter
        if (asserter != null) {
            if(asserter.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("asserter.reference").is(asserter.getValue()),
                        Criteria.where("asserter.display").is(asserter.getValue()));
            }else {
                String[] ref= asserter.getValue().split("\\|");
                criteria.and("asserter.identifier.system").is(ref[0]).and("asserter.identifier.value").is(ref[1]);
            }
        }
        // body-site
        if (bodySite != null) {
            criteria.and("bodySite.coding.code.myStringValue").is(bodySite.getValue());
        }
        // category
        if (category != null) {
            criteria.and("category.coding.system").is(category.getSystem()).and("category.coding.code")
                    .is(category.getValue());
        }
        // clinical-status
        if (clinicalStatus != null && !clinicalStatus.isEmpty()) {
            criteria.and("clinicalStatus").is(category.getValue());
        }
        // code
        if (code != null) {
            criteria.and("code.coding.code.myStringValue").is(code.getValue());
        }
        // context
        if (context != null) {
            if(context.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("context.reference").is(context.getValue()),
                        Criteria.where("context.display").is(context.getValue()));
            }else {
                String[] ref= context.getValue().split("\\|");
                criteria.and("context.identifier.system").is(ref[0]).and("context.identifier.value").is(ref[1]);
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
        // evidence
        if (evidence != null) {
            criteria.and("evidence.code.coding.code.myStringValue").is(evidence.getValue());
        }
        // evidence-detail
        if (evidenceDetail != null) {
            if(evidenceDetail.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("evidence.detail.reference").is(evidenceDetail.getValue()),
                        Criteria.where("evidence.detail.display").is(evidenceDetail.getValue()));
            }else {
                String[] ref= evidenceDetail.getValue().split("\\|");
                criteria.and("evidence.detail.identifier.system").is(ref[0]).and("evidence.detail.identifier.value").is(ref[1]);
            }
        }
        // onset-age
        if (onsetAge != null) {
            criteria = DatabaseUtil.setQuantityToCriteria(criteria, "onset", onsetAge);
        }
        // onset-date
        if (onsetDate != null) {
            criteria = DatabaseUtil.setDatetimePeriodToCriteria(criteria, "onset", onsetDate);
        }
        // onset-info
        if (onsetInfo != null) {
            criteria.and("onset.myStringValue").is(onsetInfo.getValue());
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
        // severity
        if (severity != null) {
            criteria.and("severity.coding.code.myStringValue").is(severity.getValue());
        }
        // stage
        if (stage != null) {
            criteria.and("stage.summary.coding.code.myStringValue").is(severity.getValue());
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
        // verification-status
        if (verificationStatus != null) {
            criteria.and("verificationStatus").is(verificationStatus.getValue());
        }
        return criteria;
    }
}
