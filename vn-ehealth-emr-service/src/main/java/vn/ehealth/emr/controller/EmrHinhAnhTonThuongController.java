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

import vn.ehealth.emr.model.EmrHinhAnhTonThuong;
import vn.ehealth.emr.service.EmrHinhAnhTonThuongService;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;

@RestController
@RequestMapping("/api/hatt")
public class EmrHinhAnhTonThuongController {
    
    private Logger logger = LoggerFactory.getLogger(EmrHinhAnhTonThuongController.class);
    
    @Autowired 
    private EmrHinhAnhTonThuongService emrHinhAnhTonThuongService;
    @Autowired EmrHoSoBenhAnService emrHoSoBenhAnService;
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();

    @GetMapping("/get_hatt")
    public ResponseEntity<?> getHatt(@RequestParam("hatt_id") String id) {
        var hatt = emrHinhAnhTonThuongService.getById(new ObjectId(id));
        return ResponseEntity.of(hatt);
    }
    
    @GetMapping("/get_ds_hatt")
    public ResponseEntity<?> getDsHatt(@RequestParam("hsba_id") String hsbaId) {
        var hattList = emrHinhAnhTonThuongService.getByEmrHoSoBenhAnId(new ObjectId(hsbaId));
        return ResponseEntity.ok(hattList);
    }
    
    @GetMapping("/get_ds_hatt_by_bn")
    public ResponseEntity<?> getDsHattByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
        var hattList = emrHinhAnhTonThuongService.getByEmrBenhNhanId(new ObjectId(benhNhanId));
        return ResponseEntity.ok(hattList);
    }
    
    @GetMapping("/delete_hatt")
    public ResponseEntity<?> deleteHatt(@RequestParam("hatt_id") String id) {
        try {
        	var user = UserUtil.getCurrentUser();
            emrHinhAnhTonThuongService.delete(new ObjectId(id), user.get().id);
            var result = Map.of("success" , true);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Error delete hatt:", e);
            var result = Map.of("success" , false);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/save_hatt")
    public ResponseEntity<?> saveHatt(@RequestBody String jsonSt) {
        
        try {
        	var user = UserUtil.getCurrentUser();
            var hatt = objectMapper.readValue(jsonSt, EmrHinhAnhTonThuong.class);
            hatt = emrHinhAnhTonThuongService.save(hatt, user.get().id, jsonSt);
            
            var result = Map.of(
                "success" , true,
                "emrHinhAnhTonThuong", hatt 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            var result = Map.of(
                "success" , false,
                "errors", List.of(e.getMessage()) 
            );
            logger.error("Error save hatt:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_hatt")
    public ResponseEntity<?> createOrUpdateChamSocFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = emrHoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var hattObjList = (List<Object>) map.get("emrHinhAnhTonThuongs");
            var hattList = hattObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, EmrHinhAnhTonThuong.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            emrHinhAnhTonThuongService.createOrUpdateFromHIS(userId, hsba, hattList, jsonSt);
            
            var result = Map.of(
                "success" , true,
                "hattList", hattList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = Map.of(
                "success" , false,
                "error", error 
            );
            logger.error("Error save hinhanhtonthuong from HIS:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/count_hatt_logs")
    public ResponseEntity<?> countLogs(@RequestParam("hatt_id") String id) {        
        return ResponseEntity.ok(emrHinhAnhTonThuongService.countHistory(new ObjectId(id)));
    } 
    
    @GetMapping("/get_hatt_logs")
    public ResponseEntity<?> getLogs(@RequestParam("hatt_id") String id, @RequestParam int start, @RequestParam int count) {        
        return ResponseEntity.ok(emrHinhAnhTonThuongService.getHistory(new ObjectId(id), start, count));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("hatt_id") String id) {
        return ResponseEntity.ok(Map.of("hsGoc", emrHinhAnhTonThuongService.getHsgoc(new ObjectId(id))));
    }
}
