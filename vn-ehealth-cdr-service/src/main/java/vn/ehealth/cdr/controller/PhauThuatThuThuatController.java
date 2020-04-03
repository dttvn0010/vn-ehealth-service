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
import vn.ehealth.cdr.model.PhauThuatThuThuat;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.PhauThuatThuThuatService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

@RestController
@RequestMapping("/api/pttt")
public class PhauThuatThuThuatController {
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    private Logger log = LoggerFactory.getLogger(PhauThuatThuThuatController.class);
            
    @Autowired private PhauThuatThuThuatService phauThuatThuThuatService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    @Autowired private ProcedureHelper procedureHelper;
    @Autowired private EncounterHelper encounterHelper;

    @GetMapping("/get_ds_pttt")
    public ResponseEntity<?> getDsPhauThuatThuThuat(@RequestParam("hsba_id") String id) {
        var ptttList = phauThuatThuThuatService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(ptttList);
    }
    
    private void saveToFhirDb(HoSoBenhAn hsba, List<PhauThuatThuThuat> ptttList) {
        if(hsba == null) return;
        
        try {
            var enc = encounterHelper.getEncounterByMaHsba(hsba.maYte);
            if(enc != null) {            
                ptttList.forEach(x -> procedureHelper.saveDVKT(enc, x));
            }            
        }catch(Exception e) {
            log.error("Cannot save cdha from hsba id=" + hsba.getId() + " to fhir DB", e);
        }
    }
    
    @PostMapping("/create_or_update_pttt")
    public ResponseEntity<?> createOrUpdatePtttFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElseThrow();
            
            var ptttObjList = CDRUtils.getFieldAsList(map, "dsPhauThuatThuThuat");
            var ptttList = FPUtil.transform(ptttObjList, x -> objectMapper.convertValue(x, PhauThuatThuThuat.class)); 
            
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
