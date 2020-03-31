package vn.ehealth.hl7.fhir.medication.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.MedicationRequest;
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
import vn.ehealth.hl7.fhir.medication.entity.MedicationRequestEntity;

@Repository
public class MedicationRequestDao extends BaseDao<MedicationRequestEntity, MedicationRequest> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext fhirContext, TokenParam code, TokenParam category, TokenParam identifier,
			TokenParam intent, TokenParam priority, TokenParam status, ReferenceParam subject, ReferenceParam requester,
			ReferenceParam patient, ReferenceParam medication, ReferenceParam context, ReferenceParam intendedDispenser,
			DateRangeParam authoredon, DateRangeParam date, TokenParam resid, DateRangeParam _lastUpdated,
			TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content,
			StringParam _page, String sortParam, Integer count) {
		List<Resource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(code, category, identifier, intent, priority, status, subject, requester,
				patient, medication, context, intendedDispenser, authoredon, date, resid, _lastUpdated, _tag, _profile,
				_query, _security, _content);
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
		List<MedicationRequestEntity> MedicationRequestEntitys = mongo.find(query, MedicationRequestEntity.class);
		if (MedicationRequestEntitys != null) {
			for (MedicationRequestEntity item : MedicationRequestEntitys) {
				MedicationRequest MedicationRequest = transform(item);
				resources.add(MedicationRequest);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam code, TokenParam category,
			TokenParam identifier, TokenParam intent, TokenParam priority, TokenParam status, ReferenceParam subject,
			ReferenceParam requester, ReferenceParam patient, ReferenceParam medication, ReferenceParam context,
			ReferenceParam intendedDispenser, DateRangeParam authoredon, DateRangeParam date, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(code, category, identifier, intent, priority, status, subject, requester,
				patient, medication, context, intendedDispenser, authoredon, date, resid, _lastUpdated, _tag, _profile,
				_query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, MedicationRequestEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(TokenParam code, TokenParam category, TokenParam identifier, TokenParam intent,
			TokenParam priority, TokenParam status, ReferenceParam subject, ReferenceParam requester,
			ReferenceParam patient, ReferenceParam medication, ReferenceParam context, ReferenceParam intendedDispenser,
			DateRangeParam authoredon, DateRangeParam date, TokenParam resid, DateRangeParam _lastUpdated,
			TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where("active").is(true);
		// set param default
		criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				identifier);
		// code
		if (code != null) {
			criteria.and("code.coding.code.myStringValue").is(code.getValue());
		}
		// category
		if (category != null) {
			criteria.and("category.coding.code.myStringValue").is(code.getValue());
		}
		// intent
		if (intent != null) {
			criteria.and("intent").is(intent.getValue());
		}
		// priority
		if (priority != null) {
			criteria.and("priority").is(priority.getValue());
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
		// requester
		if (requester != null) {
			if (requester.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("requester.agent.reference").is(requester.getValue()),
						Criteria.where("requester.agent.display").is(requester.getValue()));
			} else {
				String[] ref = requester.getValue().split("\\|");
				criteria.and("requester.agent.identifier.system").is(ref[0]).and("requester.agent.identifier.value")
						.is(ref[1]);
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
		// intendedDispenser
		if (intendedDispenser != null) {
			if (intendedDispenser.getValue().indexOf("|") == -1) {
				criteria.orOperator(
						Criteria.where("dispenseRequest.performer.reference").is(intendedDispenser.getValue()),
						Criteria.where("dispenseRequest.performer.display").is(intendedDispenser.getValue()));
			} else {
				String[] ref = intendedDispenser.getValue().split("\\|");
				criteria.and("dispenseRequest.performer.identifier.system").is(ref[0]).and("context.identifier.value")
						.is(ref[1]);
			}
		}
		// authoredon
		criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "authoredOn", date);
		// date
		criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "dosageInstruction.timing.event", date);
		// status
		if (status != null) {
			criteria.and("status").is(status.getValue());
		}

		return criteria;
	}

	@Override
	protected String getProfile() {
		return "MedicationRequest-v1.0";
	}

	@Override
    protected Class<? extends DomainResource> getResourceClass() {
        return MedicationRequest.class;
    }

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return MedicationRequestEntity.class;
	}
}
