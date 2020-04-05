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

import vn.ehealth.cdr.model.HinhAnhTonThuong;
import vn.ehealth.cdr.service.HinhAnhTonThuongService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

@RestController
@RequestMapping("/api/hatt")
public class HinhAnhTonThuongController {
    @Autowired private HinhAnhTonThuongService hinhAnhTonThuongService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();

    @GetMapping("/get_hatt")
    public ResponseEntity<?> getHatt(@RequestParam("hatt_id") String id) {
        var hatt = hinhAnhTonThuongService.getById(new ObjectId(id));
        return ResponseEntity.of(hatt);
    }
    
    @GetMapping("/get_ds_hatt")
    public ResponseEntity<?> getDsHatt(@RequestParam("hsba_id") String hsbaId) {
        var hattList = hinhAnhTonThuongService.getByHoSoBenhAnId(new ObjectId(hsbaId));
        return ResponseEntity.ok(hattList);
    }
    
    @PostMapping("/create_or_update_hatt")
    public ResponseEntity<?> createOrUpdateChamSocFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElse(null);
            
            if(hsba == null) {
                throw new Exception(String.format("hoSoBenhAn maTraoDoi=%s not found", maTraoDoiHsba));
            }
            
            var hattObjList = CDRUtils.getFieldAsList(map, "dsHinhAnhTonThuong");
            var hattList = FPUtil.transform(hattObjList, x -> objectMapper.convertValue(x, HinhAnhTonThuong.class));
            
            hinhAnhTonThuongService.createOrUpdateFromHIS(hsba, hattList, jsonSt);
            
            var result = DataConvertUtil.mapOf(
                "success" , true,
                "hattList", hattList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }
    }
}
