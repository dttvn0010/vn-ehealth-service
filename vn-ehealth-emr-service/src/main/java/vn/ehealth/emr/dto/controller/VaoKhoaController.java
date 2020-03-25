package vn.ehealth.emr.dto.controller;

import java.util.HashMap;
import java.util.Optional;

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

import vn.ehealth.emr.model.dto.VaoKhoa;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;


@RestController
@RequestMapping("/api/vao_khoa")
public class VaoKhoaController {

private static Logger logger = LoggerFactory.getLogger(BenhNhanController.class);
    
    @Autowired private EncounterDao encounterDao;
        
    @GetMapping("/get_by_id/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        var obj = encounterDao.read(new IdType(id));
        var dto = VaoKhoa.fromFhir(obj);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAll() {
        var lst = encounterDao.search(new HashMap<>());
        var result = transform(lst, x -> VaoKhoa.fromFhir(x));
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody VaoKhoa dto) {
        try {
            var obj = VaoKhoa.toFhir(dto);            
            
            if(obj.hasId()) {
                obj = encounterDao.update(obj, obj.getIdElement());
            }else {
                obj = encounterDao.create(obj);
            }
            dto = VaoKhoa.fromFhir(obj);
            var result = mapOf("success", true, "dto", dto);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save entity: ", e);
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = mapOf("success", false, "error", error);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
