package vn.ehealth.hl7.fhir.provider.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
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
import vn.ehealth.hl7.fhir.provider.dao.IOrganization;
import vn.ehealth.hl7.fhir.provider.dao.transform.OrganizationEntityToFHIROrganization;
import vn.ehealth.hl7.fhir.provider.entity.OrganizationEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Repository
public class OrganizationDao implements IOrganization {

    @Autowired
    MongoOperations mongo;

    @Autowired
    OrganizationEntityToFHIROrganization organizationEntityToFHIROrganization;

    @Override
    public Organization create(FhirContext fhirContext, Organization object) {
        OrganizationEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewOrganizationEntity(object, version, null);
            // save OrganizationEntity database
            mongo.save(entity);
            return organizationEntityToFHIROrganization.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "organization", key = "#idType")
    public Organization update(FhirContext fhirContext, Organization object, IdType idType) {
        OrganizationEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, OrganizationEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove OrganizationEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save OrganizationEntity
            int version = entityOld.version + 1;
            if (object != null) {
                OrganizationEntity entity = createNewOrganizationEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return organizationEntityToFHIROrganization.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "organization", key = "#idType")
    public Organization read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            OrganizationEntity entity = mongo.findOne(query, OrganizationEntity.class);
            if (entity != null) {
                return organizationEntityToFHIROrganization.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "organization", key = "#idType")
    public Organization remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            OrganizationEntity entity = mongo.findOne(query, OrganizationEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return organizationEntityToFHIROrganization.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Organization readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                OrganizationEntity entity = mongo.findOne(query, OrganizationEntity.class);
                if (entity != null) {
                    return organizationEntityToFHIROrganization.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, StringParam address,
            StringParam addressCity, StringParam addressCountry, StringParam addressPostalCode,
            StringParam addressState, TokenParam addressUse, ReferenceParam endpoint, TokenParam identifier,
            StringParam name, ReferenceParam partof, StringParam phonetic, TokenParam type, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, address, addressCity, addressCountry, addressPostalCode,
                addressState, addressUse, endpoint, identifier, name, partof, phonetic, type, resid, _lastUpdated, _tag,
                _profile, _query, _security, _content);
        if (criteria != null) {
            Query qry = Query.query(criteria);
            Pageable pageableRequest;
            pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                    count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
            qry.with(pageableRequest);
            if (!sortParam.equals("")) {
                qry.with(new Sort(Sort.Direction.ASC, sortParam));
            }
            List<OrganizationEntity> organizationResults = mongo.find(qry, OrganizationEntity.class);
            for (OrganizationEntity organizationEntity : organizationResults) {
                resources.add(organizationEntityToFHIROrganization.transform(organizationEntity));
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, StringParam address,
            StringParam addressCity, StringParam addressCountry, StringParam addressPostalCode,
            StringParam addressState, TokenParam addressUse, ReferenceParam endpoint, TokenParam identifier,
            StringParam name, ReferenceParam partof, StringParam phonetic, TokenParam type, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        Criteria criteria = null;
        criteria = setParamToCriteria(active, address, addressCity, addressCountry, addressPostalCode, addressState,
                addressUse, endpoint, identifier, name, partof, phonetic, type, resid, _lastUpdated, _tag, _profile,
                _query, _security, _content);

        long count = 0;
        if (criteria != null) {
            Query qry = Query.query(criteria);
            count = mongo.count(qry, OrganizationEntity.class);
        } else {
            Query query = new Query();
            count = mongo.count(query, OrganizationEntity.class);
        }
        return count;
    }

    private Criteria setParamToCriteria(TokenParam active, StringParam address, StringParam addressCity,
            StringParam addressCountry, StringParam addressPostalCode, StringParam addressState, TokenParam addressUse,
            ReferenceParam endpoint, TokenParam identifier, StringParam name, ReferenceParam partof,
            StringParam phonetic, TokenParam type, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        Criteria criteria = null;
        if (active != null && !active.isEmpty()) {
            criteria = Criteria.where("active").is(active.getValue());
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        if (address != null && !address.isEmpty()) {
            criteria.and("address").regex(address.getValue());
        }
        if (addressCity != null && !addressCity.isEmpty()) {
            criteria.and("address.city").regex(addressCity.getValue());
        }
        if (addressCountry != null && !addressCountry.isEmpty()) {
            criteria.and("address.country").regex(addressCountry.getValue());
        }
        if (addressPostalCode != null && !addressPostalCode.isEmpty()) {
            criteria.and("address.postalCode").regex(addressPostalCode.getValue());
        }
        if (addressState != null && !addressState.isEmpty()) {
            criteria.and("address.state").regex(addressState.getValue());
        }
        if (addressUse != null && !addressUse.isEmpty()) {
            criteria.and("address.use").is(addressUse.getValue());
        }
        if (endpoint != null) {
            criteria.and("endpoint.reference.myStringValue").is(endpoint.getValue());
        }
        
        if (name != null && !name.isEmpty()) {
            criteria.and("name").regex(name.getValue());
        }
        if (partof != null) {
            if(partof.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("partOf.reference").regex(partof.getValue()),
                        Criteria.where("partOf.display").regex(partof.getValue()));
            }else {
                String[] ref= partof.getValue().split("\\|");
                criteria.and("partOf.identifier.system").is(ref[0]).and("partOf.identifier.value").is(ref[1]);
            }
            
        }
        if (phonetic != null && !phonetic.isEmpty()) {
            criteria.and("name").regex(phonetic.getValue());
        }
        if (type != null) {
            criteria.and("type.coding.code.myStringValue").is(type.getValue());
        }
        return criteria;
    }

    private OrganizationEntity createNewOrganizationEntity(Organization obj, int version, String fhirId) {
        var ent = OrganizationEntity.fromOrganization(obj);
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
