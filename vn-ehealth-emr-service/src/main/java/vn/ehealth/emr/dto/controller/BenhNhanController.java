package vn.ehealth.emr.dto.controller;

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

import vn.ehealth.emr.model.dto.BenhNhan;
import vn.ehealth.emr.service.PatientService;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

@RestController
@RequestMapping("/api/benh_nhan")
public class BenhNhanController {
    
    private static Logger logger = LoggerFactory.getLogger(BenhNhanController.class);
            
    @Autowired private PatientService patientService;
        
    @GetMapping("/get_by_id")
    public ResponseEntity<?> getById(@RequestParam String id) {
        var obj = patientService.getById(id);
        var dto = BenhNhan.fromFhir(obj);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAll() {
        var lst = DataConvertUtil.transform(patientService.getAll(), x -> BenhNhan.fromFhir(x));
        return ResponseEntity.ok(lst);
    }
    
    @PostMapping("/create_or_update")
    public ResponseEntity<?> createOrUpdate(@RequestBody BenhNhan dto) {
        try {
            var obj = BenhNhan.toFhir(dto);
            obj = patientService.save(obj);
            dto = BenhNhan.fromFhir(obj);
            var result = Map.of("success", true, "dto", dto);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save entity: ", e);
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = Map.of("success", false, "error", error);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
