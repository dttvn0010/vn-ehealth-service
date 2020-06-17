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
import vn.ehealth.cdr.model.dto.ChanDoanHinhAnh;
import vn.ehealth.cdr.service.DichVuKyThuatService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/cdha")
public class ChanDoanHinhAnhController {
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @Autowired private DichVuKyThuatService dichVuKyThuatService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    @Autowired private ProcedureHelper procedureHelper;
            
    @GetMapping("/get_ds_cdha")
    public ResponseEntity<?> getDsChanDoanHinhAnh(@RequestParam("hsba_id") String hsbaId) {
        var cdhaList = dichVuKyThuatService.getByHsbaIdAndLoaiDVKT(hsbaId, LoaiDichVuKT.CHAN_DOAN_HINH_ANH);
        return ResponseEntity.ok(cdhaList);
    }
    
    
    @PostMapping("/create_or_update_cdha")
    public ResponseEntity<?> createOrUpdateCdhaFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElse(null);
            
            if(hsba == null) {
                throw new Exception(String.format("hoSoBenhAn maTraoDoi=%s not found", maTraoDoiHsba));
            }
            
            var cdhaObjList = CDRUtils.getFieldAsList(map, "dsChanDoanHinhAnh");
            var cdhaList = FPUtil.transform(cdhaObjList, x -> objectMapper.convertValue(x, ChanDoanHinhAnh.class));
            var dvktList = FPUtil.transform(cdhaList, ChanDoanHinhAnh::toDichVuKyThuat);
            
            dichVuKyThuatService.createOrUpdateFromHIS(hsba, dvktList, jsonSt);
            
            // save to FHIR db
            procedureHelper.saveToFhirDb(hsba, dvktList);
            
            var result = mapOf(
                "success" , true,
                "cdhaList", cdhaList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
}
