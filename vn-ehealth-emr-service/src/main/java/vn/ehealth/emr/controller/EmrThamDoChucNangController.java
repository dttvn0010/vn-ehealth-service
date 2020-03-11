package vn.ehealth.emr.controller;

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

import vn.ehealth.emr.model.EmrThamDoChucNang;
import vn.ehealth.emr.service.EmrThamDoChucNangService;
import vn.ehealth.emr.utils.EmrUtils;

@RestController
@RequestMapping("/api/tdcn")
public class EmrThamDoChucNangController {
    
    private Logger logger = LoggerFactory.getLogger(EmrThamDoChucNangController.class);
    @Autowired EmrThamDoChucNangService emrThamDoChucNangService;
    
    @GetMapping("/get_ds_tdcn")
    public ResponseEntity<?> getDsThamDoChucNang(@RequestParam("hsba_id") String id) {
        var tdcnList = emrThamDoChucNangService.getByEmrHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(tdcnList);
    }
    
    @GetMapping("/delete_tdcn")
    public ResponseEntity<?> deleteTdcn(@RequestParam("tdcn_id") String id) {
        try {
            emrThamDoChucNangService.delete(new ObjectId(id));
            var result = Map.of("success" , true);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Error delete tdcn:", e);
            var result = Map.of("success" , false);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/create_or_update_tdcn")
    public ResponseEntity<?> createOrUpdateTdcn(@RequestBody String jsonSt) {
        
        try {
            var mapper = EmrUtils.createObjectMapper();
            var tdcn = mapper.readValue(jsonSt, EmrThamDoChucNang.class);
            tdcn = emrThamDoChucNangService.createOrUpdate(tdcn);
            
            var result = Map.of(
                "success" , true,
                "emrChanDoanHinhAnh", tdcn 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            var result = Map.of(
                "success" , false,
                "errors", List.of(e.getMessage()) 
            );
            logger.error("Error save tdcn:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
