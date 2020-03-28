package vn.ehealth.hl7.fhir.medication.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.MedicationAdministration;
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
import vn.ehealth.hl7.fhir.medication.entity.MedicationAdministrationEntity;

@Repository
public class MedicationAdministrationDao extends BaseDao<MedicationAdministrationEntity, MedicationAdministration> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext fhirContext, TokenParam code, ReferenceParam context,
			ReferenceParam device, DateRangeParam effectiveTime, TokenParam identifier, ReferenceParam medication,
			TokenParam notGiven, ReferenceParam patient, ReferenceParam performer, ReferenceParam prescription,
			TokenParam reasonGiven, TokenParam reasonNotGiven, TokenParam status, ReferenceParam subject,
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, StringParam _page, String sortParam, Integer count) {
		List<Resource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(code, context, device, effectiveTime, identifier, medication, notGiven,
				patient, performer, prescription, reasonGiven, reasonNotGiven, status, subject, resid, _lastUpdated,
				_tag, _profile, _query, _security, _content);
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
		List<MedicationAdministrationEntity> medicationAdministrationEntitys = mongo.find(query,
				MedicationAdministrationEntity.class);
		if (medicationAdministrationEntitys != null) {
			for (MedicationAdministrationEntity item : medicationAdministrationEntitys) {
				MedicationAdministration medicationAdministration = transform(item);
				resources.add(medicationAdministration);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam code, ReferenceParam context,
			ReferenceParam device, DateRangeParam effectiveTime, TokenParam identifier, ReferenceParam medication,
			TokenParam notGiven, ReferenceParam patient, ReferenceParam performer, ReferenceParam prescription,
			TokenParam reasonGiven, TokenParam reasonNotGiven, TokenParam status, ReferenceParam subject,
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(code, context, device, effectiveTime, identifier, medication, notGiven,
				patient, performer, prescription, reasonGiven, reasonNotGiven, status, subject, resid, _lastUpdated,
				_tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, MedicationAdministrationEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(TokenParam code, ReferenceParam context, ReferenceParam device,
			DateRangeParam effectiveTime, TokenParam identifier, ReferenceParam medication, TokenParam notGiven,
			ReferenceParam patient, ReferenceParam performer, ReferenceParam prescription, TokenParam reasonGiven,
			TokenParam reasonNotGiven, TokenParam status, ReferenceParam subject, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where("active").is(true);
		// set param default
		criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				null);
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
		// device
		if (device != null) {
			if (device.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("device.reference").is(device.getValue()),
						Criteria.where("device.display").is(device.getValue()));
			} else {
				String[] ref = device.getValue().split("\\|");
				criteria.and("device.identifier.system").is(ref[0]).and("device.identifier.value").is(ref[1]);
			}
		}
		// effective-time
		if (effectiveTime != null) {
			criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "effective", effectiveTime);
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
		// not-given
		if (notGiven != null) {
			/** not write **/
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
		// performer
		if (performer != null) {
			if (performer.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("performer.actor.reference").is(performer.getValue()),
						Criteria.where("performer.actor.display").is(performer.getValue()));
			} else {
				String[] ref = performer.getValue().split("\\|");
				criteria.and("performer.actor.identifier.system").is(ref[0]).and("performer.actor.identifier.value")
						.is(ref[1]);
			}
		}
		// prescription
		if (prescription != null) {
			if (prescription.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("prescription.reference").is(prescription.getValue()),
						Criteria.where("prescription.display").is(prescription.getValue()));
			} else {
				String[] ref = prescription.getValue().split("\\|");
				criteria.and("prescription.identifier.system").is(ref[0]).and("prescription.identifier.value")
						.is(ref[1]);
			}
		}
		// reason-given
		if (reasonGiven != null) {
			criteria.and("reasonGiven.coding.code.myStringValue").is(reasonGiven.getValue());
		}
		// reason-not-given
		if (reasonGiven != null) {
			criteria.and("reasonNotGiven.coding.code.myStringValue").is(reasonNotGiven.getValue());
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
	protected String getProfile() {
		return "MedicationAdministration-v1.0";
	}

	@Override
	protected MedicationAdministrationEntity fromFhir(MedicationAdministration obj) {
		return MedicationAdministrationEntity.fromMedicationAdministration(obj);
	}

	@Override
	protected MedicationAdministration toFhir(MedicationAdministrationEntity ent) {
		return MedicationAdministrationEntity.toMedicationAdministration(ent);
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return MedicationAdministrationEntity.class;
	}

}
