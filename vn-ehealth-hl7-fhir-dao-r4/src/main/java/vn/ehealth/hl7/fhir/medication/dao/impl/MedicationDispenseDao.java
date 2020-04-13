package vn.ehealth.hl7.fhir.medication.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.MedicationDispense;
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
import vn.ehealth.hl7.fhir.medication.entity.MedicationDispenseEntity;

@Repository
public class MedicationDispenseDao extends BaseDao<MedicationDispenseEntity, MedicationDispense> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext fhirContext, TokenParam code, TokenParam type, TokenParam status,
			TokenParam identifier, ReferenceParam context, ReferenceParam destination, ReferenceParam medication,
			ReferenceParam patient, ReferenceParam performer, ReferenceParam prescription, ReferenceParam receiver,
			ReferenceParam responsibleparty, ReferenceParam subject, ReferenceParam whenhandedover,
			DateRangeParam whenprepared,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count) {
		List<Resource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(code, type, status, identifier, context, destination, medication,
				patient, performer, prescription, receiver, responsibleparty, subject, whenhandedover, whenprepared,
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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
		List<MedicationDispenseEntity> medicationDispenseEntitys = mongo.find(query, MedicationDispenseEntity.class);
		if (medicationDispenseEntitys != null) {
			for (MedicationDispenseEntity item : medicationDispenseEntitys) {
				MedicationDispense medicationDispense = transform(item);
				resources.add(medicationDispense);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam code, TokenParam type, TokenParam status,
			TokenParam identifier, ReferenceParam context, ReferenceParam destination, ReferenceParam medication,
			ReferenceParam patient, ReferenceParam performer, ReferenceParam prescription, ReferenceParam receiver,
			ReferenceParam responsibleparty, ReferenceParam subject, ReferenceParam whenhandedover,
			DateRangeParam whenprepared, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(code, type, status, identifier, context, destination, medication,
				patient, performer, prescription, receiver, responsibleparty, subject, whenhandedover, whenprepared,
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, MedicationDispenseEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(TokenParam code, TokenParam type, TokenParam status, TokenParam identifier,
			ReferenceParam context, ReferenceParam destination, ReferenceParam medication, ReferenceParam patient,
			ReferenceParam performer, ReferenceParam prescription, ReferenceParam receiver,
			ReferenceParam responsibleparty, ReferenceParam subject, ReferenceParam whenhandedover,
			DateRangeParam whenprepared, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		// status
		if (status != null) {
			criteria.and("status").is(status.getValue());
		}
		return criteria;
	}

	@Override
	protected List<String> getProfile() {
		return null;
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return MedicationDispense.class;
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return MedicationDispenseEntity.class;
	}
}
