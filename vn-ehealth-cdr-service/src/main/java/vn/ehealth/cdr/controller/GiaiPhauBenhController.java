package vn.ehealth.cdr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.cdr.controller.helper.ProcedureHelper;
import vn.ehealth.cdr.model.dto.GiaiPhauBenh;
import vn.ehealth.cdr.service.DichVuKyThuatService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

@RestController
@RequestMapping("/api/gpb")
public class GiaiPhauBenhController {
    
    @Autowired private DichVuKyThuatService dichVuKyThuatService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    @Autowired private ProcedureHelper procedureHelper;
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @GetMapping("/get_ds_gpb")
    public ResponseEntity<?> getDsGiaiPhauBenh(@RequestParam("hsba_id") String hsbaId) {
        var gpbList = dichVuKyThuatService.getByHsbaIdAndLoaiDVKT(hsbaId, LoaiDichVuKT.GIAI_PHAU_BENH);
        return ResponseEntity.ok(gpbList);
    }
    
    
    @PostMapping("/create_or_update_gpb")
    public ResponseEntity<?> createOrUpdateGpbFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElse(null);
            
            if(hsba == null) {
                throw new Exception(String.format("hoSoBenhAn maTraoDoi=%s not found", maTraoDoiHsba));
            }
            
            var gpbObjList = CDRUtils.getFieldAsList(map, "dsGiaiPhauBenh");
            var gpbList = FPUtil.transform(gpbObjList, x -> objectMapper.convertValue(x, GiaiPhauBenh.class));
            var dvktList = FPUtil.transform(gpbList, GiaiPhauBenh::toDichVuKyThuat);
            dichVuKyThuatService.createOrUpdateFromHIS(hsba, dvktList, jsonSt);
            
            // Save to Fhir
            procedureHelper.saveToFhirDb(hsba, dvktList);
            
            var result = DataConvertUtil.mapOf(
                "success" , true,
                "gpbList", gpbList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
}
