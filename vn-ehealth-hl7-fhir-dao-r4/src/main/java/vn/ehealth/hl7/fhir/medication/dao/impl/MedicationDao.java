package vn.ehealth.hl7.fhir.medication.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Medication;
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
import vn.ehealth.hl7.fhir.medication.dao.IMedication;
import vn.ehealth.hl7.fhir.medication.dao.transform.MedicationEntityToFHIRMedication;
import vn.ehealth.hl7.fhir.medication.entity.MedicationEntity;

@Repository
public class MedicationDao implements IMedication {

    @Autowired
    MongoOperations mongo;

    @Autowired
    MedicationEntityToFHIRMedication medicationEntityToFHIRMedication;

    @Override
    public Medication create(FhirContext fhirContext, Medication object) {
        MedicationEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewMedicationEntity(object, version, null);
            // save MedicationEntity database
            mongo.save(entity);
            return medicationEntityToFHIRMedication.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "medication", key = "#idType")
    public Medication update(FhirContext fhirContext, Medication object, IdType idType) {
        MedicationEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, MedicationEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove MedicationEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save MedicationEntity
            int version = entityOld.version + 1;
            if (object != null) {
                MedicationEntity entity = createNewMedicationEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return medicationEntityToFHIRMedication.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "medication", key = "#idType")
    public Medication read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            MedicationEntity entity = mongo.findOne(query, MedicationEntity.class);
            if (entity != null) {
                return medicationEntityToFHIRMedication.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "medication", key = "#idType")
    public Medication remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            MedicationEntity entity = mongo.findOne(query, MedicationEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return medicationEntityToFHIRMedication.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Medication readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                MedicationEntity entity = mongo.findOne(query, MedicationEntity.class);
                if (entity != null) {
                    return medicationEntityToFHIRMedication.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, TokenParam code, TokenParam container,
            TokenParam form, ReferenceParam ingredient, TokenParam ingredientCode, ReferenceParam manufacturer,
            TokenParam overTheCounter, ReferenceParam packageItem, TokenParam packageItemCode, TokenParam status,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content,TokenParam hospital,StringParam productName,StringParam medicationType, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, code, container, form, ingredient, ingredientCode, manufacturer,
                overTheCounter, packageItem, packageItemCode, status, resid, _lastUpdated, _tag, _profile, _query,
                _security, _content,hospital,productName,medicationType);
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
        List<MedicationEntity> medicationEntitys = mongo.find(query, MedicationEntity.class);
        if (medicationEntitys != null) {
            for (MedicationEntity item : medicationEntitys) {
                Medication medication = medicationEntityToFHIRMedication.transform(item);
                resources.add(medication);
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, TokenParam code,
            TokenParam container, TokenParam form, ReferenceParam ingredient, TokenParam ingredientCode,
            ReferenceParam manufacturer, TokenParam overTheCounter, ReferenceParam packageItem,
            TokenParam packageItemCode, TokenParam status, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content,TokenParam hospital,StringParam productName,StringParam medicationType) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, code, container, form, ingredient, ingredientCode, manufacturer,
                overTheCounter, packageItem, packageItemCode, status, resid, _lastUpdated, _tag, _profile, _query,
                _security, _content,hospital,productName,medicationType);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, MedicationEntity.class);
        return total;
    }

    private MedicationEntity createNewMedicationEntity(Medication obj, int version, String fhirId) {
        var ent = MedicationEntity.fromMedication(obj);
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

    private Criteria setParamToCriteria(TokenParam active, TokenParam code, TokenParam container, TokenParam form,
            ReferenceParam ingredient, TokenParam ingredientCode, ReferenceParam manufacturer,
            TokenParam overTheCounter, ReferenceParam packageItem, TokenParam packageItemCode, TokenParam status,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content,TokenParam hospital,StringParam productName,StringParam medicationType) {
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
            if(!StringUtils.isBlank(code.getSystem()) && !StringUtils.isBlank(code.getValue())) {
                criteria.and("code.coding.system.myStringValue").is(code.getSystem()).and("code.coding.code.myStringValue").regex(code.getValue(),"i");
            }else if(!StringUtils.isBlank(code.getSystem()) && StringUtils.isBlank(code.getValue())) {
                criteria.and("code.coding.system.myStringValue").is(code.getSystem());
            }else if(StringUtils.isBlank(code.getSystem()) && !StringUtils.isBlank(code.getValue())) {
                criteria.and("code.coding.code.myStringValue").regex(code.getValue(),"i");
            }            
        }
        // container
        if (container != null) {
            /** not write **/
        }
        // form
        if (form != null) {
            criteria.and("form.text.myStringValue").regex(form.getValue(),"i");
        }
        // ingredient
        if (ingredient != null) {
            /** not write **/
        }
        // ingredient-code
        if (ingredientCode != null) {
            /** not write **/
        }
        // manufacturer
        if (manufacturer != null) {
            if(manufacturer.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("manufacturer.reference").is(manufacturer.getValue()),
                        Criteria.where("manufacturer.display").is(manufacturer.getValue()));
            }else {
                String[] ref= manufacturer.getValue().split("\\|");
                criteria.and("manufacturer.identifier.system").is(ref[0]).and("manufacturer.identifier.value").is(ref[1]);
            }
        }
        // over-the-counte
        if (overTheCounter != null) {

        }
        // package-item
        if (packageItem != null) {

        }
        // package-item-code
        if (packageItemCode != null) {

        }
        // status
        if (status != null) {
            criteria.and("status").is(status.getValue());
        }
        if (hospital != null) {
            criteria.and("extension.url.myStringValue")
                    .is("https://fhir.yte360.com/StructureDefinition/Extension-ManagingOrgMedication-v1.0");
            if(hospital.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("extension.value.reference.myStringValue").regex(hospital.getValue()),
                        Criteria.where("extension.value.display").regex(hospital.getValue()));
            }else {
                String[] ref= hospital.getValue().split("\\|");
                criteria.and("extension.value.identifier.system.myStringValue").is(ref[0]).and("extension.value.identifier.value.myStringValue").is(ref[1]);
            }
        }
        if (productName != null) {
            criteria.and("extension.url.myStringValue")
                    .is("https://fhir.yte360.com/StructureDefinition/Extension-Medication-Production-Name-v1.0").and("extension.value.myStringValue").regex("^"+productName.getValue(),"i");
            
        }
        if (medicationType != null) {
            criteria.and("extension.url.myStringValue")
                    .is("https://fhir.yte360.com/StructureDefinition/Extension-Medication-Type-v1.0").and("extension.value.coding.code.myStringValue").regex("^"+medicationType.getValue(),"i");
            
        }
        return criteria;
    }
}
