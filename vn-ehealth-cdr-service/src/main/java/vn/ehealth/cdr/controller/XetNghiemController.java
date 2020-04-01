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

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.XetNghiem;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.XetNghiemService;
import vn.ehealth.cdr.utils.EmrUtils;
import vn.ehealth.cdr.validate.JsonParser;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/xetnghiem")
public class XetNghiemController {
    
    @Autowired private XetNghiemService xetNghiemService;
    
    @Autowired private HoSoBenhAnService hoSoBenhAnService;

    private JsonParser jsonParser = new JsonParser();
    
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @GetMapping("/get_ds_xetnghiem")
    public ResponseEntity<?> getDsXetNghiem(@RequestParam("hsba_id") String id) {
        var xetnghiemList = xetNghiemService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(xetnghiemList);
    }
    
    private void saveToFhirDb(HoSoBenhAn hsba, List<XetNghiem> xetNghiemList) {
        if(hsba == null) return;
        try {
            var enc = hsba.getEncounterInDB();
            if(enc == null) return;
            
            for(var xetNghiem : xetNghiemList) {
                if(xetNghiem.emrXetNghiemDichVus == null) continue;
                
                for(var xndv : xetNghiem.emrXetNghiemDichVus) {
                    xndv.ngayyeucau = xetNghiem.ngayyeucau;
                    xndv.bacsiyeucau = xetNghiem.bacsiyeucau;
                    xndv.noidungyeucau = xetNghiem.noidungyeucau;
                    xndv.ngaythuchien = xetNghiem.ngaythuchien;
                    xndv.bacsixetnghiem = xetNghiem.bacsixetnghiem;
                    DichVuKyThuatHelper.saveDichVuKT(enc, xndv);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @PostMapping("/create_or_update_xetnghiem")
    public ResponseEntity<?> createOrUpdateXetnghiemFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = hoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var xetnghiemList = EmrUtils.getFieldAsList(map, "emrXetNghiems");
            if(xetnghiemList == null) {
                throw new Exception("emrXetNghiems is null");
            }
            
            for(var xetnghiem: xetnghiemList) {
                var xndvList = EmrUtils.getFieldAsList(xetnghiem, "emrXetNghiemDichVus");
                if(xndvList == null) continue;
                
                for(var xndv : xndvList) {
                    var xnkqList = EmrUtils.getFieldAsList(xndv, "emrXetNghiemKetQuas");
                    if(xnkqList == null) continue;
                    
                    for(var xnkq : xnkqList) {
                        var chisoxn = EmrUtils.getFieldAsObject(xnkq, "emrDmChiSoXetNghiem");
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
            
            var xetnghiemModelList = xetnghiemList.stream()
                                .map(obj -> objectMapper.convertValue(obj, XetNghiem.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            xetNghiemService.createOrUpdateFromHIS(userId, hsba, xetnghiemModelList, jsonSt);
            
            // save to FHIR db
            saveToFhirDb(hsba, xetnghiemModelList);
            
            var result = mapOf(
                "success" , true,
                "xetnghiemList", xetnghiemModelList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }    
}
