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
import vn.ehealth.cdr.model.GiaiPhauBenh;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.service.GiaiPhauBenhService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.EmrUtils;
import vn.ehealth.cdr.validate.JsonParser;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/gpb")
public class GiaiPhauBenhController {
    
    @Autowired private GiaiPhauBenhService giaiPhauBenhService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @GetMapping("/get_ds_gpb")
    public ResponseEntity<?> getDsGiaiPhauBenh(@RequestParam("hsba_id") String id) {
        var gpbList = giaiPhauBenhService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(gpbList);
    }
    
    @GetMapping("/get_ds_gpb_by_bn")
    public ResponseEntity<?> getDsGiaiPhauBenhByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
        var gpbList = giaiPhauBenhService.getByHoSoBenhAnId(new ObjectId(benhNhanId));
        return ResponseEntity.ok(gpbList);
    }
    
    private void saveToFhirDb(HoSoBenhAn hsba, List<GiaiPhauBenh> gpbList) {
        if(hsba == null) return;
        try {
            var enc = hsba.getEncounterInDB();
            if(enc != null) {
                gpbList.forEach(x -> DichVuKyThuatHelper.saveDichVuKT(enc, x));
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
            var hsba = hoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var gpbObjList = (List<Object>) map.get("emrGiaiPhauBenhs");
            var gpbList = gpbObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, GiaiPhauBenh.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            giaiPhauBenhService.createOrUpdateFromHIS(userId, hsba, gpbList, jsonSt);
            
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
}
