package vn.ehealth.emr.dto.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
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
import vn.ehealth.hl7.fhir.clinical.dao.impl.ProcedureDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ServiceRequestDao;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.DiagnosticReportDao;

@RestController
@RequestMapping("/api/chan_doan_hinh_anh")
public class ChanDoanHinhAnhController {

    private static Logger logger = LoggerFactory.getLogger(ChanDoanHinhAnhController.class);
    
    @Autowired private ProcedureDao procedureDao;
    @Autowired private DiagnosticReportDao  diagnosticReportDao;
    @Autowired private ServiceRequestDao serviceRequestDao;
        
    @GetMapping("/get_by_id")
    public ResponseEntity<?> getById(@RequestParam String id) {
        var obj = procedureDao.read(new IdType(id));
        var dto = ChanDoanHinhAnh.fromFhir(obj);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAll() {
        var lst = DataConvertUtil.transform(procedureDao.getAll(), x -> ChanDoanHinhAnh.fromFhir(x));
        return ResponseEntity.ok(lst);
    }
    
    private DiagnosticReport saveDiagnosticReport(DiagnosticReport obj) {
        if(obj.hasId()) {
            return diagnosticReportDao.update(obj, new IdType(obj.getId()));
        }else {
            return diagnosticReportDao.create(obj);
        }        
    }
    
    private ServiceRequest saveServiceRequest(ServiceRequest obj) {
        if(obj.hasId()) {
            return serviceRequestDao.update(obj, new IdType(obj.getId()));
        }else {
            return serviceRequestDao.create(obj);
        }
    }
    
    private Procedure saveProcedure(Procedure obj) {
        if(obj.hasId()) {
            return procedureDao.update(obj, new IdType(obj.getId()));
        }else {
            return procedureDao.create(obj);
        }
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> createOrUpdate(@RequestBody ChanDoanHinhAnh dto) {
        try {
            Map<String, Resource> entities = ChanDoanHinhAnh.toFhir(dto);
            
            if(entities != null) {
                var procedure = (Procedure) entities.get("procedure");
                var serviceRequest = (ServiceRequest) entities.get("serviceRequest");
                var diagnosticReport = (DiagnosticReport) entities.get("diagnosticReport");
                                
                diagnosticReport = saveDiagnosticReport(diagnosticReport);
                if(diagnosticReport != null) {
                    procedure.setReport(List.of(new Reference(diagnosticReport.getId())));
                }
                
                serviceRequest = saveServiceRequest(serviceRequest);
                if(serviceRequest != null) {
                    procedure.setBasedOn(List.of(new Reference(serviceRequest.getId())));
                }
                
                procedure = saveProcedure(procedure);
                
                var result = Map.of("success", true);
                
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
