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
import vn.ehealth.cdr.model.GiaiPhauBenh;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.service.GiaiPhauBenhService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

@RestController
@RequestMapping("/api/gpb")
public class GiaiPhauBenhController {
    
    @Autowired private GiaiPhauBenhService giaiPhauBenhService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    @Autowired private ProcedureHelper procedureHelper;
    @Autowired private EncounterHelper encounterHelper;
    
    private Logger log = LoggerFactory.getLogger(GiaiPhauBenhController.class);
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @GetMapping("/get_ds_gpb")
    public ResponseEntity<?> getDsGiaiPhauBenh(@RequestParam("hsba_id") String id) {
        var gpbList = giaiPhauBenhService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(gpbList);
    }
    
    @GetMapping("/get_ds_gpb_by_bn")
    public ResponseEntity<?> getDsGiaiPhauBenhByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
        var gpbList = giaiPhauBenhService.getByHoSoBenhAnId(new ObjectId(benhNhanId));
        return ResponseEntity.ok(gpbList);
    }
    
    private void saveToFhirDb(HoSoBenhAn hsba, List<GiaiPhauBenh> gpbList) {
        if(hsba == null) return;
        
        try {
            var enc = encounterHelper.getEncounterByMaHsba(hsba.maYte);
            if(enc != null) {
                gpbList.forEach(x -> procedureHelper.saveDVKT(enc, x));
            }
        }catch(Exception e) {
            log.error("Cannot save gpb from hsba id=" + hsba.getId() + " to fhir DB", e);
        }
    }
    
    @PostMapping("/create_or_update_gpb")
    public ResponseEntity<?> createOrUpdateGpbFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElseThrow();
            
            var gpbObjList = CDRUtils.getFieldAsList(map, "dsGiaiPhauBenh");
            var gpbList = FPUtil.transform(gpbObjList, x -> objectMapper.convertValue(x, GiaiPhauBenh.class));
            giaiPhauBenhService.createOrUpdateFromHIS(hsba, gpbList, jsonSt);
            
            // Save to Fhir
            saveToFhirDb(hsba, gpbList);
            
            var result = DataConvertUtil.mapOf(
                "success" , true,
                "gpbList", gpbList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }
    }
}
