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

import vn.ehealth.emr.model.EmrHoiChan;
import vn.ehealth.emr.service.EmrHoiChanService;
import vn.ehealth.emr.service.EmrVaoKhoaService;
import vn.ehealth.emr.utils.EmrUtils;

@RestController
@RequestMapping("/api/hoichan")
public class EmrHoiChanController {
    
    private Logger logger = LoggerFactory.getLogger(EmrHoiChanController.class);
    @Autowired EmrVaoKhoaService emrVaoKhoaService;
    @Autowired EmrHoiChanService emrHoiChanService;
    
    @GetMapping("/get_ds_hoichan")
    public ResponseEntity<?> getDsHoichan(@RequestParam("hsba_id") String id) {
        var result = new ArrayList<EmrHoiChan>();
        var vkList = emrVaoKhoaService.getByEmrHoSoBenhAnId(new ObjectId(id));
        
        for(var vk : vkList) {
            var hoiChanList = emrHoiChanService.getByEmrVaoKhoaId(vk.id);
            hoiChanList.forEach(x -> x.emrVaoKhoa = vk);
            result.addAll(hoiChanList);
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/delete_hoichan")
    public ResponseEntity<?> deleteHoichan(@RequestParam("hoichan_id") String id) {
        try {
            emrHoiChanService.delete(new ObjectId(id));
            var result = Map.of("success" , true);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Error delete hoichan:", e);
            var result = Map.of("success" , false);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/create_or_update_hoichan")
    public ResponseEntity<?> createOrUpdateHoichan(@RequestBody String jsonSt) {
        
        try {
            var mapper = EmrUtils.createObjectMapper();
            var hoichan = mapper.readValue(jsonSt, EmrHoiChan.class);
            hoichan = emrHoiChanService.createOrUpdate(hoichan);
            
            var result = Map.of(
                "success" , true,
                "emrHoiChan", hoichan 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            var result = Map.of(
                "success" , false,
                "errors", List.of(e.getMessage()) 
            );
            logger.error("Error save hoichan:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

}
