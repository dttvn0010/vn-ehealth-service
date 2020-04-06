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
import vn.ehealth.cdr.model.ChanDoanHinhAnh;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.service.ChanDoanHinhAnhService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/cdha")
public class ChanDoanHinhAnhController {
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @Autowired private ChanDoanHinhAnhService chanDoanHinhAnhService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    @Autowired private ProcedureHelper procedureHelper;
    @Autowired private EncounterHelper encounterHelper;
    
    private Logger log = LoggerFactory.getLogger(ChanDoanHinhAnhController.class);
            
    @GetMapping("/get_ds_cdha")
    public ResponseEntity<?> getDsChanDoanHinhAnh(@RequestParam("hsba_id") String hsbaId) {
        var cdhaList = chanDoanHinhAnhService.getByHoSoBenhAnId(new ObjectId(hsbaId));
        return ResponseEntity.ok(cdhaList);
    }
    
    private void saveToFhirDb(HoSoBenhAn hsba, List<ChanDoanHinhAnh> cdhaList) {
        if(hsba == null) return;
        
        try {
            var enc = encounterHelper.getEncounterByMaHsba(hsba.maYte);
            if(enc != null) {
                cdhaList.forEach(x -> procedureHelper.saveDVKT(enc, x));
            }
        }catch(Exception e) {
            log.error("Cannot save cdha from hsba id=" + hsba.getId() + " to fhir DB", e);
        }
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
            
            chanDoanHinhAnhService.createOrUpdateFromHIS(hsba, cdhaList, jsonSt);
            
            // save to FHIR db
            saveToFhirDb(hsba, cdhaList);
            
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
