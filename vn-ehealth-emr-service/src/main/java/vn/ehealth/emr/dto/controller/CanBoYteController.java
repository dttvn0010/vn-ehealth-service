package vn.ehealth.emr.dto.controller;

import java.util.Map;
import java.util.Optional;

import org.hl7.fhir.r4.model.IdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.model.dto.CanboYte;
import vn.ehealth.emr.utils.DbUtils;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

@RestController
@RequestMapping("/api/can_bo_y_te")
public class CanBoYteController {

    private static Logger logger = LoggerFactory.getLogger(CanBoYteController.class);
    

    @GetMapping("/get_by_id")
    public ResponseEntity<?> getById(@RequestParam String id) {
        var obj = DbUtils.getPractitionerDao().read(new IdType(id));
        var dto = CanboYte.fromFhir(obj);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAllDto() {
        var lst = DataConvertUtil.transform(DbUtils.getPractitionerDao().getAll(), x -> CanboYte.fromFhir(x));
        return ResponseEntity.ok(lst);
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody CanboYte dto) {
        try {
            var obj = CanboYte.toFhir(dto);
            if(obj.hasId()) {
                obj = DbUtils.getPractitionerDao().update(obj, new IdType(obj.getId()));
            }else {
                obj = DbUtils.getPractitionerDao().create(obj);
            }
            dto = CanboYte.fromFhir(obj);
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
