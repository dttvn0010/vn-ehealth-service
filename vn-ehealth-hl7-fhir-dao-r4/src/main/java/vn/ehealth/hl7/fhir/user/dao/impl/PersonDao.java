package vn.ehealth.hl7.fhir.user.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Person;
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
import vn.ehealth.hl7.fhir.user.entity.PersonEntity;

@Repository
public class PersonDao extends BaseDao<PersonEntity, Person> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext ctx, StringParam address, StringParam addressCity,
			StringParam addressCountry, StringParam addressState, DateRangeParam birthDate, TokenParam email,
			StringParam gender, TokenParam identifier, StringParam name, ReferenceParam patient, TokenParam phone,
			StringParam phonetic, TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content,
			ReferenceParam managingOrg, StringParam _page, String sortParam, Integer count) {

		List<Resource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(address, addressCity, addressCountry, addressState, birthDate, email,
				gender, identifier, name, patient, phone, phonetic, telecom, resid, _lastUpdated, _tag, _profile,
				_query, _security, _content, managingOrg);

		if (criteria != null) {
			Query qry = Query.query(criteria);
			Pageable pageableRequest;
			pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
					count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
			qry.with(pageableRequest);
			if (!sortParam.equals("")) {
				qry.with(new Sort(Sort.Direction.ASC, sortParam));
			}
			List<PersonEntity> objResults = mongo.find(qry, PersonEntity.class);

			for (PersonEntity objEntity : objResults) {
				resources.add(transform(objEntity));
			}
		}

		return resources;
	}

	public long findMatchesAdvancedTotal(FhirContext ctx, StringParam address, StringParam addressCity,
			StringParam addressCountry, StringParam addressState, DateRangeParam birthDate, TokenParam email,
			StringParam gender, TokenParam identifier, StringParam name, ReferenceParam patient, TokenParam phone,
			StringParam phonetic, TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content,
			ReferenceParam managingOrg) {

		Criteria criteria = setParamToCriteria(address, addressCity, addressCountry, addressState, birthDate, email,
				gender, identifier, name, patient, phone, phonetic, telecom, resid, _lastUpdated, _tag, _profile,
				_query, _security, _content, managingOrg);
		long count = 0;
		if (criteria != null) {
			Query qry = Query.query(criteria);
			count = mongo.count(qry, PersonEntity.class);
		} else {
			Query query = new Query();
			count = mongo.count(query, PersonEntity.class);
		}
		return count;
	}

	private Criteria setParamToCriteria(StringParam address, StringParam addressCity, StringParam addressCountry,
			StringParam addressState, DateRangeParam birthDate, TokenParam email, StringParam gender,
			TokenParam identifier, StringParam name, ReferenceParam patient, TokenParam phone, StringParam phonetic,
			TokenParam telecom, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content, ReferenceParam managingOrg) {
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
			criteria.and("telecoms.system").is("PHONE").and("telecoms.phone").is(phone.getValue());
		}
		if (email != null) {
			criteria.and("telecoms.system").is("EMAIL").and("telecoms.value").is(email.getValue());
		}
		if (name != null) {
			String regexName = name.getValue(); // .toLowerCase()+".*"; // use options = i for regex
			criteria.orOperator(Criteria.where("name.family").regex(regexName),
					Criteria.where("name.given").regex(regexName));
		}
		if (managingOrg != null) {
			if (managingOrg.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("reference.myStringValue").regex(managingOrg.getValue()),
						Criteria.where("display").regex(managingOrg.getValue()));
			} else {
				String[] ref = managingOrg.getValue().split("\\|");
				criteria.and("identifier.system.myStringValue").is(ref[0]).and("identifier.value.myStringValue")
						.is(ref[1]);
			}
		}
		return criteria;
	}

	@Override
	protected String getProfile() {
		return "Person-v1.0";
	}

	@Override
	protected PersonEntity fromFhir(Person obj) {
		return PersonEntity.fromPerson(obj);
	}

	@Override
	protected Person toFhir(PersonEntity ent) {
		return PersonEntity.toPerson(ent);
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return PersonEntity.class;
	}
}
