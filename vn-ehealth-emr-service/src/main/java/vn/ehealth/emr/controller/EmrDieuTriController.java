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

import vn.ehealth.emr.model.EmrDieuTri;
import vn.ehealth.emr.service.EmrDieuTriService;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/dieutri")
public class EmrDieuTriController {
    
    @Autowired 
    private EmrDieuTriService emrDieuTriService;
    @Autowired EmrHoSoBenhAnService emrHoSoBenhAnService;
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @GetMapping("/get_ds_dieutri")
    public ResponseEntity<?> getDsDieutri(@RequestParam("hsba_id") String id) {        
        return ResponseEntity.ok(emrDieuTriService.getByEmrHoSoBenhAnId(new ObjectId(id)));
    }
    
    @GetMapping("/get_ds_dieutri_by_bn")
    public ResponseEntity<?> getDsDieutriByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {        
        return ResponseEntity.ok(emrDieuTriService.getByEmrBenhNhanId(new ObjectId(benhNhanId)));
    }
    
    @GetMapping("/delete_dieutri")
    public ResponseEntity<?> deleteDieutri(@RequestParam("dieutri_id") String id) {
        try {
        	var user = UserUtil.getCurrentUser();
            emrDieuTriService.delete(new ObjectId(id), user.get().id);
            var result = mapOf("success" , true);
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @PostMapping("/save_dieutri")
    public ResponseEntity<?> save(@RequestBody String jsonSt) {
        
        try {
            var dieutri = objectMapper.readValue(jsonSt, EmrDieuTri.class);
            var user = UserUtil.getCurrentUser();
            dieutri = emrDieuTriService.save(dieutri, user.get().id, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "emrDieuTri", dieutri 
            );
                    
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_dieu_tri")
    public ResponseEntity<?> createOrUpdateDieuTriFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = emrHoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var dtObjList = (List<Object>) map.get("emrDieuTris");
            var dtList = dtObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, EmrDieuTri.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            emrDieuTriService.createOrUpdateFromHIS(userId, hsba, dtList, dtObjList, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "dtList", dtList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @GetMapping("/count_dieutri_logs")
    public ResponseEntity<?> countLogs(@RequestParam("dieutri_id") String id) {        
        return ResponseEntity.ok(emrDieuTriService.countHistory(new ObjectId(id)));
    } 
    
    @GetMapping("/get_dieutri_logs")
    public ResponseEntity<?> getLogs(@RequestParam("dieutri_id") String id, @RequestParam int start, @RequestParam int count) {        
        return ResponseEntity.ok(emrDieuTriService.getHistory(new ObjectId(id), start, count));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("dieutri_id") String id) {
        return ResponseEntity.ok(mapOf("hsGoc", emrDieuTriService.getHsgoc(new ObjectId(id))));
    }
}
