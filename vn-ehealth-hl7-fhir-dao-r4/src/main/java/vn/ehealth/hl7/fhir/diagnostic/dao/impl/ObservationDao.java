package vn.ehealth.hl7.fhir.diagnostic.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
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
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.diagnostic.dao.IObservation;
import vn.ehealth.hl7.fhir.diagnostic.dao.transform.ObservationEntityToFHIRObservation;
import vn.ehealth.hl7.fhir.diagnostic.entity.ObservationEntity;

@Repository
public class ObservationDao implements IObservation {

    @Autowired
    MongoOperations mongo;

    @Autowired
    ObservationEntityToFHIRObservation observationEntityToFHIRObservation;

    @Override
    public Observation create(FhirContext fhirContext, Observation object) {
        ObservationEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewObservationEntity(object, version, null);
            // save ObservationEntity database
            mongo.save(entity);
            return observationEntityToFHIRObservation.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "observation", key = "#idType")
    public Observation update(FhirContext fhirContext, Observation object, IdType idType) {
        ObservationEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, ObservationEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove ObservationEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save ObservationEntity
            int version = entityOld.version + 1;
            if (object != null) {
                ObservationEntity entity = createNewObservationEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return observationEntityToFHIRObservation.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "observation", key = "#idType")
    public Observation read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ObservationEntity entity = mongo.findOne(query, ObservationEntity.class);
            if (entity != null) {
                return observationEntityToFHIRObservation.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "observation", key = "#idType")
    public Observation remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ObservationEntity entity = mongo.findOne(query, ObservationEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return observationEntityToFHIRObservation.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Observation readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                ObservationEntity entity = mongo.findOne(query, ObservationEntity.class);
                if (entity != null) {
                    return observationEntityToFHIRObservation.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, ReferenceParam basedOn,
            TokenParam category, TokenParam code, TokenParam comboCode, TokenParam comboDataAbsentReason,
            TokenParam comboValueConcept, TokenParam componentCode, TokenParam componentDataAbsentReason,
            TokenParam componentValueConcept, ReferenceParam conetext, TokenParam dataAbsentReason, DateRangeParam date,
            ReferenceParam device, ReferenceParam encounter, TokenParam identifier, TokenParam method,
            ReferenceParam patient, ReferenceParam performer, ReferenceParam relatedTarget, TokenParam relatedType,
            ReferenceParam specimen, TokenParam status, ReferenceParam subject, TokenParam valueConcept,
            DateRangeParam valueDate, StringParam valueString, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content,
            StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, basedOn, category, code, comboCode, comboDataAbsentReason,
                comboValueConcept, componentCode, componentDataAbsentReason, componentValueConcept, conetext,
                dataAbsentReason, date, device, encounter, identifier, method, patient, performer, relatedTarget,
                relatedType, specimen, status, subject, valueConcept, valueDate, valueString, resid, _lastUpdated, _tag,
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
        List<ObservationEntity> ObservationEntitys = mongo.find(query, ObservationEntity.class);
        if (ObservationEntitys != null) {
            for (ObservationEntity item : ObservationEntitys) {
                Observation Observation = observationEntityToFHIRObservation.transform(item);
                resources.add(Observation);
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, ReferenceParam basedOn,
            TokenParam category, TokenParam code, TokenParam comboCode, TokenParam comboDataAbsentReason,
            TokenParam comboValueConcept, TokenParam componentCode, TokenParam componentDataAbsentReason,
            TokenParam componentValueConcept, ReferenceParam conetext, TokenParam dataAbsentReason, DateRangeParam date,
            ReferenceParam device, ReferenceParam encounter, TokenParam identifier, TokenParam method,
            ReferenceParam patient, ReferenceParam performer, ReferenceParam relatedTarget, TokenParam relatedType,
            ReferenceParam specimen, TokenParam status, ReferenceParam subject, TokenParam valueConcept,
            DateRangeParam valueDate, StringParam valueString, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, basedOn, category, code, comboCode, comboDataAbsentReason,
                comboValueConcept, componentCode, componentDataAbsentReason, componentValueConcept, conetext,
                dataAbsentReason, date, device, encounter, identifier, method, patient, performer, relatedTarget,
                relatedType, specimen, status, subject, valueConcept, valueDate, valueString, resid, _lastUpdated, _tag,
                _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, ObservationEntity.class);
        return total;
    }

    private ObservationEntity createNewObservationEntity(Observation obj, int version, String fhirId) {
        var ent = ObservationEntity.fromObservation(obj);
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

    private Criteria setParamToCriteria(TokenParam active, ReferenceParam basedOn, TokenParam category, TokenParam code,
            TokenParam comboCode, TokenParam comboDataAbsentReason, TokenParam comboValueConcept,
            TokenParam componentCode, TokenParam componentDataAbsentReason, TokenParam componentValueConcept,
            ReferenceParam conetext, TokenParam dataAbsentReason, DateRangeParam date, ReferenceParam device,
            ReferenceParam encounter, TokenParam identifier, TokenParam method, ReferenceParam patient,
            ReferenceParam performer, ReferenceParam relatedTarget, TokenParam relatedType, ReferenceParam specimen,
            TokenParam status, ReferenceParam subject, TokenParam valueConcept, DateRangeParam valueDate,
            StringParam valueString, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
            TokenParam _query, TokenParam _security, StringParam _content) {
        Criteria criteria = null;
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active);
        } else {
            criteria = Criteria.where("active").is(true);
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
                criteria.orOperator(Criteria.where("subject.reference").is("Patient/" + patient.getValue()),
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
        // set param default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);

        return criteria;
    }


}
