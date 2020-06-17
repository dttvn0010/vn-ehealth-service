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
import vn.ehealth.cdr.model.dto.ThamDoChucNang;
import vn.ehealth.cdr.service.DichVuKyThuatService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

@RestController
@RequestMapping("/api/tdcn")
public class ThamDoChucNangController {
    
    @Autowired private DichVuKyThuatService dichVuKyThuatService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    @Autowired private ProcedureHelper procedureHelper;
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @GetMapping("/get_ds_tdcn")
    public ResponseEntity<?> getDsThamDoChucNang(@RequestParam("hsba_id") String hsbaId) {
        var tdcnList = dichVuKyThuatService.getByHsbaIdAndLoaiDVKT(hsbaId, LoaiDichVuKT.THAM_DO_CHUC_NANG);
        return ResponseEntity.ok(tdcnList);
    }
   
    @PostMapping("/create_or_update_tdcn")
    public ResponseEntity<?> createOrUpdateTdcnFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElse(null);
            
            if(hsba == null) {
                throw new Exception(String.format("hoSoBenhAn maTraoDoi=%s not found", maTraoDoiHsba));
            }
            
            var tdcnObjList = CDRUtils.getFieldAsList(map, "dsThamDoChucNang");
            var tdcnList = FPUtil.transform(tdcnObjList, x -> objectMapper.convertValue(x, ThamDoChucNang.class));
            var dvktList = FPUtil.transform(tdcnList, ThamDoChucNang::toDichVuKyThuat);
            dichVuKyThuatService.createOrUpdateFromHIS(hsba, dvktList, jsonSt);
            
            // save to FHIR db
            procedureHelper.saveToFhirDb(hsba, dvktList);
            
            var result = DataConvertUtil.mapOf(
                "success" , true,
                "tdcnList", tdcnList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
}
