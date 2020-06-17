package vn.ehealth.cdr.controller;

import java.util.ArrayList;
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
import vn.ehealth.cdr.model.DichVuKyThuat;
import vn.ehealth.cdr.model.dto.XetNghiem;
import vn.ehealth.cdr.service.DichVuKyThuatService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;
import vn.ehealth.cdr.utils.CDRUtils;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/xetnghiem")
public class XetNghiemController {

    @Autowired private DichVuKyThuatService dichVuKyThuatService;    
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    @Autowired private ProcedureHelper procedureHelper;

    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @GetMapping("/get_ds_xetnghiem")
    public ResponseEntity<?> getDsXetNghiem(@RequestParam("hsba_id") String hsbaId) {
        var xetnghiemList = dichVuKyThuatService.getByHsbaIdAndLoaiDVKT(hsbaId, LoaiDichVuKT.XET_NGHIEM);
        return ResponseEntity.ok(xetnghiemList);
    }
    
    @PostMapping("/create_or_update_xetnghiem")
    public ResponseEntity<?> createOrUpdateXetnghiemFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElse(null);
            
            if(hsba == null) {
                throw new Exception(String.format("hoSoBenhAn maTraoDoi=%s not found", maTraoDoiHsba));
            }
            
            var xnObjList = CDRUtils.getFieldAsList(map, "dsXetNghiem");
            if(xnObjList == null) {
                throw new Exception("dsXetNghiem is null");
            }
            
            for(var xetnghiem: xnObjList) {
                var xndvList = CDRUtils.getFieldAsList(xetnghiem, "dsDichVuXetNghiem");
                if(xndvList == null) continue;
                
                for(var xndv : xndvList) {
                    var xnkqList = CDRUtils.getFieldAsList(xndv, "dsKetQuaXetNghiem");
                    if(xnkqList == null) continue;
                    
                    for(var xnkq : xnkqList) {
                        var chisoxn = CDRUtils.getFieldAsObject(xnkq, "dmChiSoXetNghiem");
                        if(chisoxn != null) {
                            var extension = mapOf(
                                "donvi", chisoxn.getOrDefault("donvi", ""),
                                "chisobtnam", chisoxn.getOrDefault("chisobtnam", ""),
                                "chisobtnu", chisoxn.getOrDefault("chisobtnu", "")
                            );
                            
                            chisoxn.put("extension", extension);
                        }
                    }
                }
            }
            
            var xetnghiemList = FPUtil.transform(xnObjList,
                                        x -> objectMapper.convertValue(x, XetNghiem.class));
            
            var dvktList = new ArrayList<DichVuKyThuat>();
            
            for(var xn : xetnghiemList) {
                dvktList.addAll(xn.toDsDichVuKyThuat());
            }
            
            dichVuKyThuatService.createOrUpdateFromHIS(hsba, dvktList, jsonSt);
            
            // save to FHIR db
            procedureHelper.saveToFhirDb(hsba, dvktList);
            
            var result = mapOf(
                "success" , true,
                "xetnghiemList", xetnghiemList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }    
}
