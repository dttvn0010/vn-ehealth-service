package vn.ehealth.hl7.fhir.ehr.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.BodyStructure;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DetectedIssue;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.ImagingStudy;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Media;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.NutritionOrder;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.RiskAssessment;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.VisionPrescription;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;
import vn.ehealth.hl7.fhir.ehr.entity.EncounterEntity;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;

@Repository
public class EncounterDao extends BaseDao<EncounterEntity, Encounter> {

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(FhirContext ctx, ReferenceParam appointment, TokenParam _class,
			DateRangeParam date, ReferenceParam diagnosis, ReferenceParam episodeofcare, TokenParam identifier,
			ReferenceParam incomingreferral, NumberParam length, ReferenceParam location, DateRangeParam locationPeriod,
			ReferenceParam partOf, ReferenceParam participant, TokenParam participantType, ReferenceParam patient,
			ReferenceParam practitioner, TokenParam reason, ReferenceParam serviceProvider,
			TokenParam specialArrangement, TokenParam status, ReferenceParam subject, TokenParam type,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count,
			Set<Include> includes) {

		List<IBaseResource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(appointment, _class, date, diagnosis, episodeofcare, identifier,
				incomingreferral, length, location, locationPeriod, partOf, participant, participantType, patient,
				practitioner, reason, serviceProvider, specialArrangement, status, subject, type, resid, _lastUpdated,
				_tag, _profile, _query, _security, _content);
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

		List<EncounterEntity> encounterEntitys = mongo.find(query, EncounterEntity.class);

		String[] keys = { "subject", "episodeOfCare", "participant:individual", "appointment", "diagnosis:condition",
				"basedOn", "reasonReference", "account", "serviceProvider", "location:location",
				"hospitalization:origin", "hospitalization:destination" };

		var includeMap = getIncludeMap(ResourceType.Encounter, keys, includes);

		if (encounterEntitys != null) {
			for (EncounterEntity item : encounterEntitys) {
				Encounter obj = transform(item);

				if (includeMap.get("subject") && obj.hasSubject()) {
					setReferenceResource(obj.getSubject());
				}

				if (includeMap.get("episodeOfCare") && obj.hasEpisodeOfCare()) {
					setReferenceResource(obj.getEpisodeOfCare());
				}

				if (includeMap.get("participant:individual") && obj.hasParticipant()) {
					obj.getParticipant().forEach(x -> setReferenceResource(x.getIndividual()));
				}

				if (includeMap.get("appointment") && obj.hasAppointment()) {
					setReferenceResource(obj.getAppointment());
				}

				if (includeMap.get("diagnosis:condition") && obj.hasDiagnosis()) {
					obj.getDiagnosis().forEach(x -> setReferenceResource(x.getCondition()));
				}

				if (includeMap.get("basedOn") && obj.hasBasedOn()) {
					setReferenceResource(obj.getBasedOn());
				}

				if (includeMap.get("reasonReference") && obj.hasReasonReference()) {
					setReferenceResource(obj.getReasonReference());
				}

				if (includeMap.get("account") && obj.hasAccount()) {
					setReferenceResource(obj.getAccount());
				}

				if (includeMap.get("serviceProvider") && obj.hasServiceProvider()) {
					setReferenceResource(obj.getServiceProvider());
				}

				if (includeMap.get("location:location") && obj.hasLocation()) {
					obj.getLocation().forEach(x -> setReferenceResource(x.getLocation()));
				}

				if (includeMap.get("hospitalization:origin") && obj.hasHospitalization()) {
					setReferenceResource(obj.getHospitalization().getOrigin());
				}

				if (includeMap.get("hospitalization:destination") && obj.hasHospitalization()) {
					setReferenceResource(obj.getHospitalization().getDestination());
				}

				resources.add(obj);
			}
		}
		return resources;
	}

