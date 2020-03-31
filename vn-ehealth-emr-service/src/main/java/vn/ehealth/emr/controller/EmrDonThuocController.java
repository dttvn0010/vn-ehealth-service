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

import vn.ehealth.emr.model.EmrDonThuoc;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.service.EmrDonThuocService;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;
import vn.ehealth.hl7.fhir.medication.dao.impl.MedicationRequestDao;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;


@RestController
@RequestMapping("/api/donthuoc")
public class EmrDonThuocController {
    
    @Autowired private EmrDonThuocService emrDonThuocService;
    @Autowired private EmrHoSoBenhAnService emrHoSoBenhAnService;
    @Autowired private MedicationRequestDao medicationRequestDao;
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @GetMapping("/get_ds_donthuoc")
    public ResponseEntity<?> getDsDonThuoc(@RequestParam("hsba_id") String id) {
        var donthuocList = emrDonThuocService.getByEmrHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(donthuocList);
    }
    
    @GetMapping("/get_ds_donthuoc_by_bn")
    public ResponseEntity<?> getDsDonThuocByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
        var donthuocList = emrDonThuocService.getByEmrBenhNhanId(new ObjectId(benhNhanId));
        return ResponseEntity.ok(donthuocList);
    }
    
    @GetMapping("/delete_donthuoc")
    public ResponseEntity<?> deleteDonthuoc(@RequestParam("donthuoc_id") String id) {
        try {
        	var user = UserUtil.getCurrentUser();
            emrDonThuocService.delete(new ObjectId(id), user.get().id);
            var result = mapOf("success" , true);
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    private void saveToFhirDb(EmrHoSoBenhAn hsba, List<EmrDonThuoc> donThuocList) {
        if(hsba == null) return;
        
        try {
            var enc = hsba.getEncounterInDB();
            if(enc == null) return;
            
            for(var donthuoc : donThuocList) {
                if(donthuoc.emrDonThuocChiTiets == null) continue;                
                
                for(var dtct : donthuoc.emrDonThuocChiTiets) {
                    dtct.bacsikedon = donthuoc.bacsikedon;
                    dtct.ngaykedon = donthuoc.ngaykedon;
                    dtct.sodon = donthuoc.sodon;
                    var medReq = dtct.toFHir(enc);
                    medicationRequestDao.create(medReq);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @PostMapping("/save_donthuoc")
    public ResponseEntity<?> saveDonthuoc(@RequestBody String jsonSt) {
        
        try {
        	var user = UserUtil.getCurrentUser();
            var donthuoc = objectMapper.readValue(jsonSt, EmrDonThuoc.class);
            donthuoc = emrDonThuocService.save(donthuoc, user.get().id, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "emrDonThuoc", donthuoc 
            );
                    
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_don_thuoc")
    public ResponseEntity<?> createOrUpdateDonThuocFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = emrHoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var dtObjList = (List<Object>) map.get("emrDonThuocs");
            var dtList = dtObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, EmrDonThuoc.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            emrDonThuocService.createOrUpdateFromHIS(userId, hsba, dtList, jsonSt);
            
            saveToFhirDb(hsba, dtList);
            
            var result = mapOf(
                "success" , true,
                "dtList", dtList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @GetMapping("/count_donthuoc_logs")
    public ResponseEntity<?> countLogs(@RequestParam("donthuoc_id") String id) {        
        return ResponseEntity.ok(emrDonThuocService.countHistory(new ObjectId(id)));
    } 
    
    @GetMapping("/get_donthuoc_logs")
    public ResponseEntity<?> getLogs(@RequestParam("donthuoc_id") String id, @RequestParam int start, @RequestParam int count) {        
        return ResponseEntity.ok(emrDonThuocService.getHistory(new ObjectId(id), start, count));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("donthuoc_id") String id) {
        return ResponseEntity.ok(mapOf("hsGoc", emrDonThuocService.getHsgoc(new ObjectId(id))));
    }

}
