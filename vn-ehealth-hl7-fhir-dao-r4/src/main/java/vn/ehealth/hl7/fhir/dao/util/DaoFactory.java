package vn.ehealth.hl7.fhir.dao.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.clinical.dao.impl.CarePlanDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ClinicalImpressionDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ConditionDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.DetectedIssueDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.GoalDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ProcedureDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ServiceRequestDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.DiagnosticReportDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.ImagingStudyDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.ObservationDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.SpecimenDao;
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
import vn.ehealth.hl7.fhir.term.dao.impl.CodeSystemDao;
import vn.ehealth.hl7.fhir.term.dao.impl.ConceptMapDao;
import vn.ehealth.hl7.fhir.term.dao.impl.ValueSetDao;
import vn.ehealth.hl7.fhir.user.dao.impl.PersonDao;

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
        if(diagnosticReportDao == null) {
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
}
