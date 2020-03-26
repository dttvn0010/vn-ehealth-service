package vn.ehealth.hl7.fhir.ehr.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.ehr.entity.EncounterEntity;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;

@Repository
public class EncounterDao extends BaseDao<EncounterEntity, Encounter> {

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(FhirContext ctx, TokenParam active, ReferenceParam appointment, TokenParam _class,
			DateRangeParam date, ReferenceParam diagnosis, ReferenceParam episodeofcare, TokenParam identifier,
			ReferenceParam incomingreferral, NumberParam length, ReferenceParam location, DateRangeParam locationPeriod,
			ReferenceParam partOf, ReferenceParam participant, TokenParam participantType, ReferenceParam patient,
			ReferenceParam practitioner, TokenParam reason, ReferenceParam serviceProvider,
			TokenParam specialArrangement, TokenParam status, ReferenceParam subject, TokenParam type, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content, StringParam _page, String sortParam, Integer count, Set<Include> includes) {

		List<IBaseResource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(active, appointment, _class, date, diagnosis, episodeofcare, identifier,
				incomingreferral, length, location, locationPeriod, partOf, participant, participantType, patient,
				practitioner, reason, serviceProvider, specialArrangement, status, subject, type, resid, _lastUpdated,
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
		
		List<EncounterEntity> encounterEntitys = mongo.find(query, EncounterEntity.class);
		
		String[] keys = {"subject", "episodeOfCare", "participant:individual", "appointment", 
							"diagnosis:condition", "basedOn", "reasonReference", "account", 
							"serviceProvider", "location:location", 
							"hospitalization:origin", "hospitalization:destination"};
		
		var includeMap = getIncludeMap(ResourceType.Encounter, keys, includes);
		
		if (encounterEntitys != null) {
			for (EncounterEntity item : encounterEntitys) {
				Encounter obj = transform(item);
				
				if(includeMap.get("subject") && obj.hasSubject()) {
					setReferenceResource(obj.getSubject());
				}
				
				if(includeMap.get("episodeOfCare") && obj.hasEpisodeOfCare()) {
					setReferenceResource(obj.getEpisodeOfCare());
				}
				
				if(includeMap.get("participant:individual") && obj.hasParticipant()) {
					obj.getParticipant().forEach(x -> setReferenceResource(x.getIndividual()));
				}
				
				if(includeMap.get("appointment") && obj.hasAppointment()) {
					setReferenceResource(obj.getAppointment());
				}
				
				if(includeMap.get("diagnosis:condition") && obj.hasDiagnosis()) {
					obj.getDiagnosis().forEach(x -> setReferenceResource(x.getCondition()));
				}
				
				if(includeMap.get("basedOn") && obj.hasBasedOn()) {
					setReferenceResource(obj.getBasedOn());
				}
				
				if(includeMap.get("reasonReference") && obj.hasReasonReference()) {
					setReferenceResource(obj.getReasonReference());
				}
				
				if(includeMap.get("account") && obj.hasAccount()) {
					setReferenceResource(obj.getAccount());
				}
				
				if(includeMap.get("serviceProvider") && obj.hasServiceProvider()) {
					setReferenceResource(obj.getServiceProvider());
				}
				
				if(includeMap.get("location:location") && obj.hasLocation()) {
					obj.getLocation().forEach(x -> setReferenceResource(x.getLocation()));
				}
				
				if(includeMap.get("hospitalization:origin") && obj.hasHospitalization()) {
					setReferenceResource(obj.getHospitalization().getOrigin());
				}
				
				if(includeMap.get("hospitalization:destination") && obj.hasHospitalization()) {
					setReferenceResource(obj.getHospitalization().getDestination());
				}				
				
				resources.add(obj);
			}
		}
		return resources;
	}

	public long getTotal(FhirContext fhirContext, TokenParam active, ReferenceParam appointment, TokenParam _class,
			DateRangeParam date, ReferenceParam diagnosis, ReferenceParam episodeofcare, TokenParam identifier,
			ReferenceParam incomingreferral, NumberParam length, ReferenceParam location, DateRangeParam locationPeriod,
			ReferenceParam partOf, ReferenceParam participant, TokenParam participantType, ReferenceParam patient,
			ReferenceParam practitioner, TokenParam reason, ReferenceParam serviceProvider,
			TokenParam specialArrangement, TokenParam status, ReferenceParam subject, TokenParam type, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {

		long total = 0;
		Criteria criteria = setParamToCriteria(active, appointment, _class, date, diagnosis, episodeofcare, identifier,
				incomingreferral, length, location, locationPeriod, partOf, participant, participantType, patient,
				practitioner, reason, serviceProvider, specialArrangement, status, subject, type, resid, _lastUpdated,
				_tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, EncounterEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(TokenParam active, ReferenceParam appointment, TokenParam _class,
			DateRangeParam date, ReferenceParam diagnosis, ReferenceParam episodeofcare, TokenParam identifier,
			ReferenceParam incomingreferral, NumberParam length, ReferenceParam location, DateRangeParam locationPeriod,
			ReferenceParam partOf, ReferenceParam participant, TokenParam participantType, ReferenceParam patient,
			ReferenceParam practitioner, TokenParam reason, ReferenceParam serviceProvider,
			TokenParam specialArrangement, TokenParam status, ReferenceParam subject, TokenParam type, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {
		Criteria criteria = null;
		// active
		if (active != null) {
			criteria = Criteria.where("active").is(active);
		} else {
			criteria = Criteria.where("active").is(true);
		}
		// set param default
		criteria = addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				identifier);
		// appointment
		if (appointment != null) {
			if (appointment.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("appointment.reference").is(appointment.getValue()),
						Criteria.where("appointment.display").is(appointment.getValue()));
			} else {
				String[] ref = appointment.getValue().split("\\|");
				criteria.and("appointment.identifier.system").is(ref[0]).and("appointment.identifier.value").is(ref[1]);
			}
		}
		// class
		if (_class != null) {
			criteria.and("class.code.myStringValue").is(_class.getValue());
		}
		// date
		if (date != null) {
			criteria = setTypeDateToCriteria(criteria, "period", date);
		}
		// diagnosis
		if (diagnosis != null) {
			if (diagnosis.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("diagnosis.condition.reference").is(diagnosis.getValue()),
						Criteria.where("diagnosis.condition.display").is(diagnosis.getValue()));
			} else {
				String[] ref = diagnosis.getValue().split("\\|");
				criteria.and("diagnosis.condition.identifier.system").is(ref[0])
						.and("diagnosis.condition.identifier.value").is(ref[1]);
			}
		}
		// episodeofcare
		if (episodeofcare != null) {
			if (episodeofcare.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("episodeofcare.reference").is(episodeofcare.getValue()),
						Criteria.where("episodeofcare.display").is(episodeofcare.getValue()));
			} else {
				String[] ref = episodeofcare.getValue().split("\\|");
				criteria.and("episodeofcare.identifier.system").is(ref[0]).and("episodeofcare.identifier.value")
						.is(ref[1]);
			}
		}
		// incomingreferral
		if (incomingreferral != null) {
			if (incomingreferral.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("referralRequest.reference").is(incomingreferral.getValue()),
						Criteria.where("referralRequest.display").is(incomingreferral.getValue()));
			} else {
				String[] ref = incomingreferral.getValue().split("\\|");
				criteria.and("referralRequest.identifier.system").is(ref[0]).and("referralRequest.identifier.value")
						.is(ref[1]);
			}
		}
		// length
		if (length != null) {
			criteria.and("length").is(length.getValue());
		}
		// location
		if (location != null) {
			if (location.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("location.location.reference").is(location.getValue()),
						Criteria.where("location.location.display").is(location.getValue()));
			} else {
				String[] ref = location.getValue().split("\\|");
				criteria.and("location.location.identifier.system").is(ref[0]).and("location.location.identifier.value")
						.is(ref[1]);
			}
		}
		// location.period
		if (locationPeriod != null) {
			criteria = setTypeDateToCriteria(criteria, "location.period", locationPeriod);
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
		// participant
		if (participant != null) {
			if (participant.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("participant.individual.reference").is(participant.getValue()),
						Criteria.where("participant.individual.display").is(participant.getValue()));
			} else {
				String[] ref = participant.getValue().split("\\|");
				criteria.and("participant.individual.identifier.system").is(ref[0])
						.and("participant.individual.identifier.value").is(ref[1]);
			}
		}
		// participant-type
		if (participantType != null) {
			criteria.and("participant.type.coding.code.myStringValue").is(participantType.getValue());
		}
		// practitioner
		if (practitioner != null) {
			if (practitioner.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("participant.individual.reference").is(practitioner.getValue()),
						Criteria.where("participant.individual.display").is(practitioner.getValue()));
			} else {
				String[] ref = practitioner.getValue().split("\\|");
				criteria.and("participant.individual.identifier.system").is(ref[0])
						.and("participant.individual.identifier.value").is(ref[1]);
			}
		}
		if (reason != null) {
			if (type != null) {
				criteria.and("reason.coding.code.myStringValue").is(status.getValue());
			}
		}
		// service-provider
		if (serviceProvider != null) {
			if (serviceProvider.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("serviceProvider.reference").is(serviceProvider.getValue()),
						Criteria.where("serviceProvider.display").is(serviceProvider.getValue()));
			} else {
				String[] ref = serviceProvider.getValue().split("\\|");
				criteria.and("serviceProvider.identifier.system").is(ref[0]).and("serviceProvider.identifier.value")
						.is(ref[1]);
			}
		}
		// special-arrangement
		if (specialArrangement != null) {
			criteria.and("hospitalization.specialArrangement.coding.code.myStringValue").is(status.getValue());
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
		// type
		if (type != null) {
			criteria.and("type.coding.code").is(type.getValue())
			        .and("type.coding.system").is(type.getSystem());
		}
		return criteria;
	}
	
	@Override
	protected String getProfile() {
		return "Encounter-v1.0";
	}

	@Override
	protected EncounterEntity fromFhir(Encounter obj) {
		return EncounterEntity.fromEncounter(obj);
	}

	@Override
	protected Encounter toFhir(EncounterEntity ent) {
		return EncounterEntity.toEncounter(ent);
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return EncounterEntity.class;
	}
}
