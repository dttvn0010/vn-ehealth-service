package vn.ehealth.cdr.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.model.Privilege;
import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.cdr.controller.helper.ProcedureHelper;
import vn.ehealth.cdr.model.dto.DsChanDoanHinhAnhDTO;
import vn.ehealth.cdr.service.DichVuKyThuatService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.LogService;
import vn.ehealth.cdr.service.YlenhService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;
import vn.ehealth.cdr.utils.CDRConstants.MA_HANH_DONG;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/cdha")
public class ChanDoanHinhAnhController {
    
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private YlenhService ylenhService;
    @Autowired private DichVuKyThuatService dichVuKyThuatService;
    @Autowired private LogService logService;    
    
    @Autowired private ProcedureHelper procedureHelper;
            
    @GetMapping("/get_ds_cdha")
    public ResponseEntity<?> getDsChanDoanHinhAnh(@RequestParam("hsba_id") String hsbaId) {
        try {
        	var user = UserUtil.getCurrentUser();
        	if(!user.isAdmin() && !user.hasPrivilege(Privilege.XEM_TAB_CDHA)) {
        		var result = Map.of("success", false, "noPermission", true);
        		return new ResponseEntity<>(result, HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
        	}
            var cdhaList = dichVuKyThuatService.getByHsbaIdAndLoaiDVKT(new ObjectId(hsbaId), LoaiDichVuKT.CHAN_DOAN_HINH_ANH);
            return ResponseEntity.ok(Map.of("cdhaList", cdhaList, "success", true));
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
        
    @PostMapping("/create_or_update_cdha")
    public ResponseEntity<?> createOrUpdateCdhaFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var body = JsonUtil.parseObject(jsonSt, DsChanDoanHinhAnhDTO.class);
            var hsba = hoSoBenhAnService.getByMaTraoDoi(body.maTraoDoiHoSo);
            
            if(hsba == null) {
                throw new Exception(String.format("hoSoBenhAn maTraoDoi=%s not found", body.maTraoDoiHoSo));
            }
            
            if(body.dsChanDoanHinhAnh != null) {
                for(var cdha : body.dsChanDoanHinhAnh) {
                    var ylenh = cdha.generateYlenh();
                    var dvkt = cdha.generateDichVuKyThuat();
                    
                    ylenh = ylenhService.createOrUpdateFromHis(hsba, ylenh);
                    dvkt = dichVuKyThuatService.createOrUpdate(ylenh, dvkt);
                    procedureHelper.saveToFhirDb(ylenh, dvkt);
                }
            }
            
            logService.logAction(DsChanDoanHinhAnhDTO.class.getName(), hsba.id, MA_HANH_DONG.THEM_SUA, new Date(),  
                    null, "", jsonSt);
            
            return ResponseEntity.ok(Map.of("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
}
