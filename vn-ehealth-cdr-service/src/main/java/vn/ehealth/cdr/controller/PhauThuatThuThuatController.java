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
import vn.ehealth.cdr.model.dto.PhauThuatThuThuat;
import vn.ehealth.cdr.service.DichVuKyThuatService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

@RestController
@RequestMapping("/api/pttt")
public class PhauThuatThuThuatController {
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    @Autowired private DichVuKyThuatService dichVuKyThuatService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    @Autowired private ProcedureHelper procedureHelper;

    @GetMapping("/get_ds_pttt")
    public ResponseEntity<?> getDsPhauThuatThuThuat(@RequestParam("hsba_id") String id) {
        var ptttList = dichVuKyThuatService.getByHsbaIdAndLoaiDVKT(id, LoaiDichVuKT.PHAU_THUAT_THU_THUAT);
        return ResponseEntity.ok(ptttList);
    }
    
    @PostMapping("/create_or_update_pttt")
    public ResponseEntity<?> createOrUpdatePtttFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElse(null);
            
            if(hsba == null) {
                throw new Exception(String.format("hoSoBenhAn maTraoDoi=%s not found", maTraoDoiHsba));
            }
            
            var ptttObjList = CDRUtils.getFieldAsList(map, "dsPhauThuatThuThuat");
            var ptttList = FPUtil.transform(ptttObjList, x -> objectMapper.convertValue(x, PhauThuatThuThuat.class));
            var dvktList = FPUtil.transform(ptttList, PhauThuatThuThuat::toDichVuKyThuat);
            
            dichVuKyThuatService.createOrUpdateFromHIS(hsba, dvktList, jsonSt);
                        
            // save to FHIR db
            procedureHelper.saveToFhirDb(hsba, dvktList);
                        
            var result = DataConvertUtil.mapOf(
                "success" , true,
                "ptttList", ptttList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
}
