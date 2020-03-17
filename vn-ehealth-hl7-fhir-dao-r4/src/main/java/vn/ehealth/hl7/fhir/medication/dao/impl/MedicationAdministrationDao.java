package vn.ehealth.hl7.fhir.medication.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationAdministration;
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
import vn.ehealth.hl7.fhir.medication.dao.IMedicationAdministration;
import vn.ehealth.hl7.fhir.medication.dao.transform.MedicationAdministrationEntityToFHIRMedicationAdministration;
import vn.ehealth.hl7.fhir.medication.entity.MedicationAdministrationEntity;

@Repository
public class MedicationAdministrationDao implements IMedicationAdministration {

    @Autowired
    MongoOperations mongo;

    @Autowired
    MedicationAdministrationEntityToFHIRMedicationAdministration medicationAdministrationEntityToFHIRMedicationAdministration;

    @Override
    public MedicationAdministration create(FhirContext fhirContext, MedicationAdministration object) {
        MedicationAdministrationEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewMedicationAdministrationEntity(object, version, null);
            // save MedicationAdministrationEntity database
            mongo.save(entity);
            return medicationAdministrationEntityToFHIRMedicationAdministration.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "medicationAdministration", key = "#idType")
    public MedicationAdministration update(FhirContext fhirContext, MedicationAdministration object, IdType idType) {
        MedicationAdministrationEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, MedicationAdministrationEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove MedicationAdministrationEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save MedicationAdministrationEntity
            int version = entityOld.version + 1;
            if (object != null) {
                MedicationAdministrationEntity entity = createNewMedicationAdministrationEntity(object, version,
                        fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return medicationAdministrationEntityToFHIRMedicationAdministration.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "medicationAdministration", key = "#idType")
    public MedicationAdministration read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            MedicationAdministrationEntity entity = mongo.findOne(query, MedicationAdministrationEntity.class);
            if (entity != null) {
                return medicationAdministrationEntityToFHIRMedicationAdministration.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "medicationAdministration", key = "#idType")
    public MedicationAdministration remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            MedicationAdministrationEntity entity = mongo.findOne(query, MedicationAdministrationEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return medicationAdministrationEntityToFHIRMedicationAdministration.transform(entity);
            }
        }
        return null;
    }

    @Override
    public MedicationAdministration readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                MedicationAdministrationEntity entity = mongo.findOne(query, MedicationAdministrationEntity.class);
                if (entity != null) {
                    return medicationAdministrationEntityToFHIRMedicationAdministration.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, TokenParam code, ReferenceParam context,
            ReferenceParam device, DateRangeParam effectiveTime, TokenParam identifier, ReferenceParam medication,
            TokenParam notGiven, ReferenceParam patient, ReferenceParam performer, ReferenceParam prescription,
            TokenParam reasonGiven, TokenParam reasonNotGiven, TokenParam status, ReferenceParam subject,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, code, context, device, effectiveTime, identifier, medication,
                notGiven, patient, performer, prescription, reasonGiven, reasonNotGiven, status, subject, resid,
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
        List<MedicationAdministrationEntity> medicationAdministrationEntitys = mongo.find(query,
                MedicationAdministrationEntity.class);
        if (medicationAdministrationEntitys != null) {
            for (MedicationAdministrationEntity item : medicationAdministrationEntitys) {
                MedicationAdministration medicationAdministration = medicationAdministrationEntityToFHIRMedicationAdministration
                        .transform(item);
                resources.add(medicationAdministration);
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, TokenParam code,
            ReferenceParam context, ReferenceParam device, DateRangeParam effectiveTime, TokenParam identifier,
            ReferenceParam medication, TokenParam notGiven, ReferenceParam patient, ReferenceParam performer,
            ReferenceParam prescription, TokenParam reasonGiven, TokenParam reasonNotGiven, TokenParam status,
            ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
            TokenParam _query, TokenParam _security, StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, code, context, device, effectiveTime, identifier, medication,
                notGiven, patient, performer, prescription, reasonGiven, reasonNotGiven, status, subject, resid,
                _lastUpdated, _tag, _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, MedicationAdministrationEntity.class);
        return total;
    }

    private MedicationAdministrationEntity createNewMedicationAdministrationEntity(MedicationAdministration obj,
            int version, String fhirId) {
        var ent = MedicationAdministrationEntity.fromMedicationAdministration(obj);
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

    private Criteria setParamToCriteria(TokenParam active, TokenParam code, ReferenceParam context,
            ReferenceParam device, DateRangeParam effectiveTime, TokenParam identifier, ReferenceParam medication,
            TokenParam notGiven, ReferenceParam patient, ReferenceParam performer, ReferenceParam prescription,
            TokenParam reasonGiven, TokenParam reasonNotGiven, TokenParam status, ReferenceParam subject,
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
                null);
        // code
        if (code != null) {
            criteria.and("medication.coding.code.myStringValue").is(code.getValue());
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
        // device
        if (device != null) {
            if(device.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("device.reference").is(device.getValue()),
                        Criteria.where("device.display").is(device.getValue()));
            }else {
                String[] ref= device.getValue().split("\\|");
                criteria.and("device.identifier.system").is(ref[0]).and("device.identifier.value").is(ref[1]);
            }
        }
        // effective-time
        if (effectiveTime != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "effective", effectiveTime);
        }
        // medication
        if (medication != null) {
            if(medication.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("medication.reference").is(medication.getValue()),
                        Criteria.where("medication.display").is(medication.getValue()));
            }else {
                String[] ref= medication.getValue().split("\\|");
                criteria.and("medication.identifier.system").is(ref[0]).and("medication.identifier.value").is(ref[1]);
            }
        }
        // not-given
        if (notGiven != null) {
            /** not write **/
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
                criteria.orOperator(Criteria.where("performer.actor.reference").is(performer.getValue()),
                        Criteria.where("performer.actor.display").is(performer.getValue()));
            }else {
                String[] ref= performer.getValue().split("\\|");
                criteria.and("performer.actor.identifier.system").is(ref[0]).and("performer.actor.identifier.value").is(ref[1]);
            }
        }
        // prescription
        if (prescription != null) {
            if(prescription.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("prescription.reference").is(prescription.getValue()),
                        Criteria.where("prescription.display").is(prescription.getValue()));
            }else {
                String[] ref= prescription.getValue().split("\\|");
                criteria.and("prescription.identifier.system").is(ref[0]).and("prescription.identifier.value").is(ref[1]);
            }
        }
        // reason-given
        if (reasonGiven != null) {
            criteria.and("reasonGiven.coding.code.myStringValue").is(reasonGiven.getValue());
        }
        // reason-not-given
        if (reasonGiven != null) {
            criteria.and("reasonNotGiven.coding.code.myStringValue").is(reasonNotGiven.getValue());
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
