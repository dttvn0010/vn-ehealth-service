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

import vn.ehealth.emr.model.EmrGiaiPhauBenh;
import vn.ehealth.emr.service.EmrGiaiPhauBenhService;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;

@RestController
@RequestMapping("/api/gpb")
public class EmrGiaiPhauBenhController {
    
    private Logger logger = LoggerFactory.getLogger(EmrGiaiPhauBenhController.class);
    @Autowired 
    private EmrGiaiPhauBenhService emrGiaiPhauBenhService;
    @Autowired EmrHoSoBenhAnService emrHoSoBenhAnService;
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @GetMapping("/get_ds_gpb")
    public ResponseEntity<?> getDsGiaiPhauBenh(@RequestParam("hsba_id") String id) {
        var gpbList = emrGiaiPhauBenhService.getByEmrHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(gpbList);
    }
    
    @GetMapping("/get_ds_gpb_by_bn")
    public ResponseEntity<?> getDsGiaiPhauBenhByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
        var gpbList = emrGiaiPhauBenhService.getByEmrHoSoBenhAnId(new ObjectId(benhNhanId));
        return ResponseEntity.ok(gpbList);
    }
    
    @GetMapping("/delete_gpb")
    public ResponseEntity<?> deleteGpb(@RequestParam("gpb_id") String id) {
        try {
        	var user = UserUtil.getCurrentUser();
            emrGiaiPhauBenhService.delete(new ObjectId(id), user.get().id);
            var result = Map.of("success" , true);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Error delete gbp:", e);
            var result = Map.of("success" , false);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/save_gpb")
    public ResponseEntity<?> saveGpb(@RequestBody String jsonSt) {
        
        try {
        	var user = UserUtil.getCurrentUser();
            var gbp = objectMapper.readValue(jsonSt, EmrGiaiPhauBenh.class);
            gbp = emrGiaiPhauBenhService.save(gbp, user.get().id, jsonSt);
            
            var result = Map.of(
                "success" , true,
                "emrChanDoanHinhAnh", gbp 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            var result = Map.of(
                "success" , false,
                "errors", List.of(e.getMessage()) 
            );
            logger.error("Error save gbp:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_gpb")
    public ResponseEntity<?> createOrUpdateGpbFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = emrHoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var gpbObjList = (List<Object>) map.get("emrGiaiPhauBenhs");
            var gpbList = gpbObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, EmrGiaiPhauBenh.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            emrGiaiPhauBenhService.createOrUpdateFromHIS(userId, hsba, gpbList, jsonSt);
            
            var result = Map.of(
                "success" , true,
                "gpbList", gpbList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = Map.of(
                "success" , false,
                "error", error 
            );
            logger.error("Error save GiaiPhauBenh from HIS:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/count_gpb_logs")
    public ResponseEntity<?> countLogs(@RequestParam("gpb_id") String id) {        
        return ResponseEntity.ok(emrGiaiPhauBenhService.countHistory(new ObjectId(id)));
    } 
    
    @GetMapping("/get_gpb_logs")
    public ResponseEntity<?> getLogs(@RequestParam("gpb_id") String id, @RequestParam int start, @RequestParam int count) {        
        return ResponseEntity.ok(emrGiaiPhauBenhService.getHistory(new ObjectId(id), start, count));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("gpb_id") String id) {
        return ResponseEntity.ok(Map.of("hsGoc", emrGiaiPhauBenhService.getHsgoc(new ObjectId(id))));
    }
}
