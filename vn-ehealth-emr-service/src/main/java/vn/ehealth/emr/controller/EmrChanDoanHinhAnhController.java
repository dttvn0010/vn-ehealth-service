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

import vn.ehealth.emr.model.EmrChanDoanHinhAnh;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.service.EmrChanDoanHinhAnhService;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/cdha")
public class EmrChanDoanHinhAnhController {
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @Autowired private EmrChanDoanHinhAnhService emrChanDoanHinhAnhService;
    @Autowired private EmrHoSoBenhAnService emrHoSoBenhAnService;
    
    @GetMapping("/get_ds_cdha")
    public ResponseEntity<?> getDsChanDoanHinhAnh(@RequestParam("hsba_id") String hsbaId) {
        var cdhaList = emrChanDoanHinhAnhService.getByEmrHoSoBenhAnId(new ObjectId(hsbaId));
        return ResponseEntity.ok(cdhaList);
    }
    
    @GetMapping("/get_ds_cdha_by_bn")  
    public ResponseEntity<?> getDsChanDoanHinhAnhByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
        return ResponseEntity.ok(emrChanDoanHinhAnhService.getByEmrBenhNhanId(new ObjectId(benhNhanId)));
    }
    
    @GetMapping("/delete_cdha")
    public ResponseEntity<?> deleteCdha(@RequestParam("cdha_id") String id) {
        try {
        	var user = UserUtil.getCurrentUser();
            emrChanDoanHinhAnhService.delete(new ObjectId(id), user.get().id);
            var result = mapOf("success" , true);
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @PostMapping("/save_cdha")
    public ResponseEntity<?> saveCdha(@RequestBody String jsonSt) {
        
        try {
        	var user = UserUtil.getCurrentUser();
            var cdha = objectMapper.readValue(jsonSt, EmrChanDoanHinhAnh.class);
            cdha = emrChanDoanHinhAnhService.save(cdha, user.get().id, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "emrChanDoanHinhAnh", cdha 
            );
                    
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }   
    
    private void saveToFhirDb(EmrHoSoBenhAn hsba, List<EmrChanDoanHinhAnh> cdhaList) {
        if(hsba == null) return;
        try {
            var enc = hsba.getEncounterInDB();
            if(enc != null) {
                cdhaList.forEach(x -> EmrDichVuKyThuatHelper.saveDichVuKT(enc, x));
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_cdha")
    public ResponseEntity<?> createOrUpdateCdhaFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = emrHoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var cdhaObjList = (List<Object>) map.get("emrChanDoanHinhAnhs");
            var cdhaList = cdhaObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, EmrChanDoanHinhAnh.class))
                                .collect(Collectors.toList());
            
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            emrChanDoanHinhAnhService.createOrUpdateFromHIS(userId, hsba, cdhaList, jsonSt);
            
            // save to FHIR db
            saveToFhirDb(hsba, cdhaList);
            
            var result = mapOf(
                "success" , true,
                "cdhaList", cdhaList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @GetMapping("/count_cdha_logs")
    public ResponseEntity<?> countLogs(@RequestParam("cdha_id") String id) {        
        return ResponseEntity.ok(emrChanDoanHinhAnhService.countHistory(new ObjectId(id)));
    } 
    
    @GetMapping("/get_cdha_logs")
    public ResponseEntity<?> getLogs(@RequestParam("cdha_id") String id, @RequestParam int start, @RequestParam int count) {        
        return ResponseEntity.ok(emrChanDoanHinhAnhService.getHistory(new ObjectId(id), start, count));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("cdha_id") String id) {
        return ResponseEntity.ok(mapOf("hsGoc", emrChanDoanHinhAnhService.getHsgoc(new ObjectId(id))));
    }
}
