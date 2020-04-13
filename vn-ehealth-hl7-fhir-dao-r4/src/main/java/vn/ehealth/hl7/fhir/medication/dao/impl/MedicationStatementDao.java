package vn.ehealth.hl7.fhir.medication.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.MedicationStatement;
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
import vn.ehealth.hl7.fhir.medication.entity.MedicationStatementEntity;

@Repository
public class MedicationStatementDao extends BaseDao<MedicationStatementEntity, MedicationStatement> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext fhirContext, TokenParam category, TokenParam code, ReferenceParam context,
			DateRangeParam effective, TokenParam identifier, ReferenceParam medication, ReferenceParam partOf,
			ReferenceParam patient, ReferenceParam source, TokenParam status, ReferenceParam subject,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count) {
		List<Resource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(category, code, context, effective, identifier, medication, partOf,
				patient, source, status, subject, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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
		List<MedicationStatementEntity> medicationStatementEntitys = mongo.find(query, MedicationStatementEntity.class);
		if (medicationStatementEntitys != null) {
			for (MedicationStatementEntity item : medicationStatementEntitys) {
				MedicationStatement medicationStatement = transform(item);
				resources.add(medicationStatement);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam category, TokenParam code,
			ReferenceParam context, DateRangeParam effective, TokenParam identifier, ReferenceParam medication,
			ReferenceParam partOf, ReferenceParam patient, ReferenceParam source, TokenParam status,
			ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(category, code, context, effective, identifier, medication, partOf,
				patient, source, status, subject, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, MedicationStatementEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(TokenParam category, TokenParam code, ReferenceParam context,
			DateRangeParam effective, TokenParam identifier, ReferenceParam medication, ReferenceParam partOf,
			ReferenceParam patient, ReferenceParam source, TokenParam status, ReferenceParam subject, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		// set param default
		criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				null);
		if (category != null) {
			criteria.and("category.coding.code.myStringValue").is(category.getValue());
		}
		// code
		if (code != null) {
			criteria.and("medication.coding.code.myStringValue").is(code.getValue());
		}
		// context
		if (context != null) {
			if (context.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("context.reference").is(context.getValue()),
						Criteria.where("context.display").is(context.getValue()));
			} else {
				String[] ref = context.getValue().split("\\|");
				criteria.and("context.identifier.system").is(ref[0]).and("context.identifier.value").is(ref[1]);
			}
		}
		// effective
		if (effective != null) {
			criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "effective", effective);
		}
		// medication
		if (medication != null) {
			if (medication.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("medication.reference").is(medication.getValue()),
						Criteria.where("medication.display").is(medication.getValue()));
			} else {
				String[] ref = medication.getValue().split("\\|");
				criteria.and("medication.identifier.system").is(ref[0]).and("medication.identifier.value").is(ref[1]);
			}
		}
		// part-of
		if (partOf != null) {
			if (partOf.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("partOf.reference").is(partOf.getValue()),
						Criteria.where("partOf.display").is(partOf.getValue()));
			} else {
				String[] ref = partOf.getValue().split("\\|");
				criteria.and("partOf.identifier.system").is(ref[0]).and("partOf.identifier.value").is(ref[1]);
			}
		}
		// patient
		if (patient != null) {
			if (patient.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("subject.reference").is(patient.getValue()),
						Criteria.where("subject.display").is(patient.getValue()));
			} else {
				String[] ref = patient.getValue().split("\\|");
				criteria.and("subject.identifier.system").is(ref[0]).and("subject.identifier.value").is(ref[1]);
			}
		}
		// source
		if (source != null) {
			if (source.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("informationSource.reference").is(source.getValue()),
						Criteria.where("informationSource.display").is(source.getValue()));
			} else {
				String[] ref = source.getValue().split("\\|");
				criteria.and("informationSource.identifier.system").is(ref[0]).and("informationSource.identifier.value")
						.is(ref[1]);
			}
		}
		// status
		if (status != null) {
			criteria.and("status").is(status.getValue());
		}
		// subject
		if (subject != null) {
			if (subject.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("subject.reference").is(subject.getValue()),
						Criteria.where("subject.display").is(subject.getValue()));
			} else {
				String[] ref = subject.getValue().split("\\|");
				criteria.and("subject.identifier.system").is(ref[0]).and("subject.identifier.value").is(ref[1]);
			}
		}
		return criteria;
	}

	@Override
	protected List<String> getProfile() {
		return null;
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return MedicationStatement.class;
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return MedicationStatementEntity.class;
	}

}
