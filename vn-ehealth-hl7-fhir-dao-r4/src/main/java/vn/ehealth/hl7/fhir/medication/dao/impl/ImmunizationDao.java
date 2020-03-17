package vn.ehealth.hl7.fhir.medication.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Immunization;
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
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.medication.dao.IImmunization;
import vn.ehealth.hl7.fhir.medication.dao.transform.ImmunizationEntityToFHIRImmunization;
import vn.ehealth.hl7.fhir.medication.entity.ImmunizationEntity;

@Repository
public class ImmunizationDao implements IImmunization {

    @Autowired
    MongoOperations mongo;

    @Autowired
    ImmunizationEntityToFHIRImmunization immunizationEntityToFHIRImmunization;

    @Override
    public Immunization create(FhirContext fhirContext, Immunization object) {
        ImmunizationEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewImmunizationEntity(object, version, null);
            // save ImmunizationEntity database
            mongo.save(entity);
            return immunizationEntityToFHIRImmunization.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "immunization", key = "#idType")
    public Immunization update(FhirContext fhirContext, Immunization object, IdType idType) {
        ImmunizationEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, ImmunizationEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove ImmunizationEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save ImmunizationEntity
            int version = entityOld.version + 1;
            if (object != null) {
                ImmunizationEntity entity = createNewImmunizationEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return immunizationEntityToFHIRImmunization.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "immunization", key = "#idType")
    public Immunization read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ImmunizationEntity entity = mongo.findOne(query, ImmunizationEntity.class);
            if (entity != null) {
                return immunizationEntityToFHIRImmunization.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "immunization", key = "#idType")
    public Immunization remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ImmunizationEntity entity = mongo.findOne(query, ImmunizationEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return immunizationEntityToFHIRImmunization.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Immunization readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                ImmunizationEntity entity = mongo.findOne(query, ImmunizationEntity.class);
                if (entity != null) {
                    return immunizationEntityToFHIRImmunization.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, DateRangeParam date,
            NumberParam doseSequence, TokenParam identifier, ReferenceParam location, StringParam lotNumber,
            ReferenceParam manufacturer, TokenParam notgiven, ReferenceParam patient, ReferenceParam practitioner,
            ReferenceParam reaction, DateRangeParam reactionDate, TokenParam reason, TokenParam reasonNotGiven,
            TokenParam status, TokenParam vaccineCode, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
            String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, date, doseSequence, identifier, location, lotNumber,
                manufacturer, notgiven, patient, practitioner, reaction, reactionDate, reason, reasonNotGiven, status,
                vaccineCode, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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
        List<ImmunizationEntity> immunizationEntitys = mongo.find(query, ImmunizationEntity.class);
        if (immunizationEntitys != null) {
            for (ImmunizationEntity item : immunizationEntitys) {
                Immunization immunization = immunizationEntityToFHIRImmunization.transform(item);
                resources.add(immunization);
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, DateRangeParam date,
            NumberParam doseSequence, TokenParam identifier, ReferenceParam location, StringParam lotNumber,
            ReferenceParam manufacturer, TokenParam notgiven, ReferenceParam patient, ReferenceParam practitioner,
            ReferenceParam reaction, DateRangeParam reactionDate, TokenParam reason, TokenParam reasonNotGiven,
            TokenParam status, TokenParam vaccineCode, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, date, doseSequence, identifier, location, lotNumber,
                manufacturer, notgiven, patient, practitioner, reaction, reactionDate, reason, reasonNotGiven, status,
                vaccineCode, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, ImmunizationEntity.class);
        return total;
    }

    private ImmunizationEntity createNewImmunizationEntity(Immunization obj, int version, String fhirId) {
        var ent = ImmunizationEntity.fromImmunization(obj);
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

    private Criteria setParamToCriteria(TokenParam active, DateRangeParam date, NumberParam doseSequence,
            TokenParam identifier, ReferenceParam location, StringParam lotNumber, ReferenceParam manufacturer,
            TokenParam notgiven, ReferenceParam patient, ReferenceParam practitioner, ReferenceParam reaction,
            DateRangeParam reactionDate, TokenParam reason, TokenParam reasonNotGiven, TokenParam status,
            TokenParam vaccineCode, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
            TokenParam _query, TokenParam _security, StringParam _content) {
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
        // date
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "date", date);
        }
        // dose-sequence
        if (doseSequence != null) {
            criteria.and("vaccinationProtocol.doseSequence").is(doseSequence.getValue());
        }
        // location
        if (location != null) {
            if(location.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("location.reference").is(location.getValue()),
                        Criteria.where("location.display").is(location.getValue()));
            }else {
                String[] ref= location.getValue().split("\\|");
                criteria.and("location.identifier.system").is(ref[0]).and("location.identifier.value").is(ref[1]);
            }
        }
        // lot-number
        if (lotNumber != null && !lotNumber.isEmpty()) {
            criteria.and("lotNumber").is(lotNumber.getValue());
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
        // notgiven
        if (notgiven != null && !notgiven.isEmpty()) {
            /** not write **/
        }
        // patient
        if (patient != null) {
            if(patient.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("patient.reference").is(patient.getValue()),
                        Criteria.where("patient.display").is(patient.getValue()));
            }else {
                String[] ref= patient.getValue().split("\\|");
                criteria.and("patient.identifier.system").is(ref[0]).and("patient.identifier.value").is(ref[1]);
            }
        }
        // practitioner
        if (practitioner != null) {
            if(practitioner.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("practitioner.actor.reference").is(practitioner.getValue()),
                        Criteria.where("practitioner.actor.display").is(practitioner.getValue()));
            }else {
                String[] ref= practitioner.getValue().split("\\|");
                criteria.and("practitioner.actor.identifier.system").is(ref[0]).and("practitioner.actor.identifier.value").is(ref[1]);
            }
        }
        // reaction
        if (reaction != null) {
            if(reaction.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("reaction.detail.reference").is(reaction.getValue()),
                        Criteria.where("reaction.detail.display").is(reaction.getValue()));
            }else {
                String[] ref= reaction.getValue().split("\\|");
                criteria.and("reaction.detail.identifier.system").is(ref[0]).and("reaction.detail.identifier.value").is(ref[1]);
            }
        }
        // reaction-date
        if (reactionDate != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "reaction.date", reactionDate);
        }
        // reason
        if (reason != null && !reason.isEmpty()) {
            criteria.and("explanation.reason.coding.system").is(reason.getSystem())
                    .and("explanation.reason.coding.code").is(reason.getValue());
        }
        // reason-not-given
        if (reasonNotGiven != null && !reasonNotGiven.isEmpty()) {
            criteria.and("explanation.reasonNotGiven.coding.system").is(reasonNotGiven.getSystem())
                    .and("explanation.reasonNotGiven.coding.code").is(reasonNotGiven.getValue());
        }
        // status
        if (status != null) {
            criteria.and("status").is(status.getValue());
        }
        // vaccine-code
        if (vaccineCode != null && !vaccineCode.isEmpty()) {
            criteria.and("vaccineCode.coding.system").is(vaccineCode.getSystem()).and("vaccineCode.coding.code")
                    .is(vaccineCode.getValue());
        }
        return criteria;
    }
}
