package vn.ehealth.emr.dto.controller;

import java.util.Map;
import java.util.Optional;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.model.dto.ChanDoanHinhAnh;
import vn.ehealth.emr.service.DiagnosticReportService;
import vn.ehealth.emr.service.ProcedureService;
import vn.ehealth.emr.service.ServiceRequestService;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

@RestController
@RequestMapping("/api/chan_doan_hinh_anh")
public class ChanDoanHinhAnhController {

    private static Logger logger = LoggerFactory.getLogger(ChanDoanHinhAnhController.class);
    
    @Autowired private ProcedureService procedureService;
    @Autowired private DiagnosticReportService  diagnosticReportService;
    @Autowired private ServiceRequestService serviceRequestService;
        
    @GetMapping("/get_by_id")
    public ResponseEntity<?> getById(@RequestParam String id) {
        var obj = procedureService.getById(id);
        var dto = ChanDoanHinhAnh.fromFhir(obj);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAll() {
        var lst = DataConvertUtil.transform(procedureService.getAll(), x -> ChanDoanHinhAnh.fromFhir(x));
        return ResponseEntity.ok(lst);
    }
    
    private DiagnosticReport saveDiagnosticReport(DiagnosticReport obj) {
        return diagnosticReportService.save(obj);
    }
    
    private ServiceRequest saveServiceRequest(ServiceRequest obj) {
        return serviceRequestService.save(obj);
    }
    
    private Procedure saveProcedure(Procedure ent) {
        return procedureService.save(ent);
    }
    
    @PostMapping("/create_or_update")
    public ResponseEntity<?> createOrUpdate(@RequestBody ChanDoanHinhAnh dto) {
        try {
            Map<String, Resource> entities = ChanDoanHinhAnh.toFhir(dto);
            
            if(entities != null) {
                var procedure = (Procedure) entities.get("procedure");
                var serviceRequest = (ServiceRequest) entities.get("serviceRequest");
                var diagnosticReport = (DiagnosticReport) entities.get("diagnosticReport");
                                
                diagnosticReport = saveDiagnosticReport(diagnosticReport);
                serviceRequest = saveServiceRequest(serviceRequest);
                procedure = saveProcedure(procedure);
                
                var result = Map.of(
                                    "success", true, 
                                    "procedure", procedure,
                                    "serviceRequest", serviceRequest,
                                    "diagnosticReport", diagnosticReport
                                 );
                
                return ResponseEntity.ok(result);
            }
            var result = Map.of("success", false, "error", "Invalid data, null Entity");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            logger.error("Can not save entity: ", e);
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = Map.of("success", false, "error", error);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
