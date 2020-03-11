package vn.ehealth.hl7.fhir.clinical.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.ClinicalImpression;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
import vn.ehealth.hl7.fhir.clinical.dao.IClinicalImpression;
import vn.ehealth.hl7.fhir.clinical.dao.transform.ClinicalImpressionEntityToFHIRClinicalImpression;
import vn.ehealth.hl7.fhir.clinical.entity.ClinicalImpressionEntity;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;

@Repository
public class ClinicalImpressionDao implements IClinicalImpression {

    @Autowired
    MongoOperations mongo;

    @Autowired
    ClinicalImpressionEntityToFHIRClinicalImpression clinicalImpressionEntityToFHIRClinicalImpression;

    @Override
    public ClinicalImpression create(FhirContext fhirContext, ClinicalImpression object) {
        ClinicalImpressionEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewClinicalImpressionEntity(object, version, null);
            // save ClinicalImpressionEntity database
            mongo.save(entity);
            return clinicalImpressionEntityToFHIRClinicalImpression.transform(entity);
        }
        return null;
    }

    @Override
    public ClinicalImpression update(FhirContext fhirContext, ClinicalImpression object, IdType idType) {
        ClinicalImpressionEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, ClinicalImpressionEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove ClinicalImpressionEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save ClinicalImpressionEntity
            int version = entityOld.version + 1;
            if (object != null) {
                ClinicalImpressionEntity entity = createNewClinicalImpressionEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return clinicalImpressionEntityToFHIRClinicalImpression.transform(entity);
            }
        }
        return null;
    }

    @Override
    public ClinicalImpression read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ClinicalImpressionEntity entity = mongo.findOne(query, ClinicalImpressionEntity.class);
            if (entity != null) {
                return clinicalImpressionEntityToFHIRClinicalImpression.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "clinicalImpression", key = "#idType")
    public ClinicalImpression remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ClinicalImpressionEntity entity = mongo.findOne(query, ClinicalImpressionEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return clinicalImpressionEntityToFHIRClinicalImpression.transform(entity);
            }
        }
        return null;
    }

    @Override
    public ClinicalImpression readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                ClinicalImpressionEntity entity = mongo.findOne(query, ClinicalImpressionEntity.class);
                if (entity != null) {
                    return clinicalImpressionEntityToFHIRClinicalImpression.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, ReferenceParam action,
            ReferenceParam assessor, ReferenceParam context, DateRangeParam date, TokenParam findingCode,
            ReferenceParam findingRef, TokenParam identifier, ReferenceParam investigation, ReferenceParam patient,
            ReferenceParam previous, ReferenceParam problem, TokenParam status, ReferenceParam subject,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, action, assessor, context, date, findingCode, findingRef,
                identifier, investigation, patient, previous, problem, status, subject, resid, _lastUpdated, _tag,
                _profile, _query, _security, _content);
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
        List<ClinicalImpressionEntity> clinicalImpressionEntitys = mongo.find(query, ClinicalImpressionEntity.class);
        if (clinicalImpressionEntitys != null) {
            for (ClinicalImpressionEntity item : clinicalImpressionEntitys) {
                ClinicalImpression clinicalImpression = clinicalImpressionEntityToFHIRClinicalImpression
                        .transform(item);
                resources.add(clinicalImpression);
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, ReferenceParam action,
            ReferenceParam assessor, ReferenceParam context, DateRangeParam date, TokenParam findingCode,
            ReferenceParam findingRef, TokenParam identifier, ReferenceParam investigation, ReferenceParam patient,
            ReferenceParam previous, ReferenceParam problem, TokenParam status, ReferenceParam subject,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, action, assessor, context, date, findingCode, findingRef,
                identifier, investigation, patient, previous, problem, status, subject, resid, _lastUpdated, _tag,
                _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, ClinicalImpressionEntity.class);
        return total;
    }

    private ClinicalImpressionEntity createNewClinicalImpressionEntity(ClinicalImpression obj, int version,
            String fhirId) {
        var ent = ClinicalImpressionEntity.fromClinicalImpression(obj);
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

    private Criteria setParamToCriteria(TokenParam active, ReferenceParam action, ReferenceParam assessor,
            ReferenceParam context, DateRangeParam date, TokenParam findingCode, ReferenceParam findingRef,
            TokenParam identifier, ReferenceParam investigation, ReferenceParam patient, ReferenceParam previous,
            ReferenceParam problem, TokenParam status, ReferenceParam subject, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
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
        // action
        if (action != null) {
            if(action.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("action.reference").is(action.getValue()),
                        Criteria.where("action.display").is(action.getValue()));
            }else {
                String[] ref= action.getValue().split("\\|");
                criteria.and("action.identifier.system").is(ref[0]).and("action.identifier.value").is(ref[1]);
            }
        }
        // assessor
        if (assessor != null) {
            if(assessor.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("assessor.reference").is(assessor.getValue()),
                        Criteria.where("assessor.display").is(assessor.getValue()));
            }else {
                String[] ref= assessor.getValue().split("\\|");
                criteria.and("assessor.identifier.system").is(ref[0]).and("assessor.identifier.value").is(ref[1]);
            }
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
        // date
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "date", date);
        }
        // finding-code
        if (findingCode != null) {
            criteria.and("finding.item.coding.system").is(findingCode.getSystem()).and("finding.item.coding.code")
                    .is(findingCode.getValue());
        }
        // finding-ref
        if (findingRef != null) {
            if(findingRef.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("finding.reference").is(findingRef.getValue()),
                        Criteria.where("finding.display").is(findingRef.getValue()));
            }else {
                String[] ref= findingRef.getValue().split("\\|");
                criteria.and("finding.identifier.system").is(ref[0]).and("finding.identifier.value").is(ref[1]);
            }
        }
        // investigation
        if (investigation != null) {
            if(investigation.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("investigation.reference").is(investigation.getValue()),
                        Criteria.where("investigation.display").is(investigation.getValue()));
            }else {
                String[] ref= investigation.getValue().split("\\|");
                criteria.and("investigation.identifier.system").is(ref[0]).and("investigation.identifier.value").is(ref[1]);
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
        // previous
        if (previous != null) {
            if(previous.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("previous.reference").is(previous.getValue()),
                        Criteria.where("previous.display").is(previous.getValue()));
            }else {
                String[] ref= previous.getValue().split("\\|");
                criteria.and("previous.identifier.system").is(ref[0]).and("previous.identifier.value").is(ref[1]);
            }
        }
        // problem
        if (problem != null) {
            if(problem.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("problem.reference").is(problem.getValue()),
                        Criteria.where("problem.display").is(problem.getValue()));
            }else {
                String[] ref= problem.getValue().split("\\|");
                criteria.and("problem.identifier.system").is(ref[0]).and("problem.identifier.value").is(ref[1]);
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

    @Override
    public ClinicalImpression findNotCache(FhirContext fhirContext, IdType idType) {
        if (idType != null) {
            ObjectId objectId = new ObjectId(idType.getIdPart());
            Query query = Query.query(Criteria.where("_id").is(objectId));
            ClinicalImpressionEntity entity = mongo.findOne(query, ClinicalImpressionEntity.class);
            if (entity != null) {
                return clinicalImpressionEntityToFHIRClinicalImpression.transform(entity);
            }
        }
        return null;
    }

}
