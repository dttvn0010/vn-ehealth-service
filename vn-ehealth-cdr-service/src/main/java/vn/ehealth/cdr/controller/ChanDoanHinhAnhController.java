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
import vn.ehealth.cdr.model.ChanDoanHinhAnh;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.service.ChanDoanHinhAnhService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.EmrUtils;
import vn.ehealth.cdr.validate.JsonParser;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/cdha")
public class ChanDoanHinhAnhController {
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @Autowired private ChanDoanHinhAnhService chanDoanHinhAnhService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    @GetMapping("/get_ds_cdha")
    public ResponseEntity<?> getDsChanDoanHinhAnh(@RequestParam("hsba_id") String hsbaId) {
        var cdhaList = chanDoanHinhAnhService.getByHoSoBenhAnId(new ObjectId(hsbaId));
        return ResponseEntity.ok(cdhaList);
    }
    
    private void saveToFhirDb(HoSoBenhAn hsba, List<ChanDoanHinhAnh> cdhaList) {
        if(hsba == null) return;
        try {
            var enc = hsba.getEncounterInDB();
            if(enc != null) {
                cdhaList.forEach(x -> DichVuKyThuatHelper.saveDichVuKT(enc, x));
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
            var hsba = hoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var cdhaObjList = (List<Object>) map.get("emrChanDoanHinhAnhs");
            var cdhaList = cdhaObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, ChanDoanHinhAnh.class))
                                .collect(Collectors.toList());
            
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            chanDoanHinhAnhService.createOrUpdateFromHIS(userId, hsba, cdhaList, jsonSt);
            
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
}
