package vn.ehealth.hl7.fhir.patient.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.RelatedPerson;
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
import vn.ehealth.hl7.fhir.patient.entity.RelatedPersonEntity;

@Repository
public class RelatedPersonDao extends BaseDao<RelatedPersonEntity, RelatedPerson> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext ctx, StringParam address, StringParam addressCity,
			StringParam addressCountry, StringParam addressState, DateRangeParam birthDate, TokenParam email,
			StringParam gender, TokenParam identifier, StringParam name, ReferenceParam patient, TokenParam phone,
			StringParam phonetic, TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
			String sortParam, Integer count) {

		List<Resource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(address, addressCity, addressCountry, addressState, birthDate, email,
				gender, identifier, name, patient, phone, phonetic, telecom, resid, _lastUpdated, _tag, _profile,
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
			List<RelatedPersonEntity> objResults = mongo.find(query, RelatedPersonEntity.class);

			for (RelatedPersonEntity objEntity : objResults) {
				resources.add(transform(objEntity));
			}
		}

		return resources;
	}

	public long findMatchesAdvancedTotal(FhirContext ctx, StringParam address, StringParam addressCity,
			StringParam addressCountry, StringParam addressState, DateRangeParam birthDate, TokenParam email,
			StringParam gender, TokenParam identifier, StringParam name, ReferenceParam patient, TokenParam phone,
			StringParam phonetic, TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {

		Criteria criteria = setParamToCriteria(address, addressCity, addressCountry, addressState, birthDate, email,
				gender, identifier, name, patient, phone, phonetic, telecom, resid, _lastUpdated, _tag, _profile,
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

	private Criteria setParamToCriteria(StringParam address, StringParam addressCity, StringParam addressCountry,
			StringParam addressState, DateRangeParam birthDate, TokenParam email, StringParam gender,
			TokenParam identifier, StringParam name, ReferenceParam patient, TokenParam phone, StringParam phonetic,
			TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		criteria = Criteria.where("active").is(true);
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

	@Override
	protected String getProfile() {
		return "RelatedPerson-v1.0";
	}

	@Override
    protected Class<? extends DomainResource> getResourceClass() {
        return RelatedPerson.class;
    }

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return RelatedPersonEntity.class;
	}
}
