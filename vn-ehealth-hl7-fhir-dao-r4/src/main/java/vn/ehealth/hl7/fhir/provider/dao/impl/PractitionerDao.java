package vn.ehealth.hl7.fhir.provider.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Practitioner;
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
import vn.ehealth.hl7.fhir.provider.dao.IPractitioner;
import vn.ehealth.hl7.fhir.provider.dao.transform.PractitionerEntityToFHIRPractitioner;
import vn.ehealth.hl7.fhir.provider.entity.PractitionerEntity;

@Repository
public class PractitionerDao implements IPractitioner {

    @Autowired
    MongoOperations mongo;

    @Autowired
    PractitionerEntityToFHIRPractitioner practitionerEntityToFHIRPractitioner;

    @Override
    public Practitioner create(FhirContext fhirContext, Practitioner object) {
        PractitionerEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewPractitionerEntity(object, version, null);
            // save PractitionerEntity database
            mongo.save(entity);
            return practitionerEntityToFHIRPractitioner.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "practitioner", key = "#idType")
    public Practitioner update(FhirContext fhirContext, Practitioner object, IdType idType) {
        PractitionerEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, PractitionerEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove PractitionerEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save PractitionerEntity
            int version = entityOld.version + 1;
            if (object != null) {
                PractitionerEntity entity = createNewPractitionerEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return practitionerEntityToFHIRPractitioner.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "practitioner", key = "#idType")
    public Practitioner read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            PractitionerEntity entity = mongo.findOne(query, PractitionerEntity.class);
            if (entity != null) {
                return practitionerEntityToFHIRPractitioner.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "practitioner", key = "#idType")
    public Practitioner remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            PractitionerEntity entity = mongo.findOne(query, PractitionerEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return practitionerEntityToFHIRPractitioner.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Practitioner readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                PractitionerEntity entity = mongo.findOne(query, PractitionerEntity.class);
                if (entity != null) {
                    return practitionerEntityToFHIRPractitioner.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, StringParam address,
            StringParam addressCity, StringParam addressCountry, StringParam addressPostalCode,
            StringParam addressState, TokenParam addressUse, TokenParam communication, TokenParam email,
            StringParam family, TokenParam gender, StringParam given, TokenParam identifier, StringParam name,
            TokenParam phone, StringParam phonetic, TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content,
            ReferenceParam managingOrg, StringParam _page, String sortParam, Integer count) {

        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, address, addressCity, addressCountry, addressPostalCode,
                addressState, addressUse, communication, email, family, gender, given, identifier, name, phone,
                phonetic, telecom, resid, _lastUpdated, _tag, _profile, _query, _security, _content, managingOrg);
        Query qry = new Query();
        if (criteria != null) {
            qry = Query.query(criteria);
        }
        Pageable pageableRequest;
        pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
        qry.with(pageableRequest);
        if (!sortParam.equals("")) {
            qry.with(new Sort(Sort.Direction.ASC, sortParam));
        }
        List<PractitionerEntity> practitionerResults = mongo.find(qry, PractitionerEntity.class);
        for (PractitionerEntity practitionerEntity : practitionerResults) {
            resources.add(practitionerEntityToFHIRPractitioner.transform(practitionerEntity));
        }

        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, StringParam address,
            StringParam addressCity, StringParam addressCountry, StringParam addressPostalCode,
            StringParam addressState, TokenParam addressUse, TokenParam communication, TokenParam email,
            StringParam family, TokenParam gender, StringParam given, TokenParam identifier, StringParam name,
            TokenParam phone, StringParam phonetic, TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content,
            ReferenceParam managingOrg) {
        Criteria criteria = null;
        criteria = setParamToCriteria(active, address, addressCity, addressCountry, addressPostalCode, addressState,
                addressUse, communication, email, family, gender, given, identifier, name, phone, phonetic, telecom,
                resid, _lastUpdated, _tag, _profile, _query, _security, _content, managingOrg);
        long count = 0;
        if (criteria != null) {
            Query qry = Query.query(criteria);
            count = mongo.count(qry, PractitionerEntity.class);
        } else {
            Query query = new Query();
            count = mongo.count(query, PractitionerEntity.class);
        }
        return count;
    }

    private Criteria setParamToCriteria(TokenParam active, StringParam address, StringParam addressCity,
            StringParam addressCountry, StringParam addressPostalCode, StringParam addressState, TokenParam addressUse,
            TokenParam communication, TokenParam email, StringParam family, TokenParam gender, StringParam given,
            TokenParam identifier, StringParam name, TokenParam phone, StringParam phonetic, TokenParam telecom,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content, ReferenceParam managingOrg) {
        Criteria criteria = null;

        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active.getValue());
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        // address
        if (address != null && !address.isEmpty()) {
            criteria.and("address").regex(address.getValue());
        }
        // addressCity
        if (addressCity != null && !addressCity.isEmpty()) {
            criteria.and("address.city").regex(addressCity.getValue());
        }
        // addressCountry
        if (addressCountry != null && !addressCountry.isEmpty()) {
            criteria.and("address.country").regex(addressCountry.getValue());
        }
        // addressPostalCode
        if (addressPostalCode != null && !addressPostalCode.isEmpty()) {
            criteria.and("address.postalCode").is(addressPostalCode.getValue());
        }
        // addressState
        if (addressState != null && !addressState.isEmpty()) {
            criteria.and("address.state").regex(addressState.getValue());
        }
        // addressUse
        if (addressUse != null && !addressUse.isEmpty()) {
            criteria.and("address.use").is(addressUse.getValue());
        }
        // communication
        if (communication != null && !communication.isEmpty()) {
            criteria.and("communication").is(communication.getValue());
        }
        // email
        if (email != null && !email.isEmpty()) {
            criteria.and("telecoms.system").is("email").and("telecoms.value").is(email.getValue());
        }
        // family
        if (family != null && !family.isEmpty()) {
            criteria.and("name.family").regex(family.getValue());
        }
        // gender
        if (gender != null && !gender.isEmpty()) {
            criteria.and("gender").is(gender.getValue());
        }
        // given
        if (given != null && !given.isEmpty()) {
            criteria.and("name.given").regex(given.getValue());
        }
        // name
        if (name != null && !name.isEmpty()) {
            criteria.and("name").regex(name.getValue());
        }
        // phone
        if (phone != null && !phone.isEmpty()) {
            criteria.and("telecoms.system").is("phone").and("telecoms.value").is(phone.getValue());
        }
        // phonetic
        if (phonetic != null && !phonetic.isEmpty()) {
            criteria.and("name").regex(phonetic.getValue());
        }
        // phonetic
        if (telecom != null && !phonetic.isEmpty()) {
            criteria.and("telecom").is(phonetic.getValue());
        }
        if (managingOrg != null) {
            criteria.and("extension.url.myStringValue")
                    .is("https://fhir.yte360.com/StructureDefinition/Extension-ManagingOrgPractitioner-v1.0");
            if(managingOrg.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("extension.value.reference.myStringValue").regex(managingOrg.getValue()),
                        Criteria.where("extension.value.display").regex(managingOrg.getValue()));
            }else {
                String[] ref= managingOrg.getValue().split("\\|");
                criteria.and("extension.value.identifier.system.myStringValue").is(ref[0]).and("extension.value.identifier.value.myStringValue").is(ref[1]);
            }
        }
        return criteria;
    }

    private PractitionerEntity createNewPractitionerEntity(Practitioner obj, int version, String fhirId) {
        var ent = PractitionerEntity.fromPractitioner(obj);
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
}
