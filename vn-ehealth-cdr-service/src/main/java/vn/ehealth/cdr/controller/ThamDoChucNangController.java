package vn.ehealth.cdr.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.cdr.controller.helper.EncounterHelper;
import vn.ehealth.cdr.controller.helper.ProcedureHelper;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.ThamDoChucNang;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.ThamDoChucNangService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

@RestController
@RequestMapping("/api/tdcn")
public class ThamDoChucNangController {
    
    private Logger log = LoggerFactory.getLogger(ThamDoChucNangController.class);
            
    @Autowired private ThamDoChucNangService thamDoChucNangService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    @Autowired private ProcedureHelper procedureHelper;
    @Autowired private EncounterHelper encounterHelper;
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @GetMapping("/get_ds_tdcn")
    public ResponseEntity<?> getDsThamDoChucNang(@RequestParam("hsba_id") String id) {
        var tdcnList = thamDoChucNangService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(tdcnList);
    }
        
    private void saveToFhirDb(HoSoBenhAn hsba, List<ThamDoChucNang> tdcnList) {
        if(hsba == null) return;
        
        try {
            var enc = encounterHelper.getEncounterByMaHsba(hsba.maYte);
            if(enc != null) {
                tdcnList.forEach(x -> procedureHelper.saveDVKT(enc, x));
            }
        }catch(Exception e) {
            log.error("Cannot save cdha from hsba id=" + hsba.getId() + " to fhir DB", e);
        }
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
            thamDoChucNangService.createOrUpdateFromHIS(hsba, tdcnList, jsonSt);
            
            // save to FHIR db
            saveToFhirDb(hsba, tdcnList);
            
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
