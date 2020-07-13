package vn.ehealth.cdr.controller;

import java.util.Date;
import java.util.Map;

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
import vn.ehealth.cdr.model.dto.DSXetNghiemDTO;
import vn.ehealth.cdr.service.DichVuKyThuatService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.LogService;
import vn.ehealth.cdr.service.YlenhService;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;
import vn.ehealth.cdr.utils.CDRConstants.MA_HANH_DONG;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

@RestController
@RequestMapping("/api/xetnghiem")
public class XetNghiemController {

    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private YlenhService ylenhService;
    @Autowired private DichVuKyThuatService dichVuKyThuatService;
    @Autowired private LogService logService;
    
    @Autowired private ProcedureHelper procedureHelper;

    @GetMapping("/get_ds_xetnghiem")
    public ResponseEntity<?> getDsXetNghiem(@RequestParam("hsba_id") String hsbaId) {
    	var user = UserUtil.getCurrentUser();
    	if(!user.isAdmin() && !user.hasPrivilege(Privilege.XEM_TAB_XN)) {
    		var result = Map.of("success", false, "noPermission", true);
    		return new ResponseEntity<>(result, HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
    	}
        var xetnghiemList = dichVuKyThuatService.getByHsbaIdAndLoaiDVKT(new ObjectId(hsbaId), LoaiDichVuKT.XET_NGHIEM);
        return ResponseEntity.ok(xetnghiemList);
    }
    
    @PostMapping("/create_or_update_xetnghiem")
    public ResponseEntity<?> createOrUpdateXetnghiemFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var body = JsonUtil.parseObject(jsonSt, DSXetNghiemDTO.class);
            var hsba = hoSoBenhAnService.getByMaTraoDoi(body.maTraoDoiHoSo).orElse(null);
            
            if(hsba == null) {
                throw new Exception(String.format("hoSoBenhAn maTraoDoi=%s not found", body.maTraoDoiHoSo));
            }
            
            if(body.dsXetNghiem != null) {
           
                for(var xetNghiem : body.dsXetNghiem) {
                    var ylenh = xetNghiem.generateYlenh();
                    var dvktList = xetNghiem.generateDsDichVuKyThuat();
                    ylenh = ylenhService.createOrUpdateFromHis(hsba, ylenh);
                    for(var dvkt : dvktList) {
                        dvkt = dichVuKyThuatService.createOrUpdate(ylenh, dvkt);
                        procedureHelper.saveToFhirDb(ylenh, dvkt);
                    }
                }
            }
            
            logService.logAction(DSXetNghiemDTO.class.getName(), hsba.id, MA_HANH_DONG.THEM_SUA, new Date(),  
                    null, "", jsonSt);
            
            return ResponseEntity.ok(Map.of("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }    
}
