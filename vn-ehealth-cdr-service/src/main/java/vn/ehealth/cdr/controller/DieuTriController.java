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

import vn.ehealth.cdr.model.DieuTri;
import vn.ehealth.cdr.service.DieuTriService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/dieutri")
public class DieuTriController {
    
    @Autowired private DieuTriService dieuTriService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @GetMapping("/get_ds_dieutri")
    public ResponseEntity<?> getDsDieutri(@RequestParam("hsba_id") String id) {        
        return ResponseEntity.ok(dieuTriService.getByHoSoBenhAnId(new ObjectId(id)));
    }
    
    @PostMapping("/create_or_update_dieu_tri")
    public ResponseEntity<?> createOrUpdateDieuTriFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElse(null);
            
            if(hsba == null) {
                throw new Exception(String.format("hoSoBenhAn maTraoDoi=%s not found", maTraoDoiHsba));
            }
            
            var dtObjList = CDRUtils.getFieldAsList(map, "dsDieuTri");
            var dtList = FPUtil.transform(dtObjList, x -> objectMapper.convertValue(x, DieuTri.class));
            dieuTriService.createOrUpdateFromHIS(hsba, dtList, dtObjList, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "dtList", dtList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }
    }
}
