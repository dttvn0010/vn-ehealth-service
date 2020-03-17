package vn.ehealth.hl7.fhir.patient.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.RelatedPerson;
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
import vn.ehealth.hl7.fhir.patient.dao.IRelatedPerson;
import vn.ehealth.hl7.fhir.patient.dao.transform.RelatedPersonEntityToFHIRRelatedPerson;
import vn.ehealth.hl7.fhir.patient.entity.RelatedPersonEntity;

@Repository
public class RelatedPersonDao implements IRelatedPerson {

    @Autowired
    MongoOperations mongo;

    @Autowired
    RelatedPersonEntityToFHIRRelatedPerson relatedPersonEntityToFHIRRelatedPerson;

    @Override
    public RelatedPerson create(FhirContext fhirContext, RelatedPerson object) {
        RelatedPersonEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewRelatedPersonEntity(object, version, null);
            // save RelatedPersonEntity database
            mongo.save(entity);
            return relatedPersonEntityToFHIRRelatedPerson.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "relatedPerson", key = "#idType")
    public RelatedPerson update(FhirContext fhirContext, RelatedPerson object, IdType idType) {
        RelatedPersonEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, RelatedPersonEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove RelatedPersonEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save RelatedPersonEntity
            int version = entityOld.version + 1;
            if (object != null) {
                RelatedPersonEntity entity = createNewRelatedPersonEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return relatedPersonEntityToFHIRRelatedPerson.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "relatedPerson", key = "#idType")
    public RelatedPerson read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            RelatedPersonEntity entity = mongo.findOne(query, RelatedPersonEntity.class);
            if (entity != null) {
                return relatedPersonEntityToFHIRRelatedPerson.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "relatedPerson", key = "#idType")
    public RelatedPerson remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            RelatedPersonEntity entity = mongo.findOne(query, RelatedPersonEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return relatedPersonEntityToFHIRRelatedPerson.transform(entity);
            }
        }
        return null;
    }

    @Override
    public RelatedPerson readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                RelatedPersonEntity entity = mongo.findOne(query, RelatedPersonEntity.class);
                if (entity != null) {
                    return relatedPersonEntityToFHIRRelatedPerson.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext ctx, TokenParam active, StringParam address, StringParam addressCity,
            StringParam addressCountry, StringParam addressState, DateRangeParam birthDate, TokenParam email,
            StringParam gender, TokenParam identifier, StringParam name, ReferenceParam patient, TokenParam phone,
            StringParam phonetic, TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
            String sortParam, Integer count) {

        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, address, addressCity, addressCountry, addressState, birthDate,
                email, gender, identifier, name, patient, phone, phonetic, telecom, resid, _lastUpdated, _tag, _profile,
                _query, _security, _content);

        if (criteria != null) {
            Query qry = Query.query(criteria);
            Pageable pageableRequest;
            pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                    count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
            qry.with(pageableRequest);
            if (!sortParam.equals("")) {
                qry.with(new Sort(Sort.Direction.ASC, sortParam));
            }
            List<RelatedPersonEntity> objResults = mongo.find(qry, RelatedPersonEntity.class);

            for (RelatedPersonEntity objEntity : objResults) {
                resources.add(relatedPersonEntityToFHIRRelatedPerson.transform(objEntity));
            }
        }

        return resources;
    }

    @Override
    public long findMatchesAdvancedTotal(FhirContext ctx, TokenParam active, StringParam address,
            StringParam addressCity, StringParam addressCountry, StringParam addressState, DateRangeParam birthDate,
            TokenParam email, StringParam gender, TokenParam identifier, StringParam name, ReferenceParam patient,
            TokenParam phone, StringParam phonetic, TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {

        Criteria criteria = setParamToCriteria(active, address, addressCity, addressCountry, addressState, birthDate,
                email, gender, identifier, name, patient, phone, phonetic, telecom, resid, _lastUpdated, _tag, _profile,
                _query, _security, _content);
        long count = 0;
        if (criteria != null) {
            Query qry = Query.query(criteria);
            count = mongo.count(qry, RelatedPersonEntity.class);
        } else {
            Query query = new Query();
            count = mongo.count(query, RelatedPersonEntity.class);
        }
        return count;
    }

    private Criteria setParamToCriteria(TokenParam active, StringParam address, StringParam addressCity,
            StringParam addressCountry, StringParam addressState, DateRangeParam birthDate, TokenParam email,
            StringParam gender, TokenParam identifier, StringParam name, ReferenceParam patient, TokenParam phone,
            StringParam phonetic, TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        Criteria criteria = null;
        if (active != null) {
            criteria = Criteria.where("active").is(active.getValue());
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);

        if (address != null) {
            criteria.orOperator(Criteria.where("address.addressLine1.myStringValue").regex(address.getValue()),
                    Criteria.where("address.addressLine2.myStringValue").regex(address.getValue()));
        }

        if (addressCity != null) {
            criteria.and("address.city").regex(addressCity.getValue());
        }
        if (addressCountry != null) {
            criteria.and("address.country").regex(addressCountry.getValue());
        }
        if (addressState != null) {
            criteria.and("address.state").regex(addressState.getValue());
        }
        if (birthDate != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "birthDate", birthDate);
        }
        if (phone != null) {
            criteria.and("telecom.system").is("PHONE").and("telecom.phone").is(phone.getValue());
        }
        if (email != null) {
            criteria.and("telecom.system").is("EMAIL").and("telecom.value").is(email.getValue());
        }
        if (patient != null) {
            criteria.orOperator(Criteria.where("patient.reference").regex(patient.getValue()),
                    Criteria.where("patient.display").regex(patient.getValue()),
                    Criteria.where("patient.identifier.value").regex(patient.getValue()),
                    Criteria.where("patient.identifier.system").regex(patient.getValue()));
        }
        if (name != null) {

            String regexName = name.getValue(); // .toLowerCase()+".*"; // use options = i for regex
            criteria.orOperator(Criteria.where("name.family").regex(regexName),
                    Criteria.where("name.given").regex(regexName));
        }
        return criteria;
    }

    private RelatedPersonEntity createNewRelatedPersonEntity(RelatedPerson obj, int version, String fhirId) {
        var ent = RelatedPersonEntity.fromRelatedPerson(obj);
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
}
