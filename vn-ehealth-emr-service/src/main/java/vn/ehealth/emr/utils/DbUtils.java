package vn.ehealth.emr.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.clinical.dao.impl.ProcedureDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ServiceRequestDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.DiagnosticReportDao;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.patient.dao.impl.PatientDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.LocationDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.PractitionerDao;

@Component
public class DbUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static PatientDao patientDao;
    private static PractitionerDao practitionerDao;
    private static LocationDao locationDao;
    private static EncounterDao encounterDao;
    private static ProcedureDao procedureDao;
    private static DiagnosticReportDao diagnosticReportDao;
    private static ServiceRequestDao serviceRequestDao;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DbUtils.applicationContext = applicationContext;        
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
    
}
