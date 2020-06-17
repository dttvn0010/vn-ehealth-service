package vn.ehealth.cdr.controller;

import java.util.Date;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.cdr.controller.helper.ProcedureHelper;
import vn.ehealth.cdr.model.dto.DsGiaiPhauBenhDTO;
import vn.ehealth.cdr.service.DichVuKyThuatService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.LogService;
import vn.ehealth.cdr.service.YlenhService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;
import vn.ehealth.cdr.utils.CDRConstants.MA_HANH_DONG;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

@RestController
@RequestMapping("/api/gpb")
public class GiaiPhauBenhController {
        
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private YlenhService ylenhService;
    @Autowired private DichVuKyThuatService dichVuKyThuatService;
    @Autowired private LogService logService;
    
    @Autowired private ProcedureHelper procedureHelper;
    
    @GetMapping("/get_ds_gpb")
    public ResponseEntity<?> getDsGiaiPhauBenh(@RequestParam("hsba_id") String hsbaId) {
        var gpbList = dichVuKyThuatService.getByHsbaIdAndLoaiDVKT(new ObjectId(hsbaId), LoaiDichVuKT.GIAI_PHAU_BENH);
        return ResponseEntity.ok(gpbList);
    }
            
    @PostMapping("/create_or_update_gpb")
    public ResponseEntity<?> createOrUpdateGpbFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var body = JsonUtil.parseObject(jsonSt, DsGiaiPhauBenhDTO.class);
            var hsba = hoSoBenhAnService.getByMaTraoDoi(body.maTraoDoiHoSo).orElse(null);
            
            if(hsba == null) {
                throw new Exception(String.format("hoSoBenhAn maTraoDoi=%s not found", body.maTraoDoiHoSo));
            }
            
            if(body.dsGiaiPhauBenh != null) {
                for(var gpb : body.dsGiaiPhauBenh) {
                    var ylenh = gpb.generateYlenh();
                    var dvkt = gpb.generateDichVuKyThuat();
                    
                    ylenh = ylenhService.createOrUpdateFromHis(hsba, ylenh);
                    dvkt = dichVuKyThuatService.createOrUpdate(ylenh, dvkt);
                    procedureHelper.saveToFhirDb(ylenh, dvkt);
                }
            }
            
            logService.logAction(DsGiaiPhauBenhDTO.class.getName(), hsba.id, MA_HANH_DONG.THEM_SUA, new Date(),  
                    null, "", jsonSt);
            
            return ResponseEntity.ok(Map.of("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
}
