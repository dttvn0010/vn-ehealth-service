package vn.ehealth.emr.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.emr.model.EmrHoiChan;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.service.EmrHoiChanService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;

@RestController
@RequestMapping("/api/hoichan")
public class EmrHoiChanController {
    
    private Logger logger = LoggerFactory.getLogger(EmrHoiChanController.class);
    
    @Autowired 
    private EmrHoiChanService emrHoiChanService;
    @Autowired EmrHoSoBenhAnService emrHoSoBenhAnService;
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @GetMapping("/get_ds_hoichan")
    public ResponseEntity<?> getDsHoichan(@RequestParam("hsba_id") String id) {
        var hoichanList = emrHoiChanService.getByEmrHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(hoichanList);
    }
    
    @GetMapping("/get_ds_hoichan_by_bn")
    public ResponseEntity<?> getDsHoichanByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
        var hoichanList = emrHoiChanService.getByEmrBenhNhanId(new ObjectId(benhNhanId));
        return ResponseEntity.ok(hoichanList);
    }
    
    @GetMapping("/delete_hoichan")
    public ResponseEntity<?> deleteHoichan(@RequestParam("hoichan_id") String id) {
        try {
        	var user = UserUtil.getCurrentUser();
            emrHoiChanService.delete(new ObjectId(id), user.get().id);
            var result = Map.of("success" , true);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Error delete hoichan:", e);
            var result = Map.of("success" , false);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/save_hoichan")
    public ResponseEntity<?> saveHoichan(@RequestBody String jsonSt) {
        
        try {
        	var user = UserUtil.getCurrentUser();
            var hoichan = objectMapper.readValue(jsonSt, EmrHoiChan.class);
            hoichan = emrHoiChanService.save(hoichan, user.get().id, jsonSt);
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

    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_hoi_chan")
    public ResponseEntity<?> createOrUpdateHoiChanFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = emrHoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var hcObjList = (List<Object>) map.get("emrHoiChans");
            var hcList = hcObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, EmrHoiChan.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            emrHoiChanService.createOrUpdateFromHIS(userId, hsba, hcList, hcObjList, jsonSt);
            
            var result = Map.of(
                "success" , true,
                "hcList", hcList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = Map.of(
                "success" , false,
                "error", error 
            );
            logger.error("Error save hoichan from HIS:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/count_hoichan_logs")
    public ResponseEntity<?> countLogs(@RequestParam("hoichan_id") String id) {        
        return ResponseEntity.ok(emrHoiChanService.countHistory(new ObjectId(id)));
    } 
    
    @GetMapping("/get_hoichan_logs")
    public ResponseEntity<?> getLogs(@RequestParam("hoichan_id") String id, @RequestParam int start, @RequestParam int count) {        
        return ResponseEntity.ok(emrHoiChanService.getHistory(new ObjectId(id), start, count));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("hoichan_id") String id) {
        return ResponseEntity.ok(Map.of("hsGoc", emrHoiChanService.getHsgoc(new ObjectId(id))));
    }

}
