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

import vn.ehealth.emr.model.EmrChamSoc;
import vn.ehealth.emr.service.EmrChamSocService;
import vn.ehealth.emr.service.EmrVaoKhoaService;
import vn.ehealth.emr.utils.EmrUtils;

@RestController
@RequestMapping("/api/chamsoc")
public class EmrChamSocController {
    
    private Logger logger = LoggerFactory.getLogger(EmrChamSocController.class);
    @Autowired EmrVaoKhoaService emrVaoKhoaService;
    @Autowired EmrChamSocService emrChamSocService;
    
    @GetMapping("/get_ds_chamsoc")
    public ResponseEntity<?> getDsChamSoc(@RequestParam("hsba_id") String id) {
        var result = new ArrayList<EmrChamSoc>();
        var vkList = emrVaoKhoaService.getByEmrHoSoBenhAnId(new ObjectId(id));
        
        for(var vk : vkList) {
            var chamSocList = emrChamSocService.getByEmrVaoKhoaId(vk.id);
            chamSocList.forEach(x -> x.emrVaoKhoa = vk);
            result.addAll(chamSocList);
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/delete_chamsoc")
    public ResponseEntity<?> deleteChamSoc(@RequestParam("chamsoc_id") String id) {
        try {
            emrChamSocService.delete(new ObjectId(id));
            var result = Map.of("success" , true);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Error delete chamsoc:", e);
            var result = Map.of("success" , false);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/create_or_update_chamsoc")
    public ResponseEntity<?> createOrUpdateChamsoc(@RequestBody String jsonSt) {
        
        try {
            var mapper = EmrUtils.createObjectMapper();                    
            var chamsoc = mapper.readValue(jsonSt, EmrChamSoc.class);
            chamsoc = emrChamSocService.createOrUpdate(chamsoc);
            
            var result = Map.of(
                "success" , true,
                "emrChamSoc", chamsoc 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            var result = Map.of(
                "success" , false,
                "errors", List.of(e.getMessage()) 
            );
            logger.error("Error save chamsoc:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
