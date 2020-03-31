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

import vn.ehealth.emr.model.EmrGiaiPhauBenh;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.service.EmrGiaiPhauBenhService;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/gpb")
public class EmrGiaiPhauBenhController {
    
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
            var result = mapOf("success" , true);
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @PostMapping("/save_gpb")
    public ResponseEntity<?> saveGpb(@RequestBody String jsonSt) {
        
        try {
        	var user = UserUtil.getCurrentUser();
            var gbp = objectMapper.readValue(jsonSt, EmrGiaiPhauBenh.class);
            gbp = emrGiaiPhauBenhService.save(gbp, user.get().id, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "emrChanDoanHinhAnh", gbp 
            );
                    
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    private void saveToFhirDb(EmrHoSoBenhAn hsba, List<EmrGiaiPhauBenh> gpbList) {
        if(hsba == null) return;
        try {
            var enc = hsba.getEncounterInDB();
            if(enc != null) {
                gpbList.forEach(x -> EmrDichVuKyThuatHelper.saveDichVuKT(enc, x));
            }
        }catch(Exception e) {
            e.printStackTrace();
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
            
            // Save to Fhir
            saveToFhirDb(hsba, gpbList);
            
            var result = mapOf(
                "success" , true,
                "gpbList", gpbList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
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
        return ResponseEntity.ok(mapOf("hsGoc", emrGiaiPhauBenhService.getHsgoc(new ObjectId(id))));
    }
}
