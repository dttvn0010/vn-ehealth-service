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

import vn.ehealth.emr.model.EmrDonThuoc;
import vn.ehealth.emr.service.EmrDonThuocService;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.utils.DateUtil;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;

@RestController
@RequestMapping("/api/donthuoc")
public class EmrDonThuocController {
    
    private Logger logger = LoggerFactory.getLogger(EmrDonThuocController.class);
    @Autowired EmrDonThuocService emrDonThuocService;
    @Autowired EmrHoSoBenhAnService emrHoSoBenhAnService;
    
    @GetMapping("/get_ds_donthuoc")
    public ResponseEntity<?> getDsDonThuoc(@RequestParam("hsba_id") String id) {
        var donthuocList = emrDonThuocService.getByEmrHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(donthuocList);
    }
    
    @GetMapping("/get_ds_donthuoc_by_bn")
    public ResponseEntity<?> getDsDonThuocByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
        var emrHoSoBenhAns = emrHoSoBenhAnService.getByEmrBenhNhanId(new ObjectId(benhNhanId));
        var result = new ArrayList<>();
        for(var emrHoSoBenhAn : emrHoSoBenhAns) {
        	var donThuocList = emrDonThuocService.getByEmrHoSoBenhAnId(emrHoSoBenhAn.id); 
        	var lst = donThuocList.stream().map(x -> JsonUtil.objectToMap(x)).collect(Collectors.toList());
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
    
    @GetMapping("/delete_donthuoc")
    public ResponseEntity<?> deleteDonthuoc(@RequestParam("donthuoc_id") String id) {
        try {
            emrDonThuocService.delete(new ObjectId(id));
            var result = Map.of("success" , true);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Error delete donthuoc:", e);
            var result = Map.of("success" , false);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/create_or_update_donthuoc")
    public ResponseEntity<?> createOrUpdateDonthuoc(@RequestBody String jsonSt) {
        
        try {
            var mapper = EmrUtils.createObjectMapper();            
            var donthuoc = mapper.readValue(jsonSt, EmrDonThuoc.class);
            donthuoc = emrDonThuocService.createOrUpdate(donthuoc);
            
            var result = Map.of(
                "success" , true,
                "emrDonThuoc", donthuoc 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            var result = Map.of(
                "success" , false,
                "errors", List.of(e.getMessage()) 
            );
            logger.error("Error save donthuoc:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

}
