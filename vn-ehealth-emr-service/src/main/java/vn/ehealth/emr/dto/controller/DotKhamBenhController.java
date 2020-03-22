package vn.ehealth.emr.dto.controller;

import java.util.Optional;

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

import vn.ehealth.emr.model.dto.DotKhamBenh;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EpisodeOfCareDao;

@RestController
@RequestMapping("/api/dot_kham_benh")
public class DotKhamBenhController {

    private static Logger logger = LoggerFactory.getLogger(BenhNhanController.class);
    
    @Autowired private EpisodeOfCareDao episodeOfCareDao;
        
    @GetMapping("/get_by_id")
    public ResponseEntity<?> getById(@RequestParam String id) {
        var obj = episodeOfCareDao.read(new IdType(id));
        var dto = DotKhamBenh.fromFhir(obj);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAll() {
        var lst = transform(episodeOfCareDao.getAll(), x -> DotKhamBenh.fromFhir(x));
        return ResponseEntity.ok(lst);
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody DotKhamBenh dto) {
        try {
            var obj = DotKhamBenh.toFhir(dto);            
            
            if(obj.hasId()) {
                obj = episodeOfCareDao.update(obj, obj.getIdElement());
            }else {
                obj = episodeOfCareDao.create(obj);
            }
            dto = DotKhamBenh.fromFhir(obj);
            var result = mapOf(entry("success", true), entry("dto", dto));
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save entity: ", e);
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = mapOf(entry("success", false), entry("error", error));
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