	public long getTotal(FhirContext fhirContext, ReferenceParam appointment, TokenParam _class, DateRangeParam date,
			ReferenceParam diagnosis, ReferenceParam episodeofcare, TokenParam identifier,
			ReferenceParam incomingreferral, NumberParam length, ReferenceParam location, DateRangeParam locationPeriod,
			ReferenceParam partOf, ReferenceParam participant, TokenParam participantType, ReferenceParam patient,
			ReferenceParam practitioner, TokenParam reason, ReferenceParam serviceProvider,
			TokenParam specialArrangement, TokenParam status, ReferenceParam subject, TokenParam type, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {

		long total = 0;
		Criteria criteria = setParamToCriteria(appointment, _class, date, diagnosis, episodeofcare, identifier,
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

	private Criteria setParamToCriteria(ReferenceParam appointment, TokenParam _class, DateRangeParam date,
			ReferenceParam diagnosis, ReferenceParam episodeofcare, TokenParam identifier,
			ReferenceParam incomingreferral, NumberParam length, ReferenceParam location, DateRangeParam locationPeriod,
			ReferenceParam partOf, ReferenceParam participant, TokenParam participantType, ReferenceParam patient,
			ReferenceParam practitioner, TokenParam reason, ReferenceParam serviceProvider,
			TokenParam specialArrangement, TokenParam status, ReferenceParam subject, TokenParam type, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		// set param default
		criteria = addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security, identifier);
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
			criteria.and("type.coding.code").is(type.getValue()).and("type.coding.system").is(type.getSystem());
		}
		return criteria;
	}

	@Override
	protected String getProfile() {
		return "Encounter-v1.0";
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return Encounter.class;
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return EncounterEntity.class;
	}
	
	public List<IBaseResource> getEverything(@IdParam IdType theId, DateParam theStart, DateParam theEnd) {

		List<IBaseResource> resources = new ArrayList<IBaseResource>();

		if (theId != null) {
			Encounter object = read(theId);
			if (object != null) {
				resources.add(object);
				
				// active
				Criteria criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
				// criteria.and("subject.reference").is(thePatientId.asStringValue());
				criteria.andOperator(
						new Criteria().orOperator(Criteria.where("encounter.reference").is(theId.asStringValue())));
				if (theStart != null) {
					criteria.and(ConstantKeys.QP_UPDATED).gte(theStart.getValue());
				}
				if (theEnd != null) {
					criteria.and(ConstantKeys.QP_UPDATED).lte(theEnd.getValue());
				}
				// Patient
				List<Patient> patients = DaoFactory.getPatientDao().findByCriteria(criteria);
				if (patients != null && patients.size() > 0) {
					resources.addAll(patients);
				}
				// CarePlan
				List<CarePlan> carePlans = DaoFactory.getCarePlanDao().findByCriteria(criteria);
				if (carePlans != null && carePlans.size() > 0) {
					resources.addAll(carePlans);
				}
				// Condition
				List<Condition> conditions = DaoFactory.getConditionDao().findByCriteria(criteria);
				if (conditions != null && conditions.size() > 0) {
					resources.addAll(conditions);
				}
				// DetectedIssue
				List<DetectedIssue> detectedIssues = DaoFactory.getDetectedIssueDao().findByCriteria(criteria);
				if (detectedIssues != null && detectedIssues.size() > 0) {
					resources.addAll(detectedIssues);
				}
				// Goal
				List<Goal> goals = DaoFactory.getGoalDao().findByCriteria(criteria);
				if (goals != null && goals.size() > 0) {
					resources.addAll(goals);
				}
				// Procedure
				List<Procedure> procedures = DaoFactory.getProcedureDao().findByCriteria(criteria);
				if (procedures != null && procedures.size() > 0) {
					resources.addAll(procedures);
				}
				// ServiceRequest
				List<ServiceRequest> serviceRequests = DaoFactory.getServiceRequestDao().findByCriteria(criteria);
				if (serviceRequests != null && serviceRequests.size() > 0) {
					resources.addAll(serviceRequests);
				}
				// DiagnosticReport
				List<DiagnosticReport> diagnosticReports = DaoFactory.getDiagnosticReportDao().findByCriteria(criteria);
				if (diagnosticReports != null && diagnosticReports.size() > 0) {
					resources.addAll(diagnosticReports);
				}
				// ImagingStudy
				List<ImagingStudy> imagingStudys = DaoFactory.getImagingStudyDao().findByCriteria(criteria);
				if (imagingStudys != null && imagingStudys.size() > 0) {
					resources.addAll(imagingStudys);
				}
				// Specimen
				List<Specimen> specimens = DaoFactory.getSpecimenDao().findByCriteria(criteria);
				if (specimens != null && specimens.size() > 0) {
					resources.addAll(specimens);
				}
				// CareTeam
				List<CareTeam> careTeams = DaoFactory.getCareTeamDao().findByCriteria(criteria);
				if (careTeams != null && careTeams.size() > 0) {
					resources.addAll(careTeams);
				}
				// Immunization
				List<Immunization> immunizations = DaoFactory.getImmunizationDao().findByCriteria(criteria);
				if (immunizations != null && immunizations.size() > 0) {
					resources.addAll(immunizations);
				}
				// MedicationAdministration
				List<MedicationAdministration> medicationAdministrations = DaoFactory.getMedicationAdministrationDao()
						.findByCriteria(criteria);
				if (medicationAdministrations != null && medicationAdministrations.size() > 0) {
					resources.addAll(medicationAdministrations);
				}
				// MedicationDispense
				List<MedicationDispense> medicationDispenses = DaoFactory.getMedicationDispenseDao()
						.findByCriteria(criteria);
				if (medicationDispenses != null && medicationDispenses.size() > 0) {
					resources.addAll(medicationDispenses);
				}
				// MedicationRequest
				List<MedicationRequest> medicationRequests = DaoFactory.getMedicationRequestDao()
						.findByCriteria(criteria);
				if (medicationRequests != null && medicationRequests.size() > 0) {
					resources.addAll(medicationRequests);
				}
				// MedicationStatement
				List<MedicationStatement> medicationStatements = DaoFactory.getMedicationStatementDao()
						.findByCriteria(criteria);
				if (medicationStatements != null && medicationStatements.size() > 0) {
					resources.addAll(medicationStatements);
				}
				// Observation
				List<Observation> observations = DaoFactory.getObservationDao().findByCriteria(criteria);
				if (observations != null && observations.size() > 0) {
					resources.addAll(observations);
				}
				// FamilyMemberHistory
				List<FamilyMemberHistory> familyMemberHistorys = DaoFactory.getFamilyMemberHistoryDao()
						.findByCriteria(criteria);
				if (familyMemberHistorys != null && familyMemberHistorys.size() > 0) {
					resources.addAll(familyMemberHistorys);
				}
				// AllergyIntolerance
				List<AllergyIntolerance> allergyIntolerances = DaoFactory.getAllergyIntoleranceDao()
						.findByCriteria(criteria);
				if (allergyIntolerances != null && allergyIntolerances.size() > 0) {
					resources.addAll(allergyIntolerances);
				}
				// Media
				List<Media> medias = DaoFactory.getMediaDao().findByCriteria(criteria);
				if (medias != null && medias.size() > 0) {
					resources.addAll(medias);
				}
				// NutritionOrder
				List<NutritionOrder> nutritionOrders = DaoFactory.getNutritionOrderDao().findByCriteria(criteria);
				if (nutritionOrders != null && nutritionOrders.size() > 0) {
					resources.addAll(nutritionOrders);
				}
				// RiskAssessment
				List<RiskAssessment> riskAssessments = DaoFactory.getRiskAssessmentDao().findByCriteria(criteria);
				if (riskAssessments != null && riskAssessments.size() > 0) {
					resources.addAll(riskAssessments);
				}
				// VisionPrescription
				List<VisionPrescription> visionPrescriptions = DaoFactory.getVisionPrescriptionDao().findByCriteria(criteria);
				if (visionPrescriptions != null && visionPrescriptions.size() > 0) {
					resources.addAll(visionPrescriptions);
				}
				// BodyStructure
				List<BodyStructure> bodyStructures = DaoFactory.getBodyStructureDao().findByCriteria(criteria);
				if (bodyStructures != null && bodyStructures.size() > 0) {
					resources.addAll(bodyStructures);
				}
				// QuestionnaireResponse
				List<QuestionnaireResponse> questionnaireResponses = DaoFactory.getQuestionnaireResponseDao().findByCriteria(criteria);
				if (questionnaireResponses != null && questionnaireResponses.size() > 0) {
					resources.addAll(questionnaireResponses);
				}
				// Composition
				List<Composition> compositions = DaoFactory.getCompositionDao().findByCriteria(criteria);
				if (compositions != null && compositions.size() > 0) {
					resources.addAll(compositions);
				}
				// DocumentReference
				List<DocumentReference> documentReferences = DaoFactory.getDocumentReferenceDao().findByCriteria(criteria);
				if (documentReferences != null && documentReferences.size() > 0) {
					resources.addAll(documentReferences);
				}
				
				return resources;
			}
		}

		return null;
	}
}
