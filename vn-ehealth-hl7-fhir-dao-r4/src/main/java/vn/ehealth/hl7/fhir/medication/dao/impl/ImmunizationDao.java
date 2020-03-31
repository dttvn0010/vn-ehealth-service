package vn.ehealth.hl7.fhir.medication.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Immunization;
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
import vn.ehealth.hl7.fhir.medication.entity.ImmunizationEntity;

@Repository
public class ImmunizationDao extends BaseDao<ImmunizationEntity, Immunization> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext fhirContext, DateRangeParam date, NumberParam doseSequence,
			TokenParam identifier, ReferenceParam location, StringParam lotNumber, ReferenceParam manufacturer,
			TokenParam notgiven, ReferenceParam patient, ReferenceParam practitioner, ReferenceParam reaction,
			DateRangeParam reactionDate, TokenParam reason, TokenParam reasonNotGiven, TokenParam status,
			TokenParam vaccineCode, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content, StringParam _page, String sortParam,
			Integer count) {
		List<Resource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(date, doseSequence, identifier, location, lotNumber, manufacturer,
				notgiven, patient, practitioner, reaction, reactionDate, reason, reasonNotGiven, status, vaccineCode,
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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
		List<ImmunizationEntity> immunizationEntitys = mongo.find(query, ImmunizationEntity.class);
		if (immunizationEntitys != null) {
			for (ImmunizationEntity item : immunizationEntitys) {
				Immunization immunization = transform(item);
				resources.add(immunization);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, DateRangeParam date, NumberParam doseSequence,
			TokenParam identifier, ReferenceParam location, StringParam lotNumber, ReferenceParam manufacturer,
			TokenParam notgiven, ReferenceParam patient, ReferenceParam practitioner, ReferenceParam reaction,
			DateRangeParam reactionDate, TokenParam reason, TokenParam reasonNotGiven, TokenParam status,
			TokenParam vaccineCode, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(date, doseSequence, identifier, location, lotNumber, manufacturer,
				notgiven, patient, practitioner, reaction, reactionDate, reason, reasonNotGiven, status, vaccineCode,
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, ImmunizationEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(DateRangeParam date, NumberParam doseSequence, TokenParam identifier,
			ReferenceParam location, StringParam lotNumber, ReferenceParam manufacturer, TokenParam notgiven,
			ReferenceParam patient, ReferenceParam practitioner, ReferenceParam reaction, DateRangeParam reactionDate,
			TokenParam reason, TokenParam reasonNotGiven, TokenParam status, TokenParam vaccineCode, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where("active").is(true);
		// set param default
		criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				null);
		// date
		if (date != null) {
			criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "date", date);
		}
		// dose-sequence
		if (doseSequence != null) {
			criteria.and("vaccinationProtocol.doseSequence").is(doseSequence.getValue());
		}
		// location
		if (location != null) {
			if (location.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("location.reference").is(location.getValue()),
						Criteria.where("location.display").is(location.getValue()));
			} else {
				String[] ref = location.getValue().split("\\|");
				criteria.and("location.identifier.system").is(ref[0]).and("location.identifier.value").is(ref[1]);
			}
		}
		// lot-number
		if (lotNumber != null && !lotNumber.isEmpty()) {
			criteria.and("lotNumber").is(lotNumber.getValue());
		}
		// manufacturer
		if (manufacturer != null) {
			if (manufacturer.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("manufacturer.reference").is(manufacturer.getValue()),
						Criteria.where("manufacturer.display").is(manufacturer.getValue()));
			} else {
				String[] ref = manufacturer.getValue().split("\\|");
				criteria.and("manufacturer.identifier.system").is(ref[0]).and("manufacturer.identifier.value")
						.is(ref[1]);
			}
		}
		// notgiven
		if (notgiven != null && !notgiven.isEmpty()) {
			/** not write **/
		}
		// patient
		if (patient != null) {
			if (patient.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("patient.reference").is(patient.getValue()),
						Criteria.where("patient.display").is(patient.getValue()));
			} else {
				String[] ref = patient.getValue().split("\\|");
				criteria.and("patient.identifier.system").is(ref[0]).and("patient.identifier.value").is(ref[1]);
			}
		}
		// practitioner
		if (practitioner != null) {
			if (practitioner.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("practitioner.actor.reference").is(practitioner.getValue()),
						Criteria.where("practitioner.actor.display").is(practitioner.getValue()));
			} else {
				String[] ref = practitioner.getValue().split("\\|");
				criteria.and("practitioner.actor.identifier.system").is(ref[0])
						.and("practitioner.actor.identifier.value").is(ref[1]);
			}
		}
		// reaction
		if (reaction != null) {
			if (reaction.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("reaction.detail.reference").is(reaction.getValue()),
						Criteria.where("reaction.detail.display").is(reaction.getValue()));
			} else {
				String[] ref = reaction.getValue().split("\\|");
				criteria.and("reaction.detail.identifier.system").is(ref[0]).and("reaction.detail.identifier.value")
						.is(ref[1]);
			}
		}
		// reaction-date
		if (reactionDate != null) {
			criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "reaction.date", reactionDate);
		}
		// reason
		if (reason != null && !reason.isEmpty()) {
			criteria.and("explanation.reason.coding.system").is(reason.getSystem())
					.and("explanation.reason.coding.code").is(reason.getValue());
		}
		// reason-not-given
		if (reasonNotGiven != null && !reasonNotGiven.isEmpty()) {
			criteria.and("explanation.reasonNotGiven.coding.system").is(reasonNotGiven.getSystem())
					.and("explanation.reasonNotGiven.coding.code").is(reasonNotGiven.getValue());
		}
		// status
		if (status != null) {
			criteria.and("status").is(status.getValue());
		}
		// vaccine-code
		if (vaccineCode != null && !vaccineCode.isEmpty()) {
			criteria.and("vaccineCode.coding.system").is(vaccineCode.getSystem()).and("vaccineCode.coding.code")
					.is(vaccineCode.getValue());
		}
		return criteria;
	}

	@Override
	protected String getProfile() {
		return "Immunization-v1.0";
	}

	@Override
    protected Class<? extends DomainResource> getResourceClass() {
        return Immunization.class;
    }

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return ImmunizationEntity.class;
	}
}
