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

import vn.ehealth.emr.model.EmrChamSoc;
import vn.ehealth.emr.service.EmrChamSocService;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;

@RestController
@RequestMapping("/api/chamsoc")
public class EmrChamSocController {
    
	private JsonParser jsonParser = new JsonParser();
    
	private Logger logger = LoggerFactory.getLogger(EmrChamSocController.class);
    
    @Autowired 
    private EmrChamSocService emrChamSocService;
    @Autowired 
    private EmrHoSoBenhAnService emrHoSoBenhAnService;
    
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @GetMapping("/get_ds_chamsoc")
    public ResponseEntity<?> getDsChamSoc(@RequestParam("hsba_id") String id) {
    
        return ResponseEntity.ok(emrChamSocService.getByEmrHoSoBenhAnId(new ObjectId(id)));
    }
    
    @GetMapping("/get_ds_chamsoc_by_bn")
    public ResponseEntity<?> getDsChamSocByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
    
        return ResponseEntity.ok(emrChamSocService.getByEmrBenhNhanId(new ObjectId(benhNhanId)));
    }
    
    @GetMapping("/delete_chamsoc")
    public ResponseEntity<?> deleteChamSoc(@RequestParam("chamsoc_id") String id) {
        try {
        	var user = UserUtil.getCurrentUser();
            emrChamSocService.delete(new ObjectId(id), user.get().id);
            var result = Map.of("success" , true);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Error delete chamsoc:", e);
            var result = Map.of("success" , false);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/save_chamsoc")
    public ResponseEntity<?> save(@RequestBody String jsonSt) {
        
        try {
        	var user = UserUtil.getCurrentUser();
            var chamsoc = objectMapper.readValue(jsonSt, EmrChamSoc.class);
            chamsoc = emrChamSocService.save(chamsoc, user.get().id, jsonSt);
            
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
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_cham_soc")
    public ResponseEntity<?> createOrUpdateChamSocFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = emrHoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow(); 
            var csObjList = (List<Object>) map.get("emrChamSocs");
            var csList = csObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, EmrChamSoc.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            emrChamSocService.createOrUpdateFromHIS(userId, hsba, csList, csObjList, jsonSt);
            
            var result = Map.of(
                "success" , true,
                "csList", csList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = Map.of(
                "success" , false,
                "error", error 
            );
            logger.error("Error save chamsoc from HIS:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/count_chamsoc_logs")
    public ResponseEntity<?> countLogs(@RequestParam("chamsoc_id") String id) {        
        return ResponseEntity.ok(emrChamSocService.countHistory(new ObjectId(id)));
    } 
    
    @GetMapping("/get_chamsoc_logs")
    public ResponseEntity<?> getLogs(@RequestParam("chamsoc_id") String id, @RequestParam int start, @RequestParam int count) {        
        return ResponseEntity.ok(emrChamSocService.getHistory(new ObjectId(id), start, count));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("chamsoc_id") String id) {
        return ResponseEntity.ok(Map.of("hsGoc", emrChamSocService.getHsgoc(new ObjectId(id))));
    }
}
