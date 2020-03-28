package vn.ehealth.hl7.fhir.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DetectedIssue;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.EpisodeOfCare;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.ImagingStudy;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Media;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.rest.param.DateRangeParam;
import vn.ehealth.hl7.fhir.dao.iPlainDao;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

@Service
public class PlainDao implements iPlainDao{

	public List<IBaseResource> history(InstantType theSince, DateRangeParam theAt) {
		List<IBaseResource> resources = new ArrayList<IBaseResource>();
		Criteria criteria = Criteria.where("1 =1 ");

		// Patient
		List<Patient> patients = DaoFactory.getPatientDao().findByCriteria(criteria);
		if (patients != null && patients.size() > 0) {
			resources.addAll(patients);
		}
		// Encounter
		List<Encounter> enconters = DaoFactory.getEncounterDao().findByCriteria(criteria);
		if (enconters != null && enconters.size() > 0) {
			resources.addAll(enconters);
		}
		// EpisodeOfCare
		List<EpisodeOfCare> episodeOfCares = DaoFactory.getEpisodeOfCareDao().findByCriteria(criteria);
		if (episodeOfCares != null && episodeOfCares.size() > 0) {
			resources.addAll(episodeOfCares);
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
		List<MedicationDispense> medicationDispenses = DaoFactory.getMedicationDispenseDao().findByCriteria(criteria);
		if (medicationDispenses != null && medicationDispenses.size() > 0) {
			resources.addAll(medicationDispenses);
		}
		// MedicationRequest
		List<MedicationRequest> medicationRequests = DaoFactory.getMedicationRequestDao().findByCriteria(criteria);
		if (medicationRequests != null && medicationRequests.size() > 0) {
			resources.addAll(medicationRequests);
		}
		// MedicationStatement
		List<MedicationStatement> medicationStatements = DaoFactory.getMedicationStatementDao()
				.findByCriteria(criteria);
		if (medicationStatements != null && medicationStatements.size() > 0) {
			resources.addAll(medicationStatements);
		}
		// RelatedPerson
		List<RelatedPerson> relatedPersons = DaoFactory.getRelatedPersonDao().findByCriteria(criteria);
		if (relatedPersons != null && relatedPersons.size() > 0) {
			resources.addAll(relatedPersons);
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
		List<AllergyIntolerance> allergyIntolerances = DaoFactory.getAllergyIntoleranceDao().findByCriteria(criteria);
		if (allergyIntolerances != null && allergyIntolerances.size() > 0) {
			resources.addAll(allergyIntolerances);
		}
		// Media
		List<Media> medias = DaoFactory.getMediaDao().findByCriteria(criteria);
		if (medias != null && medias.size() > 0) {
			resources.addAll(medias);
		}
		return resources;
	}

}
