package vn.ehealth.emr.dto.controller;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

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
import vn.ehealth.hl7.fhir.clinical.entity.ProcedureEntity;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.diagnostic.entity.DiagnosticReportEntity;
import vn.ehealth.hl7.fhir.diagnostic.entity.ServiceRequestEntity;

@RestController
@RequestMapping("/api/chan_doan_hinh_anh")
public class ChanDoanHinhAnhController {

    private static Logger logger = LoggerFactory.getLogger(ChanDoanHinhAnhController.class);
    
    @Autowired private ProcedureService procedureService;
    @Autowired private DiagnosticReportService  diagnosticReportService;
    @Autowired private ServiceRequestService serviceRequestService;
        
    @GetMapping("/get_by_id")
    public ResponseEntity<?> getById(@RequestParam String fhirId) {
        var ent = procedureService.getByFhirId(fhirId).get();
        var dto = ChanDoanHinhAnh.fromEntity(ent);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAll() {
        var lst = DataConvertUtil.transform(procedureService.getAll(), x -> ChanDoanHinhAnh.fromEntity(x));
        return ResponseEntity.ok(lst);
    }
    
    private DiagnosticReportEntity saveDiagnosticReportEntity(DiagnosticReportEntity ent) {
        ent.active = true;
        ent.resCreated = new Date();
        return diagnosticReportService.save(ent);
    }
    
    private ServiceRequestEntity saveServiceRequestEntity(ServiceRequestEntity ent) {
        ent.active = true;
        ent.resCreated = new Date();
        return serviceRequestService.save(ent);
    }
    
    private ProcedureEntity saveProcedureEntity(ProcedureEntity ent) {
        ent.active = true;
        ent.resCreated = new Date();
        return procedureService.save(ent);
    }
    
    @PostMapping("/create_or_update")
    public ResponseEntity<?> createOrUpdate(@RequestBody ChanDoanHinhAnh dto) {
        try {
            Map<String, BaseResource> entities = ChanDoanHinhAnh.toEntity(dto);
            
            if(entities != null) {
                var procedure = (ProcedureEntity) entities.get("procedure");
                var serviceRequest = (ServiceRequestEntity) entities.get("serviceRequest");
                var diagnosticReport = (DiagnosticReportEntity) entities.get("diagnosticReport");
                                
                diagnosticReport = saveDiagnosticReportEntity(diagnosticReport);
                serviceRequest = saveServiceRequestEntity(serviceRequest);
                procedure = saveProcedureEntity(procedure);
                
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
