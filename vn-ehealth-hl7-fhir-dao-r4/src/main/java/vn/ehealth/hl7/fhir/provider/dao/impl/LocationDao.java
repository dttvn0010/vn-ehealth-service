package vn.ehealth.hl7.fhir.provider.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Location.LocationStatus;
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
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.provider.dao.ILocation;
import vn.ehealth.hl7.fhir.provider.dao.transform.LocationEntityToFHIRLocation;
import vn.ehealth.hl7.fhir.provider.entity.LocationEntity;

@Repository
public class LocationDao implements ILocation {

    @Autowired
    MongoOperations mongo;

    @Autowired
    LocationEntityToFHIRLocation locationEntityToFHIRLocation;

    @Override
    public Location create(FhirContext fhirContext, Location object) {
        LocationEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewLocationEntity(object, version, null);
            // save LocationEntity database
            mongo.save(entity);
            return locationEntityToFHIRLocation.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "location", key = "#idType")
    public Location update(FhirContext fhirContext, Location object, IdType idType) {
        LocationEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, LocationEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove LocationEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            entityOld.status = (LocationStatus.INACTIVE.toCode());
            mongo.save(entityOld);
            // save LocationEntity
            int version = entityOld.version + 1;
            if (object != null) {
                LocationEntity entity = createNewLocationEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return locationEntityToFHIRLocation.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "location", key = "#idType")
    public Location read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            LocationEntity entity = mongo.findOne(query, LocationEntity.class);
            if (entity != null) {
                return locationEntityToFHIRLocation.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Location readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                LocationEntity entity = mongo.findOne(query, LocationEntity.class);
                if (entity != null) {
                    return locationEntityToFHIRLocation.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "location", key = "#idType")
    public Location remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            LocationEntity entity = mongo.findOne(query, LocationEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                entity.status = (LocationStatus.INACTIVE.toCode());
                mongo.save(entity);
                return locationEntityToFHIRLocation.transform(entity);
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext ctx, StringParam address, StringParam addressCity,
            StringParam addressCountry, StringParam addressState, ReferenceParam endpoint, TokenParam identifier,
            StringParam name, TokenParam near, QuantityParam nearDistance, TokenParam operationalStatus,
            ReferenceParam organization, ReferenceParam partof, TokenParam status, TokenParam type, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content, StringParam _page, String sortParam, Integer count) {

        List<Resource> resources = new ArrayList<>();

        Criteria criteria = null;
        if (status != null) {
            criteria = Criteria.where("status").is(status.getValue());
        } else {
            criteria = Criteria.where("active").is(true);
        }
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        if (address != null) {
            criteria.orOperator(Criteria.where("addresses.addressLine1.myStringValue").regex(address.getValue()),
                    Criteria.where("addresses.addressLine2.myStringValue").regex(address.getValue()));
        }

        if (addressCity != null) {
            criteria.and("addresses.city").regex(addressCity.getValue());
        }
        if (addressCountry != null) {
            criteria.and("addresses.country").regex(addressCountry.getValue());
        }
        if (addressState != null) {
            criteria.and("addresses.state").regex(addressState.getValue());
        }
//        if(endpoint != null) {
//            criteria.and("addresses.state").regex(addressState.getValue());
//        }
        if (name != null) {
            criteria.orOperator(Criteria.where("name").regex(name.getValue()),
                    Criteria.where("alias").regex(name.getValue()));
        }

        if (near != null) {
            criteria.and("position").is(near.getValue());
        }
        if (nearDistance != null) {
            criteria.and("position").is(nearDistance.getSystem());
        }
        if (operationalStatus != null) {
            criteria.and("operationalStatus.system").is(operationalStatus.getValue());
        }
        if (organization != null) {
            if(organization.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("managingOrganization.reference").is(partof.getValue()),
                        Criteria.where("managingOrganization.display").is(organization.getValue()));
            }else {
                String[] ref= organization.getValue().split("\\|");
                criteria.and("managingOrganization.identifier.system").is(ref[0]).and("managingOrganization.identifier.value").is(ref[1]);
            }
        }
        if (type != null) {
            criteria.and("type").is(type.getValue());
        }
        if (partof != null) {
            if(partof.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("partof.reference").is(partof.getValue()),
                        Criteria.where("partof.display").is(partof.getValue()));
            }else {
                String[] ref= partof.getValue().split("\\|");
                criteria.and("partof.identifier.system").is(ref[0]).and("partof.identifier.value").is(ref[1]);
            }
        }

        if (criteria != null) {
            Query qry = Query.query(criteria);
            Pageable pageableRequest;
            pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                    count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
            qry.with(pageableRequest);
            if (!sortParam.equals("")) {
                qry.with(new Sort(Sort.Direction.ASC, sortParam));
            }
            List<LocationEntity> locationResults = mongo.find(qry, LocationEntity.class);
            for (LocationEntity locationEntity : locationResults) {
                resources.add(locationEntityToFHIRLocation.transform(locationEntity));
            }
        }
        return resources;
    }

    @Override
    public long findMatchesAdvancedTotal(FhirContext ctx, StringParam address, StringParam addressCity,
            StringParam addressCountry, StringParam addressState, ReferenceParam endpoint, TokenParam identifier,
            StringParam name, TokenParam near, QuantityParam nearDistance, TokenParam operationalStatus,
            ReferenceParam organization, ReferenceParam partof, TokenParam status, TokenParam type, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {

        Criteria criteria = null;
        if (status != null) {
            criteria = Criteria.where("status").is(status.getValue());
        } else {
            criteria = Criteria.where("status").is(true);
        }
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        if (address != null) {
            criteria.orOperator(Criteria.where("addresses.addressLine1.myStringValue").regex(address.getValue()),
                    Criteria.where("addresses.addressLine2.myStringValue").regex(address.getValue()));
        }

        if (addressCity != null) {
            criteria.and("addresses.city").regex(addressCity.getValue());
        }
        if (addressCountry != null) {
            criteria.and("addresses.country").regex(addressCountry.getValue());
        }
        if (addressState != null) {
            criteria.and("addresses.state").regex(addressState.getValue());
        }
//        if(endpoint != null) {
//            criteria.and("addresses.state").regex(addressState.getValue());
//        }
        if (name != null) {
            criteria.orOperator(Criteria.where("name").regex(name.getValue()),
                    Criteria.where("alias").regex(name.getValue()));
        }

        if (near != null) {
            criteria.and("position").is(near.getValue());
        }
        if (nearDistance != null) {
            criteria.and("position").is(nearDistance.getSystem());
        }
        if (operationalStatus != null) {
            criteria.and("operationalStatus.system").is(operationalStatus.getValue());
        }
        if (organization != null) {
            if(organization.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("managingOrganization.reference").regex(organization.getValue()),
                        Criteria.where("managingOrganization.display").regex(organization.getValue()));
            }else {
                String[] ref= organization.getValue().split("\\|");
                criteria.and("managingOrganization.identifier.system").is(ref[0]).and("managingOrganization.identifier.value").is(ref[1]);
            }
        }
        if (type != null) {
            criteria.and("type").is(type.getValue());
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

        long count = 0;
        if (criteria != null) {
            Query qry = Query.query(criteria);
            count = mongo.count(qry, LocationEntity.class);
        } else {
            Query query = new Query();
            count = mongo.count(query, LocationEntity.class);
        }
        return count;
    }

    private LocationEntity createNewLocationEntity(Location obj, int version, String fhirId) {
        var ent = LocationEntity.fromLocation(obj);
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
