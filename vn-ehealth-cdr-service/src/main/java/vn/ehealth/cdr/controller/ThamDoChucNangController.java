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

import vn.ehealth.cdr.model.ThamDoChucNang;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.ThamDoChucNangService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

@RestController
@RequestMapping("/api/tdcn")
public class ThamDoChucNangController {
    
    @Autowired 
    private ThamDoChucNangService thamDoChucNangService;
    @Autowired HoSoBenhAnService hoSoBenhAnService;
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @GetMapping("/get_ds_tdcn")
    public ResponseEntity<?> getDsThamDoChucNang(@RequestParam("hsba_id") String id) {
        var tdcnList = thamDoChucNangService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(tdcnList);
    }
        
    @PostMapping("/create_or_update_tdcn")
    public ResponseEntity<?> createOrUpdateTdcnFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElseThrow();
            
            var tdcnObjList = CDRUtils.getFieldAsList(map, "dsThamDoChucNang");
            var tdcnList = FPUtil.transform(tdcnObjList, x -> objectMapper.convertValue(x, ThamDoChucNang.class));
            thamDoChucNangService.createOrUpdateFromHIS(hsba, tdcnList, jsonSt);
            
            var result = DataConvertUtil.mapOf(
                "success" , true,
                "tdcnList", tdcnList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }
    }
}
