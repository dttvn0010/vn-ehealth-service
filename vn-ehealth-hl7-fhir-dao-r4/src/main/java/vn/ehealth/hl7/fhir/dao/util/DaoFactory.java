package vn.ehealth.hl7.fhir.dao.util;

import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.base.dao.impl.BinaryDao;
import vn.ehealth.hl7.fhir.base.dao.impl.BundleDao;
import vn.ehealth.hl7.fhir.base.dao.impl.SubscriptionDao;
import vn.ehealth.hl7.fhir.careprovision.dao.impl.NutritionOrderDao;
import vn.ehealth.hl7.fhir.careprovision.dao.impl.RequestGroupDao;
import vn.ehealth.hl7.fhir.careprovision.dao.impl.RiskAssessmentDao;
import vn.ehealth.hl7.fhir.careprovision.dao.impl.VisionPrescriptionDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.AllergyIntoleranceDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.CarePlanDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ClinicalImpressionDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ConditionDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.DetectedIssueDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.FamilyMemberHistoryDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.GoalDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ProcedureDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ServiceRequestDao;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.definitionalartifact.dao.impl.ActivityDefinitionDao;
import vn.ehealth.hl7.fhir.definitionalartifact.dao.impl.PlanDefinitionDao;
import vn.ehealth.hl7.fhir.definitionalartifact.dao.impl.QuestionnaireDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.BodyStructureDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.DiagnosticReportDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.ImagingStudyDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.MediaDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.ObservationDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.QuestionnaireResponseDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.SpecimenDao;
import vn.ehealth.hl7.fhir.document.dao.impl.CompositionDao;
import vn.ehealth.hl7.fhir.document.dao.impl.DocumentManifestDao;
import vn.ehealth.hl7.fhir.document.dao.impl.DocumentReferenceDao;
import vn.ehealth.hl7.fhir.ehr.dao.impl.CareTeamDao;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EpisodeOfCareDao;
import vn.ehealth.hl7.fhir.medication.dao.impl.ImmunizationDao;
import vn.ehealth.hl7.fhir.medication.dao.impl.MedicationAdministrationDao;
import vn.ehealth.hl7.fhir.medication.dao.impl.MedicationDao;
import vn.ehealth.hl7.fhir.medication.dao.impl.MedicationDispenseDao;
import vn.ehealth.hl7.fhir.medication.dao.impl.MedicationRequestDao;
import vn.ehealth.hl7.fhir.medication.dao.impl.MedicationStatementDao;
import vn.ehealth.hl7.fhir.patient.dao.impl.PatientDao;
import vn.ehealth.hl7.fhir.patient.dao.impl.RelatedPersonDao;
import vn.ehealth.hl7.fhir.product.dao.impl.SubstanceDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.DeviceDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.HealthcareServiceDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.LocationDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.OrganizationDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.PractitionerDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.PractitionerRoleDao;
import vn.ehealth.hl7.fhir.schedule.dao.impl.AppointmentDao;
import vn.ehealth.hl7.fhir.schedule.dao.impl.AppointmentResponseDao;
import vn.ehealth.hl7.fhir.schedule.dao.impl.ScheduleDao;
import vn.ehealth.hl7.fhir.schedule.dao.impl.SlotDao;
import vn.ehealth.hl7.fhir.security.dao.impl.AuditEventDao;
import vn.ehealth.hl7.fhir.security.dao.impl.ConsentDao;
import vn.ehealth.hl7.fhir.security.dao.impl.ProvenanceDao;
import vn.ehealth.hl7.fhir.term.dao.impl.CodeSystemDao;
import vn.ehealth.hl7.fhir.term.dao.impl.ConceptMapDao;
import vn.ehealth.hl7.fhir.term.dao.impl.ValueSetDao;
import vn.ehealth.hl7.fhir.user.dao.impl.GroupDao;
import vn.ehealth.hl7.fhir.user.dao.impl.PersonDao;
import vn.ehealth.hl7.fhir.workflow.dao.impl.TaskDao;

