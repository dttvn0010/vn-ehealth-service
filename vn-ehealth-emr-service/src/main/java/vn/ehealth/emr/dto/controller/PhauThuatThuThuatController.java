package vn.ehealth.emr.dto.controller;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.entry;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.listOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createReference;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
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

import vn.ehealth.emr.model.dto.PhauThuatThuThuat;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.LoaiDichVuKT;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ProcedureDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.DiagnosticReportDao;

@RestController
@RequestMapping("/api/phau_thuat_thu_thuat")
public class PhauThuatThuThuatController {

    private static Logger logger = LoggerFactory.getLogger(PhauThuatThuThuatController.class);
    
    @Autowired private ProcedureDao procedureDao;
    @Autowired private DiagnosticReportDao diagnosticReportDao;
        
    @GetMapping("/get_by_id")
    public ResponseEntity<?> getById(@RequestParam String id) {
        var obj = diagnosticReportDao.read(new IdType(id));
        var dto = PhauThuatThuThuat.fromFhir(obj);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAll() {
        var params = mapOf(
                    entry("category.coding.code", (Object) LoaiDichVuKT.PHAU_THUAT_THU_THUAT),
                    entry("category.coding.system", CodeSystemValue.DICH_VU_KY_THUAT)
                );
        
        var lst = diagnosticReportDao.find(params);
        var result = transform(lst, x -> PhauThuatThuThuat.fromFhir(x));
        return ResponseEntity.ok(result);
    }
    
    private DiagnosticReport saveDiagnosticReport(DiagnosticReport obj) {
        if(obj.hasId()) {
            return diagnosticReportDao.update(obj, obj.getIdElement());
        }else {
            return diagnosticReportDao.create(obj);
        }        
    }
    
    private Procedure saveProcedure(Procedure obj) {
        if(obj.hasId()) {
            return procedureDao.update(obj, obj.getIdElement());
        }else {
            return procedureDao.create(obj);
        }
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody PhauThuatThuThuat dto) {
        try {
            var entities = PhauThuatThuThuat.toFhir(dto);
            
            if(entities != null) {
                var procedure = (Procedure) entities.get("procedure");
                var diagnosticReport = (DiagnosticReport) entities.get("diagnosticReport");
                                
                diagnosticReport = saveDiagnosticReport(diagnosticReport);
                if(diagnosticReport != null) {
                    procedure.setReport(listOf(createReference(diagnosticReport)));
                }
                
                procedure = saveProcedure(procedure);
                
                dto = PhauThuatThuThuat.fromFhir(diagnosticReport);
                
                var result = mapOf(entry("success", true), entry("phauThuatThuThuat", dto));
                
                return ResponseEntity.ok(result);
            }
            var result = mapOf(entry("success", false), entry("error", "Invalid data, null Entity"));
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            logger.error("Can not save entity: ", e);
            var result = mapOf(entry("success", false), entry("error", e.getMessage()));
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
