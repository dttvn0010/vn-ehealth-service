package vn.ehealth.emr.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.model.EmrPhauThuatThuThuat;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.service.EmrPhauThuatThuThuatService;
import vn.ehealth.emr.utils.DateUtil;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;

@RestController
@RequestMapping("/api/pttt")
public class EmrPhauThuatThuThuatController {
    
    private Logger logger = LoggerFactory.getLogger(EmrPhauThuatThuThuatController.class);
            
    @Autowired EmrPhauThuatThuThuatService emrPhauThuatThuThuatService;
    @Autowired EmrHoSoBenhAnService emrHoSoBenhAnService;
    
    @GetMapping("/get_ds_pttt")
    public ResponseEntity<?> getDsPhauThuatThuThuat(@RequestParam("hsba_id") String id) {
        var ptttList = emrPhauThuatThuThuatService.getByEmrHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(ptttList);
    }
    
    @GetMapping("/get_ds_pttt_by_bn")
    public ResponseEntity<?> getDsPhauThuatThuThuatByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
        var emrHoSoBenhAns = emrHoSoBenhAnService.getByEmrBenhNhanId(new ObjectId(benhNhanId));
        var result = new ArrayList<>();
        for(var emrHoSoBenhAn : emrHoSoBenhAns) {
           	var ptttList = emrPhauThuatThuThuatService.getByEmrHoSoBenhAnId(emrHoSoBenhAn.id);
        	var lst = ptttList.stream().map(x -> JsonUtil.objectToMap(x)).collect(Collectors.toList());
        	lst.forEach(x -> {
        		x.put("tenCoSoKhamBenh", emrHoSoBenhAn.getEmrCoSoKhamBenh().ten);
        		x.put("soBenhAn", emrHoSoBenhAn.matraodoi);
        		x.put("ngayVaoVien", DateUtil.parseDateToString(emrHoSoBenhAn.emrQuanLyNguoiBenh.ngaygiovaovien, "dd/MM/yyyy HH:mm"));
        		x.put("ngayRaVien", DateUtil.parseDateToString(emrHoSoBenhAn.emrQuanLyNguoiBenh.ngaygioravien, "dd/MM/yyyy HH:mm"));
        	});
            result.addAll(lst);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/delete_pttt")
    public ResponseEntity<?> deletePttt(@RequestParam("pttt_id") String id) {
        try {
            emrPhauThuatThuThuatService.delete(new ObjectId(id));
            var result = Map.of("success" , true);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Error delete pttt:", e);
            var result = Map.of("success" , false);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/create_or_update_pttt")
    public ResponseEntity<?> createOrUpdatePttt(@RequestBody String jsonSt) {
        
        try {
            var mapper = EmrUtils.createObjectMapper();
            var pttt = mapper.readValue(jsonSt, EmrPhauThuatThuThuat.class);
            pttt = emrPhauThuatThuThuatService.createOrUpdate(pttt);
            
            var result = Map.of(
                "success" , true,
                "emrPhauThuatThuThuat", pttt 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            var result = Map.of(
                "success" , false,
                "errors", List.of(e.getMessage()) 
            );
            logger.error("Error save pttt:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
