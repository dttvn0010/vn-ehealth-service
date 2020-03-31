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

import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.model.EmrPhauThuatThuThuat;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.service.EmrPhauThuatThuThuatService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/pttt")
public class EmrPhauThuatThuThuatController {
    
    @Autowired private EmrPhauThuatThuThuatService emrPhauThuatThuThuatService;
    @Autowired private EmrHoSoBenhAnService emrHoSoBenhAnService;
    
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    private JsonParser jsonParser = new JsonParser();    
    
    @GetMapping("/get_ds_pttt")
    public ResponseEntity<?> getDsPhauThuatThuThuat(@RequestParam("hsba_id") String id) {
        var ptttList = emrPhauThuatThuThuatService.getByEmrHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(ptttList);
    }
    
    @GetMapping("/get_ds_pttt_by_bn")
    public ResponseEntity<?> getDsPhauThuatThuThuatByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {  	
    	var result = emrPhauThuatThuThuatService.getByEmrBenhNhanId(new ObjectId(benhNhanId));  
        return ResponseEntity.ok(result);
    }

    @GetMapping("/delete_pttt")
    public ResponseEntity<?> deletePttt(@RequestParam("pttt_id") String id) {
        try {
        	var user = UserUtil.getCurrentUser();
            emrPhauThuatThuThuatService.delete(new ObjectId(id), user.get().id);
            var result = mapOf("success" , true);
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @PostMapping("/save_pttt")
    public ResponseEntity<?> createOrUpdatePttt(@RequestBody String jsonSt) {
        
        try {
        	var user = UserUtil.getCurrentUser();
            var pttt = objectMapper.readValue(jsonSt, EmrPhauThuatThuThuat.class);
            pttt = emrPhauThuatThuThuatService.save(pttt, user.get().id, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "emrPhauThuatThuThuat", pttt 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    private void saveToFhirDb(EmrHoSoBenhAn hsba, List<EmrPhauThuatThuThuat> ptttList) {
        if(hsba == null) return;
        try {
            var enc = hsba.getEncounterInDB();
            if(enc != null) {
                ptttList.forEach(x -> EmrDichVuKyThuatHelper.saveDichVuKT(enc, x));
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_pttt")
    public ResponseEntity<?> createOrUpdatePtttFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = emrHoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var ptttObjList = (List<Object>) map.get("emrPhauThuatThuThuats");
            var ptttList = ptttObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, EmrPhauThuatThuThuat.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            emrPhauThuatThuThuatService.createOrUpdateFromHIS(userId, hsba, ptttList, jsonSt);
            
            // save to FHIR db
            saveToFhirDb(hsba, ptttList);
                        
            var result = mapOf(
                "success" , true,
                "ptttList", ptttList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @GetMapping("/count_pttt_logs")
    public ResponseEntity<?> countLogs(@RequestParam("pttt_id") String id) {        
        return ResponseEntity.ok(emrPhauThuatThuThuatService.countHistory(new ObjectId(id)));
    } 
    
    @GetMapping("/get_pttt_logs")
    public ResponseEntity<?> getLogs(@RequestParam("pttt_id") String id, @RequestParam int start, @RequestParam int count) {        
        return ResponseEntity.ok(emrPhauThuatThuThuatService.getHistory(new ObjectId(id), start, count));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("pttt_id") String id) {
        return ResponseEntity.ok(mapOf("hsGoc", emrPhauThuatThuThuatService.getHsgoc(new ObjectId(id))));
    }
}
