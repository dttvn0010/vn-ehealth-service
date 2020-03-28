package vn.ehealth.hl7.fhir.provider.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.PractitionerRole;
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
import vn.ehealth.hl7.fhir.provider.entity.PractitionerRoleEntity;

@Repository
public class PractitionerRoleDao extends BaseDao<PractitionerRoleEntity, PractitionerRole> {
	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext fhirContext, DateRangeParam date, TokenParam email,
			ReferenceParam endpoint, TokenParam identifier, ReferenceParam location, ReferenceParam organization,
			TokenParam phone, ReferenceParam practitioner, TokenParam role, ReferenceParam service,
			TokenParam specialty, TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
			String sortParam, Integer count) {
		List<Resource> resources = new ArrayList<>();
		Criteria criteria = null;
		criteria = setParamToCriteria(date, email, endpoint, identifier, location, organization, phone, practitioner,
				role, service, specialty, telecom, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
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
		List<PractitionerRoleEntity> practitionerRoleResults = mongo.find(query, PractitionerRoleEntity.class);
		for (PractitionerRoleEntity practitionerRoleEntity : practitionerRoleResults) {
			resources.add(transform(practitionerRoleEntity));
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, DateRangeParam date, TokenParam email,
			ReferenceParam endpoint, TokenParam identifier, ReferenceParam location, ReferenceParam organization,
			TokenParam phone, ReferenceParam practitioner, TokenParam role, ReferenceParam service,
			TokenParam specialty, TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		criteria = setParamToCriteria(date, email, endpoint, identifier, location, organization, phone, practitioner,
				role, service, specialty, telecom, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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

	private Criteria setParamToCriteria(DateRangeParam date, TokenParam email, ReferenceParam endpoint,
			TokenParam identifier, ReferenceParam location, ReferenceParam organization, TokenParam phone,
			ReferenceParam practitioner, TokenParam role, ReferenceParam service, TokenParam specialty,
			TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where("active").is(true);
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

	@Override
	protected String getProfile() {
		return "Practitioner-v1.0";
	}

	@Override
	protected PractitionerRoleEntity fromFhir(PractitionerRole obj) {
		return PractitionerRoleEntity.fromPractitionerRole(obj);
	}

	@Override
	protected PractitionerRole toFhir(PractitionerRoleEntity ent) {
		return PractitionerRoleEntity.toPractitionerRole(ent);
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return PractitionerRoleEntity.class;
	}
}
