package vn.ehealth.emr.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ServiceFactory implements ApplicationContextAware  {

    private static ApplicationContext applicationContext;
    private static EmrHoSoBenhAnService emrHoSoBenhAnService;
    private static EmrBenhNhanService emrBenhNhanService;
    private static EmrCoSoKhamBenhService emrCoSoKhamBenhService;
    
    private static EmrChamSocService emrChamSocService;
    
    //private static EmrDieuTriService emrDieuTriService;
    
    //private static EmrChucNangSongService emrChucNangSongService;
    
    //private static EmrHoiChanService emrHoiChanService;
    
    private static EmrXetNghiemService emrXetNghiemService;
    
    private static UserService userService;
    private static RoleService roleService;
    
    private static PatientService patientService;
    private static PractitionerService practitionerService;
    private static LocationService locationService;
    private static EncounterService encounterService;
    private static DiagnosticReportService diagnosticReportService;
    private static ServiceRequestService serviceRequestService;
    private static ProcedureService procedureService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ServiceFactory.applicationContext = applicationContext;        
    }
    
    public static EmrHoSoBenhAnService getEmrHoSoBenhAnService() {
        if(emrHoSoBenhAnService == null) {
            emrHoSoBenhAnService = applicationContext.getBean(EmrHoSoBenhAnService.class);
        }
        return emrHoSoBenhAnService;
    }
    
    public static EmrBenhNhanService getEmrBenhNhanService() {
        if(emrBenhNhanService == null) {
            emrBenhNhanService = applicationContext.getBean(EmrBenhNhanService.class);
        }
        return emrBenhNhanService;
    }
    
    public static EmrCoSoKhamBenhService getEmrCoSoKhamBenhService() {
        if(emrCoSoKhamBenhService == null) {
            emrCoSoKhamBenhService = applicationContext.getBean(EmrCoSoKhamBenhService.class); 
        }
        return emrCoSoKhamBenhService;        
    }
    
    public static EmrChamSocService getEmrChamSocService() {
        if(emrChamSocService == null) {
            emrChamSocService = applicationContext.getBean(EmrChamSocService.class);
        }
        
        return emrChamSocService;
    }
    
    public static EmrXetNghiemService getEmrXetNghiemService() {
        if(emrXetNghiemService == null) {
            emrXetNghiemService = applicationContext.getBean(EmrXetNghiemService.class);
        }
        
        return emrXetNghiemService;
    }
    
    public static UserService getUserService() {
        if(userService == null) {
            userService = applicationContext.getBean(UserService.class);
        }
        return userService;
    }
    
    public static RoleService getRoleService() {
        if(roleService == null) {
            roleService = applicationContext.getBean(RoleService.class);
        }
        return roleService;
    }
    
    public static PatientService getPatientService() {
        if(patientService == null) {
            patientService = applicationContext.getBean(PatientService.class);
        }
        return patientService;
    }
    
    public static PractitionerService getPractitionerService() {
        if(practitionerService == null) {
            practitionerService = applicationContext.getBean(PractitionerService.class);
        }
        return practitionerService;
    }
    
    public static EncounterService getEncounterService() {
        if(encounterService == null) {
            encounterService = applicationContext.getBean(EncounterService.class);
        }
        return encounterService;
    }
    
    public static LocationService getLocationService() {
        if(locationService == null) {
            locationService = applicationContext.getBean(LocationService.class);
        }
        return locationService;
    }
    
    public static DiagnosticReportService getDiagnosticReportService() {
        if(diagnosticReportService == null) {
            diagnosticReportService = applicationContext.getBean(DiagnosticReportService.class);
        }
        return diagnosticReportService;
    }
    
    public static ServiceRequestService getServiceRequestService() {
        if(serviceRequestService == null) {
            serviceRequestService = applicationContext.getBean(ServiceRequestService.class);
        }
        return serviceRequestService;
    }
    
    public static ProcedureService getProcedureService() {
        if(procedureService == null) {
            procedureService = applicationContext.getBean(ProcedureService.class);
        }
        return procedureService;
    }
}
