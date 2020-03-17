package vn.ehealth.hl7.fhir.provider.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.PractitionerRole;
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
import vn.ehealth.hl7.fhir.provider.dao.IPractitionerRole;
import vn.ehealth.hl7.fhir.provider.dao.transform.PractitionerRoleEntityToFHIRPractitionerRole;
import vn.ehealth.hl7.fhir.provider.entity.PractitionerRoleEntity;

@Repository
public class PractitionerRoleDao implements IPractitionerRole {

    @Autowired
    MongoOperations mongo;

    @Autowired
    PractitionerRoleEntityToFHIRPractitionerRole practitionerRoleEntityToFHIRPractitionerRole;

    @Override
    public PractitionerRole create(FhirContext fhirContext, PractitionerRole object) {
        PractitionerRoleEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewPractitionerRoleEntity(object, version, null);
            // save PractitionerRoleEntity database
            mongo.save(entity);
            return practitionerRoleEntityToFHIRPractitionerRole.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "practitionerRole", key = "#idType")
    public PractitionerRole update(FhirContext fhirContext, PractitionerRole object, IdType idType) {
        PractitionerRoleEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, PractitionerRoleEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove PractitionerRoleEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save PractitionerRoleEntity
            int version = entityOld.version + 1;
            if (object != null) {
                PractitionerRoleEntity entity = createNewPractitionerRoleEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return practitionerRoleEntityToFHIRPractitionerRole.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "practitionerRole", key = "#idType")
    public PractitionerRole read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            PractitionerRoleEntity entity = mongo.findOne(query, PractitionerRoleEntity.class);
            if (entity != null) {
                return practitionerRoleEntityToFHIRPractitionerRole.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "practitionerRole", key = "#idType")
    public PractitionerRole remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            PractitionerRoleEntity entity = mongo.findOne(query, PractitionerRoleEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return practitionerRoleEntityToFHIRPractitionerRole.transform(entity);
            }
        }
        return null;
    }

    @Override
    public PractitionerRole readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                PractitionerRoleEntity entity = mongo.findOne(query, PractitionerRoleEntity.class);
                if (entity != null) {
                    return practitionerRoleEntityToFHIRPractitionerRole.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, DateRangeParam date, TokenParam email,
            ReferenceParam endpoint, TokenParam identifier, ReferenceParam location, ReferenceParam organization,
            TokenParam phone, ReferenceParam practitioner, TokenParam role, ReferenceParam service,
            TokenParam specialty, TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
            String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = null;
        criteria = setParamToCriteria(active, date, email, endpoint, identifier, location, organization, phone,
                practitioner, role, service, specialty, telecom, resid, _lastUpdated, _tag, _profile, _query, _security,
                _content);
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
        List<PractitionerRoleEntity> practitionerRoleResults = mongo.find(qry, PractitionerRoleEntity.class);
        for (PractitionerRoleEntity practitionerRoleEntity : practitionerRoleResults) {
            resources.add(practitionerRoleEntityToFHIRPractitionerRole.transform(practitionerRoleEntity));
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, DateRangeParam date,
            TokenParam email, ReferenceParam endpoint, TokenParam identifier, ReferenceParam location,
            ReferenceParam organization, TokenParam phone, ReferenceParam practitioner, TokenParam role,
            ReferenceParam service, TokenParam specialty, TokenParam telecom, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        Criteria criteria = null;
        criteria = setParamToCriteria(active, date, email, endpoint, identifier, location, organization, phone,
                practitioner, role, service, specialty, telecom, resid, _lastUpdated, _tag, _profile, _query, _security,
                _content);
        long count = 0;
        if (criteria != null) {
            Query qry = Query.query(criteria);
            count = mongo.count(qry, PractitionerRoleEntity.class);
        } else {
            Query query = new Query();
            count = mongo.count(query, PractitionerRoleEntity.class);
        }
        return count;
    }

    private PractitionerRoleEntity createNewPractitionerRoleEntity(PractitionerRole obj, int version,
            String fhirId) {
        var ent = PractitionerRoleEntity.fromPractitionerRole(obj);
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

    private Criteria setParamToCriteria(TokenParam active, DateRangeParam date, TokenParam email,
            ReferenceParam endpoint, TokenParam identifier, ReferenceParam location, ReferenceParam organization,
            TokenParam phone, ReferenceParam practitioner, TokenParam role, ReferenceParam service,
            TokenParam specialty, TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
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
        // date
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "period", date);
        }
        // email
        if (email != null) {
            criteria.and("telecom.system").is("EMAIL").and("telecom.value").is(email.getValue());
        }
        // endpoint
        if (endpoint != null) {
            criteria.orOperator(Criteria.where("endpoint.reference").regex(endpoint.getValue()),
                    Criteria.where("endpoint.display").regex(endpoint.getValue()),
                    Criteria.where("endpoint.identifier.value").regex(endpoint.getValue()),
                    Criteria.where("endpoint.identifier.system").regex(endpoint.getValue()));
        }
        // identifier
        if (identifier != null) {
            criteria.and("identifier.system").is(identifier.getSystem()).and("identifier.value")
                    .is(identifier.getValue());
        }
        // location
        if (location != null) {
            criteria.orOperator(Criteria.where("location.reference").regex(location.getValue()),
                    Criteria.where("location.display").regex(location.getValue()),
                    Criteria.where("location.identifier.value").regex(location.getValue()),
                    Criteria.where("location.identifier.system").regex(location.getValue()));
        }
        // organization
        if (organization != null) {
            criteria.orOperator(Criteria.where("organization.reference").regex(organization.getValue()),
                    Criteria.where("organization.display").regex(organization.getValue()),
                    Criteria.where("organization.identifier.value").regex(organization.getValue()),
                    Criteria.where("organization.identifier.system").regex(organization.getValue()));
        }
        // phone
        if (phone != null) {
            criteria.and("telecoms.system").is("PHONE").and("telecoms.value").is(phone.getValue());
        }
        // practitioner
        if (practitioner != null) {
            criteria.orOperator(Criteria.where("practitioner.reference").regex(practitioner.getValue()),
                    Criteria.where("practitioner.display").regex(practitioner.getValue()),
                    Criteria.where("practitioner.identifier.value").regex(practitioner.getValue()),
                    Criteria.where("practitioner.identifier.system").regex(practitioner.getValue()));
        }
        // role
        if (role != null) {
            criteria.and("code").regex(role.getValue());
        }
        // service
        if (service != null) {
            criteria.orOperator(Criteria.where("healthcareService.reference").regex(service.getValue()),
                    Criteria.where("healthcareService.display").regex(service.getValue()),
                    Criteria.where("healthcareService.identifier.value").regex(service.getValue()),
                    Criteria.where("healthcareService.identifier.system").regex(service.getValue()));
        }
        // specialty
        if (specialty != null) {
            criteria.and("specialty").regex(specialty.getValue());
        }
        // telecom
        if (telecom != null) {
            criteria.and("telecom").regex(telecom.getValue());
        }
        return criteria;
    }
}