@Component
public class DaoFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static PatientDao patientDao;
    private static EncounterDao encounterDao;
    
    private static ProcedureDao procedureDao;
    private static DiagnosticReportDao diagnosticReportDao;
    private static ServiceRequestDao serviceRequestDao;
    private static ObservationDao observationDao;
    
    private static CarePlanDao carePlanDao;
    private static ClinicalImpressionDao clinicalImpressionDao;
    private static ConditionDao conditionDao;
    private static DetectedIssueDao detectedIssueDao;
    private static GoalDao goalDao;
    
    private static ImagingStudyDao imagingStudyDao;
    private static SpecimenDao specimenDao;
    
    private static CareTeamDao careTeamDao;
    private static EpisodeOfCareDao episodeOfCareDao;
    private static RelatedPersonDao relatedPersonDao;
    
    private static ImmunizationDao immunizationDao;
    private static MedicationAdministrationDao medicationAdministrationDao;
    private static MedicationDao medicationDao;
    private static MedicationDispenseDao medicationDispenseDao;
    private static MedicationRequestDao medicationRequestDao;
    private static MedicationStatementDao medicationStatementDao;
    
    private static DeviceDao deviceDao;
    private static HealthcareServiceDao healthcareServiceDao;
    private static PractitionerDao practitionerDao;
    private static LocationDao locationDao;
    private static OrganizationDao organizationDao;
    private static PractitionerRoleDao practitionerRoleDao;
    
    private static AppointmentDao appointmentDao;
    private static AppointmentResponseDao appointmentResponseDao;
    private static ScheduleDao scheduleDao;
    private static SlotDao slotDao;
    
    private static CodeSystemDao codeSystemDao;
    private static ConceptMapDao conceptMapDao;
    private static ValueSetDao valueSetDao;
    
    private static PersonDao personDao;
    
    private static AllergyIntoleranceDao allergyIntoleranceDao;
    private static FamilyMemberHistoryDao familyMemberHistoryDao;
    private static MediaDao mediaDao;
    
    private static BinaryDao binaryDao;
    private static BundleDao bundleDao;
    private static SubscriptionDao subscriptionDao;
    
    private static CompositionDao compositionDao;
    private static DocumentManifestDao documentManifestDao;
    private static DocumentReferenceDao documentReferenceDao;
    
    private static NutritionOrderDao nutritionOrderDao;
    private static RequestGroupDao requestGroupDao;
    private static RiskAssessmentDao riskAssessmentDao;
    private static VisionPrescriptionDao visionPrescriptionDao;
    
    private static ActivityDefinitionDao activityDefinitionDao;
    private static PlanDefinitionDao planDefinitionDao;
    private static QuestionnaireDao questionnaireDao;
    
    private static BodyStructureDao bodyStructureDao;
    private static QuestionnaireResponseDao questionnaireResponseDao;
    
    private static AuditEventDao auditEventDao;
    private static ConsentDao consentDao;
    private static ProvenanceDao provenanceDao;
    
    private static GroupDao groupDao;
    private static SubstanceDao substanceDao;
    private static TaskDao taskDao;
    
	
	public static BaseDao<?,?> getDaoByType(ResourceType resourceType) {		
		switch (resourceType) {
		case CarePlan: return getCarePlanDao();
		case Patient: return getPatientDao();
		case Encounter: return getEncounterDao();
		case ClinicalImpression: return getClinicalImpressionDao();
		case Condition: return getConditionDao();
		case DetectedIssue: return getDetectedIssueDao();
		case Goal: return getGoalDao();
		case Procedure: return getProcedureDao();
		case ServiceRequest: return getServiceRequestDao();
		case DiagnosticReport: return getDiagnosticReportDao();
		case ImagingStudy: return getImagingStudyDao();
		case Observation: return getObservationDao();
		case Specimen: return getSpecimenDao();
		case CareTeam: return getCareTeamDao();
		case EpisodeOfCare: return getEpisodeOfCareDao();
		case Immunization: return getImmunizationDao();
		case MedicationAdministration: return getMedicationAdministrationDao();
		case MedicationDispense: return getMedicationDispenseDao();
		case Medication: return getMedicationDao();
		case MedicationRequest: return getMedicationRequestDao();
		case MedicationStatement: return getMedicationStatementDao();
		case RelatedPerson: return getRelatedPersonDao();
		case Device: return getDeviceDao();
		case HealthcareService: return getHealthcareServiceDao();
		case Location: return getLocationDao();
		case Organization: return getOrganizationDao();
		case Practitioner: return getPractitionerDao();
		case PractitionerRole: return getPractitionerRoleDao();
		case Appointment: return getAppointmentDao();
		case AppointmentResponse: return getAppointmentResponseDao();
		case Schedule: return getScheduleDao();
		case Slot: return getSlotDao();
		case CodeSystem: return getCodeSystemDao();
		case ConceptMap: return getConceptMapDao();
		case ValueSet: return getValueSetDao();
		case Person: return getPersonDao();
		case AllergyIntolerance: return getAllergyIntoleranceDao();
		case FamilyMemberHistory: return getFamilyMemberHistoryDao();
		case Media: return getMediaDao();
		case Binary: return getBinaryDao();
		case Bundle: return getBundleDao();
		case Composition: return getCompositionDao();
		case DocumentManifest: return getDocumentManifestDao();
		case DocumentReference: return getDocumentReferenceDao();
		case NutritionOrder: return getNutritionOrderDao();
		case Subscription: return getSubscriptionDao();
		case RequestGroup: return getRequestGroupDao();
		case RiskAssessment: return getRiskAssessmentDao();
		case VisionPrescription: return getVisionPrescriptionDao();
		case ActivityDefinition: return getActivityDefinitionDao();
		case PlanDefinition: return getPlanDefinitionDao();
		case Questionnaire: return getQuestionnaireDao();
		case QuestionnaireResponse: return getQuestionnaireResponseDao();
		case BodyStructure: return getBodyStructureDao();
		case Substance: return getSubstanceDao();
		case AuditEvent: return getAuditEventDao();
		case Consent: return getConsentDao();
		case Provenance: return getProvenanceDao();
		case Group: return getGroupDao();
		case Task: return getTaskDao();		
		
		default: return null;
		}
	}

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DaoFactory.applicationContext = applicationContext;        
    }
    
    public static PatientDao getPatientDao() {
        if(patientDao == null) {
            patientDao = applicationContext.getBean(PatientDao.class);
        }
        return patientDao;
    }
    
    public static PractitionerDao getPractitionerDao() {
        if(practitionerDao == null) {
            practitionerDao = applicationContext.getBean(PractitionerDao.class);
        }
        return practitionerDao;
    }
    
    public static LocationDao getLocationDao() {
        if(locationDao == null) {
            locationDao = applicationContext.getBean(LocationDao.class);
        }
        return locationDao;
    }
    
    public static EncounterDao getEncounterDao() {
        if(encounterDao == null) {
            encounterDao = applicationContext.getBean(EncounterDao.class);
        }
        return encounterDao;
    }
    
    public static ProcedureDao getProcedureDao() {
        if(procedureDao == null) {
            procedureDao = applicationContext.getBean(ProcedureDao.class);
        }
        return procedureDao;
    }
    
    public static DiagnosticReportDao getDiagnosticReportDao() {
        if(diagnosticReportDao == null) {
            diagnosticReportDao = applicationContext.getBean(DiagnosticReportDao.class);
        }
        return diagnosticReportDao;
    }
    
    public static ServiceRequestDao getServiceRequestDao() {
        if(serviceRequestDao == null) {
            serviceRequestDao = applicationContext.getBean(ServiceRequestDao.class);
        }
        return serviceRequestDao;
    }

    public static ObservationDao getObservationDao() {
        if(observationDao == null) {
        	observationDao = applicationContext.getBean(ObservationDao.class);
        }
        return observationDao;
    }
    
    public static CarePlanDao getCarePlanDao() {
        if(carePlanDao == null) {
        	carePlanDao = applicationContext.getBean(CarePlanDao.class);
        }
        return carePlanDao;
    }
    
    public static ClinicalImpressionDao getClinicalImpressionDao() {
        if(clinicalImpressionDao == null) {
        	clinicalImpressionDao = applicationContext.getBean(ClinicalImpressionDao.class);
        }
        return clinicalImpressionDao;
    }
    
    public static ConditionDao getConditionDao() {
        if(conditionDao == null) {
        	conditionDao = applicationContext.getBean(ConditionDao.class);
        }
        return conditionDao;
    }
    
    public static DetectedIssueDao getDetectedIssueDao() {
        if(detectedIssueDao == null) {
        	detectedIssueDao = applicationContext.getBean(DetectedIssueDao.class);
        }
        return detectedIssueDao;
    }
    
    public static GoalDao getGoalDao() {
        if(goalDao == null) {
        	goalDao = applicationContext.getBean(GoalDao.class);
        }
        return goalDao;
    }
    
    public static ImagingStudyDao getImagingStudyDao() {
        if(imagingStudyDao == null) {
        	imagingStudyDao = applicationContext.getBean(ImagingStudyDao.class);
        }
        return imagingStudyDao;
    }
    
    public static SpecimenDao getSpecimenDao() {
        if(specimenDao == null) {
        	specimenDao = applicationContext.getBean(SpecimenDao.class);
        }
        return specimenDao;
    }
    
    public static CareTeamDao getCareTeamDao() {
        if(careTeamDao == null) {
        	careTeamDao = applicationContext.getBean(CareTeamDao.class);
        }
        return careTeamDao;
    }
    
    public static EpisodeOfCareDao getEpisodeOfCareDao() {
        if(episodeOfCareDao == null) {
        	episodeOfCareDao = applicationContext.getBean(EpisodeOfCareDao.class);
        }
        return episodeOfCareDao;
    }
    
    public static ImmunizationDao getImmunizationDao() {
        if(immunizationDao == null) {
        	immunizationDao = applicationContext.getBean(ImmunizationDao.class);
        }
        return immunizationDao;
    }
    
    public static MedicationAdministrationDao getMedicationAdministrationDao() {
        if(medicationAdministrationDao == null) {
        	medicationAdministrationDao = applicationContext.getBean(MedicationAdministrationDao.class);
        }
        return medicationAdministrationDao;
    }
    
    public static MedicationDao getMedicationDao() {
        if(medicationDao == null) {
        	medicationDao = applicationContext.getBean(MedicationDao.class);
        }
        return medicationDao;
    }
    
    public static MedicationDispenseDao getMedicationDispenseDao() {
        if(medicationDispenseDao == null) {
        	medicationDispenseDao = applicationContext.getBean(MedicationDispenseDao.class);
        }
        return medicationDispenseDao;
    }
    
    public static MedicationRequestDao getMedicationRequestDao() {
        if(medicationRequestDao == null) {
        	medicationRequestDao = applicationContext.getBean(MedicationRequestDao.class);
        }
        return medicationRequestDao;
    }
    
    public static MedicationStatementDao getMedicationStatementDao() {
        if(medicationStatementDao == null) {
        	medicationStatementDao = applicationContext.getBean(MedicationStatementDao.class);
        }
        return medicationStatementDao;
    }
    
    public static RelatedPersonDao getRelatedPersonDao() {
        if(relatedPersonDao == null) {
        	relatedPersonDao = applicationContext.getBean(RelatedPersonDao.class);
        }
        return relatedPersonDao;
    }
    
    public static DeviceDao getDeviceDao() {
        if(deviceDao == null) {
        	deviceDao = applicationContext.getBean(DeviceDao.class);
        }
        return deviceDao;
    }
    
    public static HealthcareServiceDao getHealthcareServiceDao() {
        if(healthcareServiceDao == null) {
        	healthcareServiceDao = applicationContext.getBean(HealthcareServiceDao.class);
        }
        return healthcareServiceDao;
    }
    
    public static OrganizationDao getOrganizationDao() {
        if(organizationDao == null) {
        	organizationDao = applicationContext.getBean(OrganizationDao.class);
        }
        return organizationDao;
    }
    
    public static PractitionerRoleDao getPractitionerRoleDao() {
        if(practitionerRoleDao == null) {
        	practitionerRoleDao = applicationContext.getBean(PractitionerRoleDao.class);
        }
        return practitionerRoleDao;
    }
    
    public static AppointmentDao getAppointmentDao() {
        if(appointmentDao == null) {
        	appointmentDao = applicationContext.getBean(AppointmentDao.class);
        }
        return appointmentDao;
    }
    
    public static AppointmentResponseDao getAppointmentResponseDao() {
        if(appointmentResponseDao == null) {
        	appointmentResponseDao = applicationContext.getBean(AppointmentResponseDao.class);
        }
        return appointmentResponseDao;
    }
    
    public static ScheduleDao getScheduleDao() {
        if(scheduleDao == null) {
        	scheduleDao = applicationContext.getBean(ScheduleDao.class);
        }
        return scheduleDao;
    }
    
    public static SlotDao getSlotDao() {
        if(slotDao == null) {
        	slotDao = applicationContext.getBean(SlotDao.class);
        }
        return slotDao;
    }
    
    public static CodeSystemDao getCodeSystemDao() {
        if(codeSystemDao == null) {
        	codeSystemDao = applicationContext.getBean(CodeSystemDao.class);
        }
        return codeSystemDao;
    }
    
    public static ConceptMapDao getConceptMapDao() {
        if(conceptMapDao == null) {
        	conceptMapDao = applicationContext.getBean(ConceptMapDao.class);
        }
        return conceptMapDao;
    }
    
    public static ValueSetDao getValueSetDao() {
        if(valueSetDao == null) {
        	valueSetDao = applicationContext.getBean(ValueSetDao.class);
        }
        return valueSetDao;
    }
    
    public static PersonDao getPersonDao() {
        if(personDao == null) {
        	personDao = applicationContext.getBean(PersonDao.class);
        }
        return personDao;
    }
    
    public static FamilyMemberHistoryDao getFamilyMemberHistoryDao() {
        if(familyMemberHistoryDao == null) {
        	familyMemberHistoryDao = applicationContext.getBean(FamilyMemberHistoryDao.class);
        }
        return familyMemberHistoryDao;
    }
    
    public static AllergyIntoleranceDao getAllergyIntoleranceDao() {
        if(allergyIntoleranceDao == null) {
        	allergyIntoleranceDao = applicationContext.getBean(AllergyIntoleranceDao.class);
        }
        return allergyIntoleranceDao;
    }
    
    public static MediaDao getMediaDao() {
        if(mediaDao == null) {
        	mediaDao = applicationContext.getBean(MediaDao.class);
        }
        return mediaDao;
    }
    
    public static BinaryDao getBinaryDao() {
        if(binaryDao == null) {
        	binaryDao = applicationContext.getBean(BinaryDao.class);
        }
        return binaryDao;
    }
    
    public static BundleDao getBundleDao() {
        if(bundleDao == null) {
        	bundleDao = applicationContext.getBean(BundleDao.class);
        }
        return bundleDao;
    }
    
    public static CompositionDao getCompositionDao() {
        if(compositionDao == null) {
        	compositionDao = applicationContext.getBean(CompositionDao.class);
        }
        return compositionDao;
    }
    
    public static DocumentManifestDao getDocumentManifestDao() {
        if(documentManifestDao == null) {
        	documentManifestDao = applicationContext.getBean(DocumentManifestDao.class);
        }
        return documentManifestDao;
    }
    
    public static DocumentReferenceDao getDocumentReferenceDao() {
        if(documentReferenceDao == null) {
        	documentReferenceDao = applicationContext.getBean(DocumentReferenceDao.class);
        }
        return documentReferenceDao;
    }
    
    public static NutritionOrderDao getNutritionOrderDao() {
        if(nutritionOrderDao == null) {
        	nutritionOrderDao = applicationContext.getBean(NutritionOrderDao.class);
        }
        return nutritionOrderDao;
    }
    
    public static SubscriptionDao getSubscriptionDao() {
        if(subscriptionDao == null) {
        	subscriptionDao = applicationContext.getBean(SubscriptionDao.class);
        }
        return subscriptionDao;
    }
    
    public static RequestGroupDao getRequestGroupDao() {
        if(requestGroupDao == null) {
        	requestGroupDao = applicationContext.getBean(RequestGroupDao.class);
        }
        return requestGroupDao;
    }
    
    public static RiskAssessmentDao getRiskAssessmentDao() {
        if(riskAssessmentDao == null) {
        	riskAssessmentDao = applicationContext.getBean(RiskAssessmentDao.class);
        }
        return riskAssessmentDao;
    }
    
    public static VisionPrescriptionDao getVisionPrescriptionDao() {
        if(visionPrescriptionDao == null) {
        	visionPrescriptionDao = applicationContext.getBean(VisionPrescriptionDao.class);
        }
        return visionPrescriptionDao;
    }
    
    public static ActivityDefinitionDao getActivityDefinitionDao() {
        if(activityDefinitionDao == null) {
        	activityDefinitionDao = applicationContext.getBean(ActivityDefinitionDao.class);
        }
        return activityDefinitionDao;
    }
    
    public static PlanDefinitionDao getPlanDefinitionDao() {
        if(planDefinitionDao == null) {
        	planDefinitionDao = applicationContext.getBean(PlanDefinitionDao.class);
        }
        return planDefinitionDao;
    }
    
    public static QuestionnaireDao getQuestionnaireDao() {
        if(questionnaireDao == null) {
        	questionnaireDao = applicationContext.getBean(QuestionnaireDao.class);
        }
        return questionnaireDao;
    }
    
    public static BodyStructureDao getBodyStructureDao() {
        if(bodyStructureDao == null) {
        	bodyStructureDao = applicationContext.getBean(BodyStructureDao.class);
        }
        return bodyStructureDao;
    }
    
    public static QuestionnaireResponseDao getQuestionnaireResponseDao() {
        if(questionnaireResponseDao == null) {
        	questionnaireResponseDao = applicationContext.getBean(QuestionnaireResponseDao.class);
        }
        return questionnaireResponseDao;
    }
    
    public static SubstanceDao getSubstanceDao() {
        if(substanceDao == null) {
        	substanceDao = applicationContext.getBean(SubstanceDao.class);
        }
        return substanceDao;
    }
    
    public static AuditEventDao getAuditEventDao() {
        if(auditEventDao == null) {
        	auditEventDao = applicationContext.getBean(AuditEventDao.class);
        }
        return auditEventDao;
    }
    
    public static ConsentDao getConsentDao() {
        if(consentDao == null) {
        	consentDao = applicationContext.getBean(ConsentDao.class);
        }
        return consentDao;
    }
    
    public static ProvenanceDao getProvenanceDao() {
        if(provenanceDao == null) {
        	provenanceDao = applicationContext.getBean(ProvenanceDao.class);
        }
        return provenanceDao;
    }
    
    public static GroupDao getGroupDao() {
        if(groupDao == null) {
        	groupDao = applicationContext.getBean(GroupDao.class);
        }
        return groupDao;
    }
    
    public static TaskDao getTaskDao() {
        if(taskDao == null) {
        	taskDao = applicationContext.getBean(TaskDao.class);
        }
        return taskDao;
    }
}
