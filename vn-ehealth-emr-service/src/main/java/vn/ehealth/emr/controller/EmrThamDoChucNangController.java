package vn.ehealth.emr.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.emr.model.EmrThamDoChucNang;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.service.EmrThamDoChucNangService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/tdcn")
public class EmrThamDoChucNangController {
    
    @Autowired 
    private EmrThamDoChucNangService emrThamDoChucNangService;
    @Autowired EmrHoSoBenhAnService emrHoSoBenhAnService;
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @GetMapping("/get_ds_tdcn")
    public ResponseEntity<?> getDsThamDoChucNang(@RequestParam("hsba_id") String id) {
        var tdcnList = emrThamDoChucNangService.getByEmrHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(tdcnList);
    }
    
    @GetMapping("/delete_tdcn")
    public ResponseEntity<?> deleteTdcn(@RequestParam("tdcn_id") String id) {
        try {
        	var user = UserUtil.getCurrentUser();
            emrThamDoChucNangService.delete(new ObjectId(id), user.get().id);
            var result = mapOf("success" , true);
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @PostMapping("/save_tdcn")
    public ResponseEntity<?> saveTdcn(@RequestBody String jsonSt) {
        
        try {
        	var user = UserUtil.getCurrentUser();
            var tdcn = objectMapper.readValue(jsonSt, EmrThamDoChucNang.class);
            tdcn = emrThamDoChucNangService.save(tdcn, user.get().id, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "emrChanDoanHinhAnh", tdcn 
            );
                    
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_tdcn")
    public ResponseEntity<?> createOrUpdateTdcnFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = emrHoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var tdcnObjList = (List<Object>) map.get("emrThamDoChucNangs");
            var tdcnList = tdcnObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, EmrThamDoChucNang.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            emrThamDoChucNangService.createOrUpdateFromHIS(userId, hsba, tdcnList, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "tdcnList", tdcnList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @GetMapping("/count_tdcn_logs")
    public ResponseEntity<?> countLogs(@RequestParam("tdcn_id") String id) {        
        return ResponseEntity.ok(emrThamDoChucNangService.countHistory(new ObjectId(id)));
    } 
    
    @GetMapping("/get_tdcn_logs")
    public ResponseEntity<?> getLogs(@RequestParam("tdcn_id") String id, @RequestParam int start, @RequestParam int count) {        
        return ResponseEntity.ok(emrThamDoChucNangService.getHistory(new ObjectId(id), start, count));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("tdcn_id") String id) {
        return ResponseEntity.ok(mapOf("hsGoc", emrThamDoChucNangService.getHsgoc(new ObjectId(id))));
    }
}
