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
import vn.ehealth.cdr.model.XetNghiem;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.XetNghiemService;
import vn.ehealth.cdr.utils.CDRUtils;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/xetnghiem")
public class XetNghiemController {
    
    private Logger log = LoggerFactory.getLogger(XetNghiemController.class);
    
    @Autowired private XetNghiemService xetNghiemService;    
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    @Autowired private ProcedureHelper procedureHelper;
    @Autowired private EncounterHelper encounterHelper;

    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @GetMapping("/get_ds_xetnghiem")
    public ResponseEntity<?> getDsXetNghiem(@RequestParam("hsba_id") String id) {
        var xetnghiemList = xetNghiemService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(xetnghiemList);
    }
    
    private void saveToFhirDb(HoSoBenhAn hsba, List<XetNghiem> xetNghiemList) {
        if(hsba == null) return;
        try {
            var enc = encounterHelper.getEncounterByMaHsba(hsba.maYte);
            if(enc == null) return;
            
            for(var xetNghiem : xetNghiemList) {
                if(xetNghiem.dsDichVuXetNghiem == null) continue;
                
                for(var xndv : xetNghiem.dsDichVuXetNghiem) {
                    xndv.ngayYeuCau = xetNghiem.ngayYeuCau;
                    xndv.bacSiYeuCau = xetNghiem.bacSiYeuCau;
                    xndv.noiDungYeuCau = xetNghiem.noiDungYeuCau;
                    xndv.ngayThucHien = xetNghiem.ngayThucHien;
                    xndv.bacSiXetNghiem = xetNghiem.bacSiXetNghiem;
                    procedureHelper.saveDVKT(enc, xndv);
                }
            }
        }catch(Exception e) {
            log.error("Cannot save xetnghiem from hsba id=" + hsba.getId() + " to fhir DB", e);
        }
    }
    
    @PostMapping("/create_or_update_xetnghiem")
    public ResponseEntity<?> createOrUpdateXetnghiemFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElseThrow();
            
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
            
            xetNghiemService.createOrUpdateFromHIS(hsba, xetnghiemList, jsonSt);
            
            // save to FHIR db
            saveToFhirDb(hsba, xetnghiemList);
            
            var result = mapOf(
                "success" , true,
                "xetnghiemList", xetnghiemList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }
    }    
}
