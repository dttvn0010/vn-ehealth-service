package vn.ehealth.hl7.fhir.medication.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationDispense;
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
import vn.ehealth.hl7.fhir.medication.dao.IMedicationDispense;
import vn.ehealth.hl7.fhir.medication.dao.transform.MedicationDispenseEntityToFHIRMedicationDispense;
import vn.ehealth.hl7.fhir.medication.entity.MedicationDispenseEntity;

@Repository
public class MedicationDispenseDao implements IMedicationDispense {

    @Autowired
    MongoOperations mongo;

    @Autowired
    MedicationDispenseEntityToFHIRMedicationDispense medicationDispenseEntityToFHIRMedicationDispense;

    @Override
    public MedicationDispense create(FhirContext fhirContext, MedicationDispense object) {
        MedicationDispenseEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewMedicationDispenseEntity(object, version, null);
            // save MedicationDispenseEntity database
            mongo.save(entity);
            return medicationDispenseEntityToFHIRMedicationDispense.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "medicationDispense", key = "#idType")
    public MedicationDispense update(FhirContext fhirContext, MedicationDispense object, IdType idType) {
        MedicationDispenseEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, MedicationDispenseEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove MedicationDispenseEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save MedicationDispenseEntity
            int version = entityOld.version + 1;
            if (object != null) {
                MedicationDispenseEntity entity = createNewMedicationDispenseEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return medicationDispenseEntityToFHIRMedicationDispense.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "medicationDispense", key = "#idType")
    public MedicationDispense read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            MedicationDispenseEntity entity = mongo.findOne(query, MedicationDispenseEntity.class);
            if (entity != null) {
                return medicationDispenseEntityToFHIRMedicationDispense.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "medicationDispense", key = "#idType")
    public MedicationDispense remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            MedicationDispenseEntity entity = mongo.findOne(query, MedicationDispenseEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return medicationDispenseEntityToFHIRMedicationDispense.transform(entity);
            }
        }
        return null;
    }

    @Override
    public MedicationDispense readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                MedicationDispenseEntity entity = mongo.findOne(query, MedicationDispenseEntity.class);
                if (entity != null) {
                    return medicationDispenseEntityToFHIRMedicationDispense.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, TokenParam code, TokenParam type,
            TokenParam status, TokenParam identifier, ReferenceParam context, ReferenceParam destination,
            ReferenceParam medication, ReferenceParam patient, ReferenceParam performer, ReferenceParam prescription,
            ReferenceParam receiver, ReferenceParam responsibleparty, ReferenceParam subject,
            ReferenceParam whenhandedover, DateRangeParam whenprepared, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content,
            StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, code, type, status, identifier, context, destination, medication,
                patient, performer, prescription, receiver, responsibleparty, subject, whenhandedover, whenprepared,
                resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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
        List<MedicationDispenseEntity> medicationDispenseEntitys = mongo.find(query, MedicationDispenseEntity.class);
        if (medicationDispenseEntitys != null) {
            for (MedicationDispenseEntity item : medicationDispenseEntitys) {
                MedicationDispense medicationDispense = medicationDispenseEntityToFHIRMedicationDispense
                        .transform(item);
                resources.add(medicationDispense);
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, TokenParam code, TokenParam type,
            TokenParam status, TokenParam identifier, ReferenceParam context, ReferenceParam destination,
            ReferenceParam medication, ReferenceParam patient, ReferenceParam performer, ReferenceParam prescription,
            ReferenceParam receiver, ReferenceParam responsibleparty, ReferenceParam subject,
            ReferenceParam whenhandedover, DateRangeParam whenprepared, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, code, type, status, identifier, context, destination, medication,
                patient, performer, prescription, receiver, responsibleparty, subject, whenhandedover, whenprepared,
                resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, MedicationDispenseEntity.class);
        return total;
    }

    private MedicationDispenseEntity createNewMedicationDispenseEntity(MedicationDispense obj, int version,
            String fhirId) {
        var ent = MedicationDispenseEntity.fromMedicationDispense(obj);
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

    private Criteria setParamToCriteria(TokenParam active, TokenParam code, TokenParam type, TokenParam status,
            TokenParam identifier, ReferenceParam context, ReferenceParam destination, ReferenceParam medication,
            ReferenceParam patient, ReferenceParam performer, ReferenceParam prescription, ReferenceParam receiver,
            ReferenceParam responsibleparty, ReferenceParam subject, ReferenceParam whenhandedover,
            DateRangeParam whenprepared, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        Criteria criteria = null;
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active);
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // status
        if (status != null) {
            criteria.and("status").is(status.getValue());
        }
        return criteria;
    }
}
