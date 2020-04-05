package vn.ehealth.cdr.controller;

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

import vn.ehealth.cdr.model.HoiChan;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.HoiChanService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

@RestController
@RequestMapping("/api/hoichan")
public class HoiChanController {
    @Autowired 
    private HoiChanService hoiChanService;
    @Autowired HoSoBenhAnService hoSoBenhAnService;
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @GetMapping("/get_ds_hoichan")
    public ResponseEntity<?> getDsHoichan(@RequestParam("hsba_id") String id) {
        var hoichanList = hoiChanService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(hoichanList);
    }
    
    @PostMapping("/create_or_update_hoi_chan")
    public ResponseEntity<?> createOrUpdateHoiChanFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElse(null);
            
            if(hsba == null) {
                throw new Exception(String.format("hoSoBenhAn maTraoDoi=%s not found", maTraoDoiHsba));
            }
            
            var hcObjList = CDRUtils.getFieldAsList(map, "dsHoiChan");
            var hcList = FPUtil.transform(hcObjList, x -> objectMapper.convertValue(x, HoiChan.class));
            hoiChanService.createOrUpdateFromHIS(hsba, hcList, hcObjList, jsonSt);
            
            var result = DataConvertUtil.mapOf(
                "success" , true,
                "hcList", hcList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }
    }
    
}
