package vn.ehealth.emr.dto.controller;

import org.hl7.fhir.r4.model.IdType;
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

import vn.ehealth.emr.model.dto.CanboYte;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import vn.ehealth.hl7.fhir.provider.dao.impl.PractitionerDao;

@RestController
@RequestMapping("/api/can_bo_y_te")
public class CanBoYteController {

    @Autowired private PractitionerDao practitionerDao;
    private static Logger logger = LoggerFactory.getLogger(CanBoYteController.class);
    

    @GetMapping("/get_by_id")
    public ResponseEntity<?> getById(@RequestParam String id) {
        var obj = practitionerDao.read(new IdType(id));
        var dto = CanboYte.fromFhir(obj);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAllDto() {
        var lst = transform(practitionerDao.getAll(), x -> CanboYte.fromFhir(x));
        return ResponseEntity.ok(lst);
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody CanboYte dto) {
        try {
            var obj = CanboYte.toFhir(dto);
            if(obj.hasId()) {
                obj = practitionerDao.update(obj, obj.getIdElement());
            }else {
                obj = practitionerDao.create(obj);
            }
            dto = CanboYte.fromFhir(obj);
            var result = mapOf(entry("success", true), entry("dto", dto));
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save entity: ", e);
            var result = mapOf(entry("success", false), entry("error", e.getMessage()));
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
