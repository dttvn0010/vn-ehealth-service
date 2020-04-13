package vn.ehealth.hl7.fhir.schedule.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.AppointmentResponse;
import org.hl7.fhir.r4.model.DomainResource;
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
import vn.ehealth.hl7.fhir.schedule.entity.AppointmentResponseEntity;

@Repository
public class AppointmentResponseDao extends BaseDao<AppointmentResponseEntity, AppointmentResponse> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext fhirContext, ReferenceParam actor, TokenParam identifier,
			ReferenceParam appointment, ReferenceParam location, ReferenceParam patient, ReferenceParam practitioner,
			TokenParam partStatus,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count) {

		List<Resource> resources = new ArrayList<>();

		Criteria criteria = setParamToCriteria(fhirContext, actor, identifier, appointment, location, patient,
				practitioner, partStatus, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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
		List<AppointmentResponseEntity> appointmentResponseEntitys = mongo.find(query, AppointmentResponseEntity.class);
		if (appointmentResponseEntitys != null) {
			for (AppointmentResponseEntity item : appointmentResponseEntitys) {
				AppointmentResponse appointmentResponse = transform(item);
				resources.add(appointmentResponse);
			}
		}
		return resources;
	}

	public long findMatchesAdvancedTotal(FhirContext fhirContext, ReferenceParam actor, TokenParam identifier,
			ReferenceParam appointment, ReferenceParam location, ReferenceParam patient, ReferenceParam practitioner,
			TokenParam partStatus, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(fhirContext, actor, identifier, appointment, location, patient,
				practitioner, partStatus, resid, _lastUpdated, _tag, _profile, _query, _security, _content);

		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, AppointmentResponseEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(FhirContext fhirContext, ReferenceParam actor, TokenParam identifier,
			ReferenceParam appointment, ReferenceParam location, ReferenceParam patient, ReferenceParam practitioner,
			TokenParam partStatus, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		// default
		criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				identifier);
		if (actor != null) {
			if (actor.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("actor.reference").is(actor.getValue()),
						Criteria.where("actor.display").is(actor.getValue()));
			} else {
				String[] ref = actor.getValue().split("\\|");
				criteria.and("actor.identifier.system").is(ref[0]).and("actor.identifier.value").is(ref[1]);
			}
		}

		if (appointment != null) {
			if (appointment.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("appointment.reference").is(appointment.getValue()),
						Criteria.where("appointment.display").is(appointment.getValue()));
			} else {
				String[] ref = appointment.getValue().split("\\|");
				criteria.and("appointment.identifier.system").is(ref[0]).and("appointment.identifier.value").is(ref[1]);
			}
		}
		if (location != null) {
			if (location.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("actor.reference").is(location.getValue()),
						Criteria.where("actor.display").is(location.getValue()));
			} else {
				String[] ref = location.getValue().split("\\|");
				criteria.and("actor.identifier.system").is(ref[0]).and("actor.identifier.value").is(ref[1]);
			}
		}
		if (patient != null) {
			if (patient.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("actor.reference").is(patient.getValue()),
						Criteria.where("actor.display").is(patient.getValue()));
			} else {
				String[] ref = patient.getValue().split("\\|");
				criteria.and("actor.identifier.system").is(ref[0]).and("actor.identifier.value").is(ref[1]);
			}
		}
		if (practitioner != null) {
			if (practitioner.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("actor.reference").is(practitioner.getValue()),
						Criteria.where("actor.display").is(practitioner.getValue()));
			} else {
				String[] ref = practitioner.getValue().split("\\|");
				criteria.and("actor.identifier.system").is(ref[0]).and("actor.identifier.value").is(ref[1]);
			}
		}
		if (partStatus != null) {
			criteria.and("participantStatus").is(partStatus.getValue());
		}
		return criteria;
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return AppointmentResponse.class;
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return AppointmentResponseEntity.class;
	}
}
