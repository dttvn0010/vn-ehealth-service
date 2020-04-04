package vn.ehealth.hl7.fhir.schedule.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Appointment;
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
import vn.ehealth.hl7.fhir.schedule.entity.AppointmentEntity;

@Repository
public class AppointmentDao extends BaseDao<AppointmentEntity, Appointment> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext fhirContext, ReferenceParam actor, TokenParam appointmentType,
			DateRangeParam date, TokenParam identifier, ReferenceParam incomingreferral, ReferenceParam location,
			TokenParam partStatus, ReferenceParam patient, ReferenceParam practitioner, TokenParam serviceType,
			TokenParam status,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count) {
		List<Resource> resources = new ArrayList<>();
		Criteria criteria = null;
		criteria = setParamToCriteria(actor, appointmentType, date, identifier, incomingreferral, location, partStatus,
				patient, practitioner, serviceType, status, resid, _lastUpdated, _tag, _profile, _query, _security,
				_content);
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
		List<AppointmentEntity> appointmentResults = mongo.find(query, AppointmentEntity.class);
		for (AppointmentEntity appointmentEntity : appointmentResults) {
			resources.add(transform(appointmentEntity));
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, ReferenceParam actor, TokenParam appointmentType,
			DateRangeParam date, TokenParam identifier, ReferenceParam incomingreferral, ReferenceParam location,
			TokenParam partStatus, ReferenceParam patient, ReferenceParam practitioner, TokenParam serviceType,
			TokenParam status, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		criteria = setParamToCriteria(actor, appointmentType, date, identifier, incomingreferral, location, partStatus,
				patient, practitioner, serviceType, status, resid, _lastUpdated, _tag, _profile, _query, _security,
				_content);
		long count = 0;
		if (criteria != null) {
			Query qry = Query.query(criteria);
			count = mongo.count(qry, AppointmentEntity.class);
		} else {
			Query query = new Query();
			count = mongo.count(query, AppointmentEntity.class);
		}
		return count;
	}

	private Criteria setParamToCriteria(ReferenceParam actor, TokenParam appointmentType, DateRangeParam date,
			TokenParam identifier, ReferenceParam incomingreferral, ReferenceParam location, TokenParam partStatus,
			ReferenceParam patient, ReferenceParam practitioner, TokenParam serviceType, TokenParam status,
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		// default
		criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				identifier);
		// actor
		if (actor != null) {
			if (actor.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("participant.actor.reference").is(actor.getValue()),
						Criteria.where("participant.actor.display").is(actor.getValue()));
			} else {
				String[] ref = actor.getValue().split("\\|");
				criteria.and("participant.actor.identifier.system").is(ref[0]).and("participant.actor.identifier.value")
						.is(ref[1]);
			}
		}
		// appointment-type
		if (appointmentType != null) {
			criteria.and("appointmentType").regex(appointmentType.getValue());
		}
		// date
		if (date != null) {
			String keySearch = "start";
			criteria = DatabaseUtil.setTypeDateToCriteria(criteria, keySearch, date);
		}
		// identifier
		if (identifier != null) {
			criteria.and("identifiers.system").is(identifier.getSystem()).and("identifiers.value")
					.is(identifier.getValue());
		}
		// incomingreferral
		if (incomingreferral != null) {
			if (incomingreferral.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("incomingreferral.reference").is(incomingreferral.getValue()),
						Criteria.where("incomingreferral.display").is(incomingreferral.getValue()));
			} else {
				String[] ref = incomingreferral.getValue().split("\\|");
				criteria.and("incomingreferral.identifier.system").is(ref[0]).and("incomingreferral.identifier.value")
						.is(ref[1]);
			}
		}
		// location
		if (location != null) {
			if (location.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("participant.actor.reference").is(location.getValue()),
						Criteria.where("participant.actor.display").is(location.getValue()));
			} else {
				String[] ref = location.getValue().split("\\|");
				criteria.and("participant.actor.identifier.system").is(ref[0]).and("participant.actor.identifier.value")
						.is(ref[1]);
			}
		}
		// part-status
		if (partStatus != null) {
			criteria.and("participant.actor").regex(partStatus.getValue());
		}
		// patient
		if (patient != null) {
			if (patient.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("participant.actor.reference").is(patient.getValue()),
						Criteria.where("participant.actor.display").is(patient.getValue()));
			} else {
				String[] ref = patient.getValue().split("\\|");
				criteria.and("participant.actor.identifier.system").is(ref[0]).and("participant.actor.identifier.value")
						.is(ref[1]);
			}
		}
		// practitioner
		if (practitioner != null) {
			if (practitioner.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("participant.actor.reference").is(practitioner.getValue()),
						Criteria.where("participant.actor.display").is(practitioner.getValue()));
			} else {
				String[] ref = practitioner.getValue().split("\\|");
				criteria.and("participant.actor.identifier.system").is(ref[0]).and("participant.actor.identifier.value")
						.is(ref[1]);
			}
		}
		// service-type
		if (serviceType != null) {
			criteria.and("serviceType").is(serviceType.getValue());
		}
		// status
		if (status != null) {
			criteria.and("status").is(status.getValue());
		}
		return criteria;
	}

	@Override
	protected String getProfile() {
		return "Appointment-v1.0";
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return Appointment.class;
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return AppointmentEntity.class;
	}
}
