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

import vn.ehealth.emr.model.dto.KhoaDieuTri;
import vn.ehealth.emr.service.LocationService;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

@RestController
@RequestMapping("/api/khoa_dieu_tri")
public class KhoaDieuTriController {

    private static Logger logger = LoggerFactory.getLogger(KhoaDieuTriController.class);
    
    @Autowired private LocationService locationService;
    
    @GetMapping("/get_by_id")
    public ResponseEntity<?> getById(@RequestParam String id) {
        var ent = locationService.getById(id).get();
        var dto = KhoaDieuTri.fromEntity(ent);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAllDto() {
        var lst = DataConvertUtil.transform(locationService.getAll(), x -> KhoaDieuTri.fromEntity(x));
        return ResponseEntity.ok(lst);
    }
    
    @PostMapping("/create_or_update")
    public ResponseEntity<?> createOrUpdate(@RequestBody KhoaDieuTri dto) {
        try {
            var ent = KhoaDieuTri.toEntity(dto);
            ent.active = true;
            ent.resCreated = new Date();
            ent = locationService.save(ent);
            var result = Map.of("success", true, "entity", ent);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save entity: ", e);
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = Map.of("success", false, "error", error);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
