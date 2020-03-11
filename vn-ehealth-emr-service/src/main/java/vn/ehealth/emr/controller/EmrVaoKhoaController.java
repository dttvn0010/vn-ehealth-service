package vn.ehealth.emr.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

import vn.ehealth.emr.model.EmrVaoKhoa;
import vn.ehealth.emr.service.EmrVaoKhoaService;
import vn.ehealth.emr.utils.EmrUtils;

@RestController
@RequestMapping("/api/vaokhoa")
public class EmrVaoKhoaController {

    @Autowired EmrVaoKhoaService emrVaoKhoaService;
    
    @GetMapping("/get_ds_vaokhoa")
    public ResponseEntity<?> getDsVaoKhoa(@RequestParam("hsba_id") String id) {
        var vkList = emrVaoKhoaService.getByEmrHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(vkList);
    }
    
    @SuppressWarnings("rawtypes")
    @PostMapping("save_ds_vaokhoa")
    public ResponseEntity<?> saveDsVaoKhoa(@RequestBody Map<String, Object> body) throws IOException {
        String hsbaId = (String) body.get("hsbaId");       
        var emrVaoKhoasJson = (List) body.get("emrVaoKhoas");
        var mapper = EmrUtils.createObjectMapper();        
        var emrVaoKhoas = new ArrayList<EmrVaoKhoa>();
        
        for(var emrVaoKhoaJson : emrVaoKhoasJson) {
            var emrVaoKhoa = mapper.readValue(mapper.writeValueAsString(emrVaoKhoaJson), EmrVaoKhoa.class);
            emrVaoKhoas.add(emrVaoKhoa);
        }
        emrVaoKhoaService.saveByEmrHoSoBenhAnId(new ObjectId(hsbaId), emrVaoKhoas);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
