package vn.ehealth.hl7.fhir.provider.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.provider.entity.HealthcareServiceEntity;

@Repository
public class HealthcareServiceDao extends BaseDao<HealthcareServiceEntity, HealthcareService> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext fhirContext, TokenParam category, TokenParam characteristic,
			ReferenceParam endpoint, TokenParam identifier, ReferenceParam location, StringParam name,
			ReferenceParam organization, StringParam programname, TokenParam type,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count) {
		List<Resource> resources = new ArrayList<>();
		Criteria criteria = null;
		criteria = setParamToCriteria(category, characteristic, endpoint, identifier, location, name, organization,
				programname, type, resid, _lastUpdated, _tag, _profile, _query, _security, _content);

		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		Pageable pageableRequest;
		pageableRequest = new PageRequest(
				_page != null ? Integer.valueOf(_page.getValue().intValue()) : ConstantKeys.PAGE,
				count != null ? count : ConstantKeys.DEFAULT_PAGE_SIZE);
		query.with(pageableRequest);
		if (sortParam != null && !sortParam.equals("")) {
			query.with(new Sort(Sort.Direction.DESC, sortParam));
		} else {
			query.with(new Sort(Sort.Direction.DESC, ConstantKeys.QP_UPDATED));
			query.with(new Sort(Sort.Direction.DESC, ConstantKeys.QP_CREATED));
		}
		List<HealthcareServiceEntity> healthcareServiceResults = mongo.find(query, HealthcareServiceEntity.class);
		for (HealthcareServiceEntity healthcareServiceEntity : healthcareServiceResults) {
			resources.add(transform(healthcareServiceEntity));
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam category, TokenParam characteristic,
			ReferenceParam endpoint, TokenParam identifier, ReferenceParam location, StringParam name,
			ReferenceParam organization, StringParam programname, TokenParam type, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {
		Criteria criteria = null;
		criteria = setParamToCriteria(category, characteristic, endpoint, identifier, location, name, organization,
				programname, type, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		long count = 0;
		if (criteria != null) {
			Query qry = Query.query(criteria);
			count = mongo.count(qry, HealthcareServiceEntity.class);
		} else {
			Query query = new Query();
			count = mongo.count(query, HealthcareServiceEntity.class);
		}
		return count;
	}

	private Criteria setParamToCriteria(TokenParam category, TokenParam characteristic, ReferenceParam endpoint,
			TokenParam identifier, ReferenceParam location, StringParam name, ReferenceParam organization,
			StringParam programname, TokenParam type, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		criteria = Criteria.where("$where").is("1==1");
		// default
		criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				identifier);
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		// category
		if (category != null) {
			criteria.and("category").regex(category.getValue());
		}
		// characteristic
		if (characteristic != null) {
			criteria.and("characteristic").regex(characteristic.getValue());
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
		// name
		if (name != null && !name.isEmpty()) {
			criteria.and("name").regex(name.getValue());
		}
		// organization
		if (organization != null) {
			criteria.orOperator(Criteria.where("providedBy.reference").regex(organization.getValue()),
					Criteria.where("providedBy.display").regex(organization.getValue()),
					Criteria.where("providedBy.identifier.value").regex(organization.getValue()),
					Criteria.where("providedBy.identifier.system").regex(organization.getValue()));
		}
		// programName
		if (programname != null) {
			criteria.and("programName").regex(characteristic.getValue());
		}
		// type
		if (type != null) {
			criteria.and("type").regex(type.getValue());
		}

		return criteria;
	}

	@Override
	protected List<String> getProfile() {
		return null;
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return HealthcareService.class;
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return HealthcareServiceEntity.class;
	}

}
