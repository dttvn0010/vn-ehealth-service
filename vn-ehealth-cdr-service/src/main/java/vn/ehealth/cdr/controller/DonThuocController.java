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

import vn.ehealth.cdr.controller.helper.MedicationRequestHelper;
import vn.ehealth.cdr.model.dto.DsDonThuocDTO;
import vn.ehealth.cdr.service.DonThuocService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.LogService;
import vn.ehealth.cdr.service.YlenhService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.cdr.utils.CDRConstants.MA_HANH_DONG;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;


@RestController
@RequestMapping("/api/donthuoc")
public class DonThuocController {
    
    @Autowired private LogService logService;
    @Autowired private DonThuocService donThuocService;
    @Autowired private YlenhService ylenhService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private MedicationRequestHelper medicationRequestHelper;
        
    @GetMapping("/get_ds_donthuoc")
    public ResponseEntity<?> getDsDonThuoc(@RequestParam("hsba_id") String id) {
        var donthuocList = donThuocService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(donthuocList);
    }
    
    @PostMapping("/create_or_update_don_thuoc")
    public ResponseEntity<?> createOrUpdateDonThuocFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var body = JsonUtil.parseObject(jsonSt, DsDonThuocDTO.class);
            var hsba = hoSoBenhAnService.getByMaTraoDoi(body.maTraoDoiHoSo);
            
            if(hsba == null) {
                throw new Exception(String.format("hoSoBenhAn maTraoDoi=%s not found", body.maTraoDoiHoSo));
            }
            
            if(body.dsDonThuoc != null) {
                for(var donThuocDTO : body.dsDonThuoc) {
                    var ylenh = donThuocDTO.generateYlenh();
                    var donThuoc = donThuocDTO.generateDonThuoc();
                    ylenh.trangThai = donThuoc.trangThai;
                    ylenh = ylenhService.createOrUpdateFromHis(hsba, ylenh);
                    donThuoc = donThuocService.createOrUpdate(ylenh, donThuoc);
                    medicationRequestHelper.saveToFhirDb(hsba, donThuoc.getDsDonThuocChiTiet());
                }
            }            
            
            logService.logAction(DsDonThuocDTO.class.getName(), hsba.id, MA_HANH_DONG.THEM_SUA, new Date(), null, 
                    "", jsonSt);
            
            return ResponseEntity.ok(Map.of("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }    
}
