package vn.ehealth.cdr.controller;

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

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.cdr.model.DonThuoc;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.service.DonThuocService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.EmrUtils;
import vn.ehealth.cdr.validate.JsonParser;
import vn.ehealth.hl7.fhir.medication.dao.impl.MedicationRequestDao;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;


@RestController
@RequestMapping("/api/donthuoc")
public class DonThuocController {
    
    @Autowired private DonThuocService donThuocService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private MedicationRequestDao medicationRequestDao;
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @GetMapping("/get_ds_donthuoc")
    public ResponseEntity<?> getDsDonThuoc(@RequestParam("hsba_id") String id) {
        var donthuocList = donThuocService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(donthuocList);
    }
    
    private void saveToFhirDb(HoSoBenhAn hsba, List<DonThuoc> donThuocList) {
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
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_don_thuoc")
    public ResponseEntity<?> createOrUpdateDonThuocFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = hoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var dtObjList = (List<Object>) map.get("emrDonThuocs");
            var dtList = dtObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, DonThuoc.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            donThuocService.createOrUpdateFromHIS(userId, hsba, dtList, jsonSt);
            
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
}
