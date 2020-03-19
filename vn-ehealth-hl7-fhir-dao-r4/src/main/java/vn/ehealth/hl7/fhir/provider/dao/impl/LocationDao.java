package vn.ehealth.hl7.fhir.provider.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.provider.entity.LocationEntity;

@Repository
public class LocationDao extends BaseDao<LocationEntity, Location> {
      
    @SuppressWarnings("deprecation")
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
            Query query = Query.query(criteria);
            Pageable pageableRequest;
            pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                    count != null ? count : ConstantKeys.DEFAULT_PAGE_SIZE);
            query.with(pageableRequest);
    		if (sortParam != null && !sortParam.equals("")) {
    			query.with(new Sort(Sort.Direction.DESC, sortParam));
    		} else {
    			query.with(new Sort(Sort.Direction.DESC, "resUpdated"));
    			query.with(new Sort(Sort.Direction.DESC, "resCreated"));
    		}
            List<LocationEntity> locationResults = mongo.find(query, LocationEntity.class);
            for (LocationEntity locationEntity : locationResults) {
                resources.add(transform(locationEntity));
            }
        }
        return resources;
    }

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

    @Override
    protected String getProfile() {
        return "Location-v1.0";
    }

    @Override
    protected LocationEntity fromFhir(Location obj) {
        return LocationEntity.fromLocation(obj);
    }

    @Override
    protected Location toFhir(LocationEntity ent) {
        return LocationEntity.toLocation(ent);
    }

    @Override
    protected Class<? extends BaseResource> getEntityClass() {
        return LocationEntity.class;
    }
}
