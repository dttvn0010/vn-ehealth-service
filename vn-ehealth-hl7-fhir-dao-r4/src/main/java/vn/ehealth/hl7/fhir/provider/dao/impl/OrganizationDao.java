package vn.ehealth.hl7.fhir.provider.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.provider.entity.OrganizationEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Repository
public class OrganizationDao extends BaseDao<OrganizationEntity, Organization> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext fhirContext, StringParam address, StringParam addressCity,
			StringParam addressCountry, StringParam addressPostalCode, StringParam addressState, TokenParam addressUse,
			ReferenceParam endpoint, TokenParam identifier, StringParam name, ReferenceParam partof,
			StringParam phonetic, TokenParam type, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
			String sortParam, Integer count) {
		List<Resource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(address, addressCity, addressCountry, addressPostalCode, addressState,
				addressUse, endpoint, identifier, name, partof, phonetic, type, resid, _lastUpdated, _tag, _profile,
				_query, _security, _content);
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
			List<OrganizationEntity> organizationResults = mongo.find(query, OrganizationEntity.class);
			for (OrganizationEntity organizationEntity : organizationResults) {
				resources.add(transform(organizationEntity));
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, StringParam address, StringParam addressCity,
			StringParam addressCountry, StringParam addressPostalCode, StringParam addressState, TokenParam addressUse,
			ReferenceParam endpoint, TokenParam identifier, StringParam name, ReferenceParam partof,
			StringParam phonetic, TokenParam type, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		criteria = setParamToCriteria(address, addressCity, addressCountry, addressPostalCode, addressState, addressUse,
				endpoint, identifier, name, partof, phonetic, type, resid, _lastUpdated, _tag, _profile, _query,
				_security, _content);

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

	private Criteria setParamToCriteria(StringParam address, StringParam addressCity, StringParam addressCountry,
			StringParam addressPostalCode, StringParam addressState, TokenParam addressUse, ReferenceParam endpoint,
			TokenParam identifier, StringParam name, ReferenceParam partof, StringParam phonetic, TokenParam type,
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		criteria = Criteria.where("active").is(true);
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
			if (partof.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("partOf.reference").regex(partof.getValue()),
						Criteria.where("partOf.display").regex(partof.getValue()));
			} else {
				String[] ref = partof.getValue().split("\\|");
				criteria.and("partOf.identifier.system").is(ref[0]).and("partOf.identifier.value").is(ref[1]);
			}

		}
		if (phonetic != null && !phonetic.isEmpty()) {
			criteria.and("name").regex(phonetic.getValue());
		}
		if (type != null) {
			criteria.and("type.coding.code").is(type.getValue()).and("type.coding.system").is(type.getSystem());
		}
		return criteria;
	}

	@Override
	protected String getProfile() {
		return "Organization-v1.0";
	}

	@Override
    public Class<? extends DomainResource> getResourceClass() {
        return Organization.class;
    }

	@Override
	public Class<? extends BaseResource> getEntityClass() {
		return OrganizationEntity.class;
	}
}
