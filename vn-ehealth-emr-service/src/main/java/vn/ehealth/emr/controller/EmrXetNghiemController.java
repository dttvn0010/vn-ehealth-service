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

import vn.ehealth.emr.model.EmrXetNghiem;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.service.EmrXetNghiemService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;

@RestController
@RequestMapping("/api/xetnghiem")
public class EmrXetNghiemController {
    
    private Logger logger = LoggerFactory.getLogger(EmrXetNghiemController.class);
    
    @Autowired private EmrXetNghiemService emrXetNghiemService;
    
    @Autowired private EmrHoSoBenhAnService emrHoSoBenhAnService;

    private JsonParser jsonParser = new JsonParser();
    
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @GetMapping("/get_ds_xetnghiem")
    public ResponseEntity<?> getDsXetNghiem(@RequestParam("hsba_id") String id) {
        var xetnghiemList = emrXetNghiemService.getByEmrHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(xetnghiemList);
    }
  
    @GetMapping("/get_ds_xetnghiem_by_bn")
    public ResponseEntity<?> getDsXetNghiemByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
    	var result = emrXetNghiemService.getByEmrBenhNhanId(new ObjectId(benhNhanId));
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/delete_xetnghiem")
    public ResponseEntity<?> deleteXetnghiem(@RequestParam("xetnghiem_id") String id) {
        try {
        	var user = UserUtil.getCurrentUser();
            emrXetNghiemService.delete(new ObjectId(id), user.get().id);
            var result = Map.of("success" , true);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Error delete xetnghiem:", e);
            var result = Map.of("success" , false);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/save_xetnghiem")
    public ResponseEntity<?> saveXetnghiem(@RequestBody String jsonSt) {
        
        try {
        	var user = UserUtil.getCurrentUser();
            var xetnghiem = objectMapper.readValue(jsonSt, EmrXetNghiem.class);
            xetnghiem = emrXetNghiemService.save(xetnghiem, user.get().id, jsonSt);
            
            var result = Map.of(
                "success" , true,
                "emrXetNghiem", xetnghiem 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            var result = Map.of(
                "success" , false,
                "errors", List.of(e.getMessage()) 
            );
            logger.error("Error save xetnghiem:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/create_or_update_xetnghiem")
    public ResponseEntity<?> createOrUpdateXetnghiemFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = emrHoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var xetnghiemList = EmrUtils.getFieldAsList(map, "emrXetNghiems");
            if(xetnghiemList == null) {
                throw new Exception("emrXetNghiems is null");
            }
            
            for(var xetnghiem: xetnghiemList) {
                var xndvList = EmrUtils.getFieldAsList(xetnghiem, "emrXetNghiemDichVus");
                if(xndvList == null) continue;
                
                for(var xndv : xndvList) {
                    var xnkqList = EmrUtils.getFieldAsList(xndv, "emrXetNghiemKetQuas");
                    if(xnkqList == null) continue;
                    
                    for(var xnkq : xnkqList) {
                        var chisoxn = EmrUtils.getFieldAsObject(xnkq, "emrDmChiSoXetNghiem");
                        if(chisoxn != null) {
                            var extension = Map.of(
                                "donvi", chisoxn.getOrDefault("donvi", ""),
                                "chisobtnam", chisoxn.getOrDefault("chisobtnam", ""),
                                "chisobtnu", chisoxn.getOrDefault("chisobtnu", "")
                            );
                            
                            chisoxn.put("extension", extension);
                        }
                    }
                }
            }
            
            var xetnghiemModelList = xetnghiemList.stream()
                                .map(obj -> objectMapper.convertValue(obj, EmrXetNghiem.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            emrXetNghiemService.createOrUpdateFromHIS(userId, hsba, xetnghiemModelList, jsonSt);
            
            var result = Map.of(
                "success" , true,
                "xetnghiemList", xetnghiemModelList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = Map.of(
                "success" , false,
                "error", error 
            );
            logger.error("Error save xetnghiem from HIS:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/count_xetnghiem_logs")
    public ResponseEntity<?> countLogs(@RequestParam("xetnghiem_id") String id) {        
        return ResponseEntity.ok(emrXetNghiemService.countHistory(new ObjectId(id)));
    } 
    
    @GetMapping("/get_xetnghiem_logs")
    public ResponseEntity<?> getLogs(@RequestParam("xetnghiem_id") String id, @RequestParam int start, @RequestParam int count) {        
        return ResponseEntity.ok(emrXetNghiemService.getHistory(new ObjectId(id), start, count));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("xetnghiem_id") String id) {
        return ResponseEntity.ok(Map.of("hsGoc", emrXetNghiemService.getHsgoc(new ObjectId(id))));
    }
}
