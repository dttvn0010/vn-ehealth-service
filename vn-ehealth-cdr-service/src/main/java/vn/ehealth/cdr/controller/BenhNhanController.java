package vn.ehealth.cdr.controller;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.cdr.controller.helper.PatientHelper;
import vn.ehealth.cdr.model.BenhNhan;
import vn.ehealth.cdr.service.BenhNhanService;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.patient.dao.impl.PatientDao;
import vn.ehealth.cdr.utils.*;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/benh_nhan")
public class BenhNhanController {
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @Autowired private BenhNhanService benhNhanService;
    @Autowired private PatientHelper patientHelper;
    @Autowired private PatientDao patientDao;
    
    @GetMapping("/count_benhnhan")
    public long countBenhNhan(@RequestParam String keyword) {
        return benhNhanService.countBenhNhan(keyword);
    }
    
    @GetMapping("/search_benhnhan")
    public ResponseEntity<?> searchBenhNhan(@RequestParam String keyword, 
                                            @RequestParam Optional<Integer> start, 
                                            @RequestParam Optional<Integer> count) {
        var benhNhans = benhNhanService.searchBenhNhan(keyword, start.orElse(-1), count.orElse(-1));
        return ResponseEntity.ok(benhNhans);
    }
    
    @GetMapping("/get_benhnhan_by_id")
    public ResponseEntity<?> getBenhNhan(@RequestParam String id) {
        var benhNhan = benhNhanService.getById(new ObjectId(id));
        return ResponseEntity.of(benhNhan);
    }
    
    @GetMapping("/get_id_by_sobhyte")
    public ResponseEntity<?> getIdBySobhyt(@RequestParam String sobhyt) {
        var benhNhan = benhNhanService.getBySobhyt(sobhyt);
        var id = benhNhan.map(x -> x.getId()).orElse("");
        return ResponseEntity.ok(mapOf("id", id));
    }
    
    private IdType getBenhNhanFhirId(@Nonnull BenhNhan benhNhan) {
        var patient = patientHelper.getPatientBySobhyt(benhNhan.sobhyt);
        if(patient != null) {
            return patient.getIdElement();
        }
        return null;
    }
    
    private void saveToFhirDb(BenhNhan benhNhan) throws Exception {
        if(benhNhan == null) return;
        
        var patient = benhNhan.toFhir();
        
        if(patient != null) {
            var fhirId = getBenhNhanFhirId(benhNhan);
            if(fhirId == null) {
                patientDao.create(patient);
            }else {
                patientDao.update(patient, fhirId);
            }
        }
    }

    @PostMapping("/create_or_update_benhnhan")
    public ResponseEntity<?> createOrUpdateBenhNhan(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var benhNhan = objectMapper.convertValue(map, BenhNhan.class);
            if(StringUtils.isEmpty(benhNhan.idDinhDanhChinh)) {
                benhNhan.idDinhDanhChinh = benhNhan.idhis;
            }
            
            if(StringUtils.isEmpty(benhNhan.idDinhDanhChinh)) {
                throw new RuntimeException("Empty idDinhDanhChinh");
            }
            
            benhNhan = benhNhanService.createOrUpdate(benhNhan, jsonSt);
            
            // Save to FhirDB
            saveToFhirDb(benhNhan);
            
            var result = mapOf(
                "success" , true,
                "benhNhan", benhNhan 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }        
    }
}
