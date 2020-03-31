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

import vn.ehealth.emr.model.EmrHoiChan;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.service.EmrHoiChanService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/hoichan")
public class EmrHoiChanController {
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
            var result = mapOf("success" , true);
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @PostMapping("/save_hoichan")
    public ResponseEntity<?> saveHoichan(@RequestBody String jsonSt) {
        
        try {
        	var user = UserUtil.getCurrentUser();
            var hoichan = objectMapper.readValue(jsonSt, EmrHoiChan.class);
            hoichan = emrHoiChanService.save(hoichan, user.get().id, jsonSt);
            var result = mapOf(
                    "success" , true,
                    "emrHoiChan", hoichan 
                );
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
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
            
            var result = mapOf(
                "success" , true,
                "hcList", hcList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
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
        return ResponseEntity.ok(mapOf("hsGoc", emrHoiChanService.getHsgoc(new ObjectId(id))));
    }

}
