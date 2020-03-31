package vn.ehealth.emr.controller;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.listOf;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;
import vn.ehealth.emr.model.EmrDichVuKyThuat;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

public class EmrDichVuKyThuatHelper {

    private static DiagnosticReport saveDiagnosticReport(DiagnosticReport obj) {        
        if(obj != null) {
            var diagnosticReportDao = DaoFactory.getDiagnosticReportDao();
            if(obj.hasId()) {
                return diagnosticReportDao.update(obj, obj.getIdElement());
            }else {
                return diagnosticReportDao.create(obj);
            }
        }
        return null;
    }
    
    private static ServiceRequest saveServiceRequest(ServiceRequest obj) {
        if(obj != null) {
            var serviceRequestDao = DaoFactory.getServiceRequestDao();
            if(obj.hasId()) {
                return serviceRequestDao.update(obj, obj.getIdElement());
            }else {
                return serviceRequestDao.create(obj);
            }
        }
        return null;
    }
    
    private static Procedure saveProcedure(Procedure obj) {
        if(obj != null) {
            var procedureDao = DaoFactory.getProcedureDao();
            if(obj.hasId()) {
                return procedureDao.update(obj, obj.getIdElement());
            }else {
                return procedureDao.create(obj);
            }
        }
        return null;
    }
    
    private static Specimen saveSpecimen(Specimen obj) {
        if((obj != null)) {
            var specimentDao = DaoFactory.getSpecimenDao();
            if(obj.hasId()) {
                return specimentDao.update(obj, obj.getIdElement());
            }else {
                return specimentDao.create(obj);
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static void saveDichVuKT(Encounter encounter,  EmrDichVuKyThuat dto) {
        if(dto == null || encounter == null) return;
        
        Map<String, Object> resources = dto.toFhir(encounter);
        
        var serviceRequest = (ServiceRequest) resources.get("serviceRequest");
        var procedure = (Procedure) resources.get("procedure");                
        var diagnosticReport = (DiagnosticReport) resources.get("diagnosticReport");
        var specimen = (Specimen) resources.get("specimen");
        var observations = (List<Observation>) resources.get("observations");
        if(observations == null) observations = new ArrayList<>();
        
        // Service Request
        serviceRequest = saveServiceRequest(serviceRequest);
        if(serviceRequest != null) {
            var ref = createReference(serviceRequest);
            if(procedure != null) procedure.setBasedOn(listOf(ref));
            if(diagnosticReport != null) diagnosticReport.setBasedOn(listOf(ref));
            if(specimen != null) specimen.setRequest(listOf(ref));
            observations.forEach(x -> x.setBasedOn(listOf(ref)));
        }
        
        // DiagnosticReport
        diagnosticReport = saveDiagnosticReport(diagnosticReport);
        if(diagnosticReport != null) {
            if(procedure != null) procedure.setReport(listOf(createReference(diagnosticReport)));                
        }
        
        // Procedure
        procedure = saveProcedure(procedure);
        if(procedure != null) {                
            var ref = createReference(procedure);
            observations.forEach(x -> x.setPartOf(listOf(ref)));
        }
        
        // Specimen            
        specimen = saveSpecimen(specimen);
        if(specimen != null) {
            serviceRequest.setSpecimen(listOf(createReference(specimen)));
            serviceRequest = saveServiceRequest(serviceRequest);
        }
        
        // Observation
        observations.forEach(x -> DaoFactory.getObservationDao().create(x));
    }
}
