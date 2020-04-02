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

import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.PhauThuatThuThuat;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.PhauThuatThuThuatService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

@RestController
@RequestMapping("/api/pttt")
public class PhauThuatThuThuatController {
    
    @Autowired private PhauThuatThuThuatService phauThuatThuThuatService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();

    @GetMapping("/get_ds_pttt")
    public ResponseEntity<?> getDsPhauThuatThuThuat(@RequestParam("hsba_id") String id) {
        var ptttList = phauThuatThuThuatService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(ptttList);
    }
    
    private void saveToFhirDb(HoSoBenhAn hsba, List<PhauThuatThuThuat> ptttList) {
        if(hsba == null) return;
        try {
            var enc = hsba.getEncounterInDB();
            if(enc != null) {
                ptttList.forEach(x -> DichVuKyThuatHelper.saveDichVuKT(enc, x));
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_pttt")
    public ResponseEntity<?> createOrUpdatePtttFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElseThrow();
            
            var ptttObjList = (List<Object>) map.get("dsPhauThuatThuThuat");
            var ptttList = ptttObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, PhauThuatThuThuat.class))
                                .collect(Collectors.toList());
            
            phauThuatThuThuatService.createOrUpdateFromHIS(hsba, ptttList, jsonSt);
            
            // save to FHIR db
            saveToFhirDb(hsba, ptttList);
                        
            var result = DataConvertUtil.mapOf(
                "success" , true,
                "ptttList", ptttList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }
    }
}
