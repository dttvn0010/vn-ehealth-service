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

import vn.ehealth.emr.model.EmrChanDoanHinhAnh;
import vn.ehealth.emr.service.EmrChanDoanHinhAnhService;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.utils.DateUtil;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;

@RestController
@RequestMapping("/api/cdha")
public class EmrChanDoanHinhAnhController {
    
    private Logger logger = LoggerFactory.getLogger(EmrChanDoanHinhAnhController.class);
    @Autowired EmrChanDoanHinhAnhService emrChanDoanHinhAnhService;
    @Autowired EmrHoSoBenhAnService emrHoSoBenhAnService;
    
    @GetMapping("/get_ds_cdha")
    public ResponseEntity<?> getDsChanDoanHinhAnh(@RequestParam("hsba_id") String hsbaId) {
        var cdhaList = emrChanDoanHinhAnhService.getByEmrHoSoBenhAnId(new ObjectId(hsbaId));
        return ResponseEntity.ok(cdhaList);
    }
    
    @GetMapping("/get_ds_cdha_by_bn2")
    public ResponseEntity<?> getDsChanDoanHinhAnhByBenhNhan2(@RequestParam("benhnhan_id") String benhNhanId) {
    	var lst = emrChanDoanHinhAnhService.getByEmrBenhNhanId(new ObjectId(benhNhanId));
    	return ResponseEntity.ok(lst);
    }
    
    @GetMapping("/get_ds_cdha_by_bn")
    public ResponseEntity<?> getDsChanDoanHinhAnhByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
        var emrHoSoBenhAns = emrHoSoBenhAnService.getByEmrBenhNhanId(new ObjectId(benhNhanId));
        var result = new ArrayList<>();
        for(var emrHoSoBenhAn : emrHoSoBenhAns) {
           	var cdha_List = emrChanDoanHinhAnhService.getByEmrHoSoBenhAnId(emrHoSoBenhAn.id);
        	var lst = cdha_List.stream().map(x -> JsonUtil.objectToMap(x)).collect(Collectors.toList());
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

    @GetMapping("/delete_cdha")
    public ResponseEntity<?> deleteCdha(@RequestParam("cdha_id") String id) {
        try {
            emrChanDoanHinhAnhService.delete(new ObjectId(id));
            var result = Map.of("success" , true);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Error delete cdha:", e);
            var result = Map.of("success" , false);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/create_or_update_cdha")
    public ResponseEntity<?> createOrUpdateCdha(@RequestBody String jsonSt) {
        
        try {
            var mapper = EmrUtils.createObjectMapper();
            var cdha = mapper.readValue(jsonSt, EmrChanDoanHinhAnh.class);
            cdha = emrChanDoanHinhAnhService.createOrUpdate(cdha);
            
            var result = Map.of(
                "success" , true,
                "emrChanDoanHinhAnh", cdha 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            var result = Map.of(
                "success" , false,
                "errors", List.of(e.getMessage()) 
            );
            logger.error("Error save cdha:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
