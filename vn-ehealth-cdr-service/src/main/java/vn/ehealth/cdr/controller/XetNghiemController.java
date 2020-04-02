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
import vn.ehealth.cdr.utils.CDRUtils;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.cdr.validate.JsonParser;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/xetnghiem")
public class XetNghiemController {
    
    @Autowired private XetNghiemService xetNghiemService;
    
    @Autowired private HoSoBenhAnService hoSoBenhAnService;

    private JsonParser jsonParser = new JsonParser();
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
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
                if(xetNghiem.dsDichVuXetNghiem == null) continue;
                
                for(var xndv : xetNghiem.dsDichVuXetNghiem) {
                    xndv.ngayYeuCau = xetNghiem.ngayYeuCau;
                    xndv.bacSiYeuCau = xetNghiem.bacSiYeuCau;
                    xndv.noiDungYeuCau = xetNghiem.noiDungYeuCau;
                    xndv.ngayThucHien = xetNghiem.ngayThucHien;
                    xndv.bacSiXetNghiem = xetNghiem.bacSiXetNghiem;
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
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = jsonParser.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElseThrow();
            
            var xetnghiemList = CDRUtils.getFieldAsList(map, "dsXetNghiem");
            if(xetnghiemList == null) {
                throw new Exception("dsXetNghiem is null");
            }
            
            for(var xetnghiem: xetnghiemList) {
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
            return CDRUtils.errorResponse(e);
        }
    }    
}
