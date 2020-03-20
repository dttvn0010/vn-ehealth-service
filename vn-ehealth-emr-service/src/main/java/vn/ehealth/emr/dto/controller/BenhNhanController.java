package vn.ehealth.emr.dto.controller;

import org.hl7.fhir.r4.model.IdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.model.dto.BenhNhan;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import vn.ehealth.hl7.fhir.patient.dao.impl.PatientDao;

@RestController
@RequestMapping("/api/benh_nhan")
public class BenhNhanController {
    
    private static Logger logger = LoggerFactory.getLogger(BenhNhanController.class);
            
    @Autowired private PatientDao patientDao;
    
    @GetMapping("/get_by_id/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        var obj = patientDao.read(new IdType(id));
        var dto = BenhNhan.fromFhir(obj);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAll() {
        var lst = transform(patientDao.getAll(), x -> BenhNhan.fromFhir(x));
        return ResponseEntity.ok(lst);
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody BenhNhan dto) {
        try {
            var obj = BenhNhan.toFhir(dto);
            if(obj.hasId()) {
                obj = patientDao.update(obj, obj.getIdElement());
            }else {
                obj = patientDao.create(obj);
            }
            dto = BenhNhan.fromFhir(obj);
            var result =  mapOf(entry("success", true), entry("dto", dto));
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save entity: ", e);
            var result = mapOf(entry("success", false), entry("error", e.getMessage()));
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
