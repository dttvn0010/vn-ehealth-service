package vn.ehealth.emr.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
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

import vn.ehealth.emr.model.EmrDieuTri;
import vn.ehealth.emr.service.EmrDieuTriService;
import vn.ehealth.emr.service.EmrVaoKhoaService;
import vn.ehealth.emr.utils.EmrUtils;

@RestController
@RequestMapping("/api/dieutri")
public class EmrDieuTriController {
    
    private Logger logger = LoggerFactory.getLogger(EmrDieuTriController.class);
            
    @Autowired EmrVaoKhoaService emrVaoKhoaService;
    @Autowired EmrDieuTriService emrDieuTriService;
    
    @GetMapping("/get_ds_dieutri")
    public ResponseEntity<?> getDsDieutri(@RequestParam("hsba_id") String id) {
        var result = new ArrayList<EmrDieuTri>();
        var vkList = emrVaoKhoaService.getByEmrHoSoBenhAnId(new ObjectId(id));
        
        for(var vk : vkList) {
            var dieuTriList = emrDieuTriService.getByEmrVaoKhoaId(vk.id);
            dieuTriList.forEach(x -> x.emrVaoKhoa = vk);
            result.addAll(dieuTriList);
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/delete_dieutri")
    public ResponseEntity<?> deleteDieutri(@RequestParam("dieutri_id") String id) {
        try {
            emrDieuTriService.delete(new ObjectId(id));
            var result = Map.of("success" , true);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Error delete dieutri:", e);
            var result = Map.of("success" , false);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/create_or_update_dieutri")
    public ResponseEntity<?> createOrUpdateDieutri(@RequestBody String jsonSt) {
        
        try {
            var mapper = EmrUtils.createObjectMapper();            
            var dieutri = mapper.readValue(jsonSt, EmrDieuTri.class);
            dieutri = emrDieuTriService.createOrUpdate(dieutri);
            
            var result = Map.of(
                "success" , true,
                "emrDieuTri", dieutri 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            var result = Map.of(
                "success" , false,
                "errors", List.of(e.getMessage()) 
            );
            logger.error("Error save dieutri:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
