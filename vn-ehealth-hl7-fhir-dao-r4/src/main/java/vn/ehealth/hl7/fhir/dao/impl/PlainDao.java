package vn.ehealth.hl7.fhir.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.InstantType;
import org.springframework.stereotype.Repository;
import ca.uhn.fhir.rest.param.DateRangeParam;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

@Repository
public class PlainDao {

	public List<IBaseResource> history(InstantType theSince, DateRangeParam theAt) {
		List<IBaseResource> resources = new ArrayList<IBaseResource>();

		// Patient
		List<IBaseResource> patients = DaoFactory.getPatientDao().getHistory(null, theSince, theAt, null, 0);
		if (patients != null && patients.size() > 0) {
			resources.addAll(patients);
		}
		// Encounter
		List<IBaseResource> enconters = DaoFactory.getEncounterDao().getHistory(null, theSince, theAt, null, 0);
		if (enconters != null && enconters.size() > 0) {
			resources.addAll(enconters);
		}
		// EpisodeOfCare
		List<IBaseResource> episodeOfCares = DaoFactory.getEpisodeOfCareDao().getHistory(null, theSince, theAt, null,
				0);
		if (episodeOfCares != null && episodeOfCares.size() > 0) {
			resources.addAll(episodeOfCares);
		}
		// CarePlan
		List<IBaseResource> carePlans = DaoFactory.getCarePlanDao().getHistory(null, theSince, theAt, null, 0);
		if (carePlans != null && carePlans.size() > 0) {
			resources.addAll(carePlans);
		}
		// Condition
		List<IBaseResource> conditions = DaoFactory.getConditionDao().getHistory(null, theSince, theAt, null, 0);
		if (conditions != null && conditions.size() > 0) {
			resources.addAll(conditions);
		}
		// DetectedIssue
		List<IBaseResource> detectedIssues = DaoFactory.getDetectedIssueDao().getHistory(null, theSince, theAt, null,
				0);
		if (detectedIssues != null && detectedIssues.size() > 0) {
			resources.addAll(detectedIssues);
		}
		// Goal
		List<IBaseResource> goals = DaoFactory.getGoalDao().getHistory(null, theSince, theAt, null, 0);
		if (goals != null && goals.size() > 0) {
			resources.addAll(goals);
		}
		// Procedure
		List<IBaseResource> procedures = DaoFactory.getProcedureDao().getHistory(null, theSince, theAt, null, 0);
		if (procedures != null && procedures.size() > 0) {
			resources.addAll(procedures);
		}
		// ServiceRequest
		List<IBaseResource> serviceRequests = DaoFactory.getServiceRequestDao().getHistory(null, theSince, theAt, null,
				0);
		if (serviceRequests != null && serviceRequests.size() > 0) {
			resources.addAll(serviceRequests);
		}
		// DiagnosticReport
		List<IBaseResource> diagnosticReports = DaoFactory.getDiagnosticReportDao().getHistory(null, theSince, theAt,
				null, 0);
		if (diagnosticReports != null && diagnosticReports.size() > 0) {
			resources.addAll(diagnosticReports);
		}
		// ImagingStudy
		List<IBaseResource> imagingStudys = DaoFactory.getImagingStudyDao().getHistory(null, theSince, theAt, null, 0);
		if (imagingStudys != null && imagingStudys.size() > 0) {
			resources.addAll(imagingStudys);
		}
		// Specimen
		List<IBaseResource> specimens = DaoFactory.getSpecimenDao().getHistory(null, theSince, theAt, null, 0);
		if (specimens != null && specimens.size() > 0) {
			resources.addAll(specimens);
		}
		// CareTeam
		List<IBaseResource> careTeams = DaoFactory.getCareTeamDao().getHistory(null, theSince, theAt, null, 0);
		if (careTeams != null && careTeams.size() > 0) {
			resources.addAll(careTeams);
		}
		// Immunization
		List<IBaseResource> immunizations = DaoFactory.getImmunizationDao().getHistory(null, theSince, theAt, null, 0);
		if (immunizations != null && immunizations.size() > 0) {
			resources.addAll(immunizations);
		}
		// MedicationAdministration
		List<IBaseResource> medicationAdministrations = DaoFactory.getMedicationAdministrationDao().getHistory(null,
				theSince, theAt, null, 0);
		if (medicationAdministrations != null && medicationAdministrations.size() > 0) {
			resources.addAll(medicationAdministrations);
		}
		// MedicationDispense
		List<IBaseResource> medicationDispenses = DaoFactory.getMedicationDispenseDao().getHistory(null, theSince,
				theAt, null, 0);
		if (medicationDispenses != null && medicationDispenses.size() > 0) {
			resources.addAll(medicationDispenses);
		}
		// MedicationRequest
		List<IBaseResource> medicationRequests = DaoFactory.getMedicationRequestDao().getHistory(null, theSince, theAt,
				null, 0);
		if (medicationRequests != null && medicationRequests.size() > 0) {
			resources.addAll(medicationRequests);
		}
		// MedicationStatement
		List<IBaseResource> medicationStatements = DaoFactory.getMedicationStatementDao().getHistory(null, theSince,
				theAt, null, 0);
		if (medicationStatements != null && medicationStatements.size() > 0) {
			resources.addAll(medicationStatements);
		}
		// RelatedPerson
		List<IBaseResource> relatedPersons = DaoFactory.getRelatedPersonDao().getHistory(null, theSince, theAt, null,
				0);
		if (relatedPersons != null && relatedPersons.size() > 0) {
			resources.addAll(relatedPersons);
		}
		// Observation
		List<IBaseResource> observations = DaoFactory.getObservationDao().getHistory(null, theSince, theAt, null, 0);
		if (observations != null && observations.size() > 0) {
			resources.addAll(observations);
		}
		// FamilyMemberHistory
		List<IBaseResource> familyMemberHistorys = DaoFactory.getFamilyMemberHistoryDao().getHistory(null, theSince,
				theAt, null, 0);
		if (familyMemberHistorys != null && familyMemberHistorys.size() > 0) {
			resources.addAll(familyMemberHistorys);
		}
		// AllergyIntolerance
		List<IBaseResource> allergyIntolerances = DaoFactory.getAllergyIntoleranceDao().getHistory(null, theSince,
				theAt, null, 0);
		if (allergyIntolerances != null && allergyIntolerances.size() > 0) {
			resources.addAll(allergyIntolerances);
		}
		// Media
		List<IBaseResource> medias = DaoFactory.getMediaDao().getHistory(null, theSince, theAt, null, 0);
		if (medias != null && medias.size() > 0) {
			resources.addAll(medias);
		}
		return resources;
	}

}
