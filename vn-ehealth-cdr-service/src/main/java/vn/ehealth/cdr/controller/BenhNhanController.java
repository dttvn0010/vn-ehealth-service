package vn.ehealth.cdr.controller;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.patient.dao.impl.PatientDao;
import vn.ehealth.cdr.utils.*;

@RestController
@RequestMapping("/api/benh_nhan")
public class BenhNhanController {
    
    private Logger log = LoggerFactory.getLogger(BenhNhanController.class);
    
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
    
    private void removeOldFhirData(@Nonnull BenhNhan benhNhan) {
        var patient = patientHelper.getPatientBySobhyt(benhNhan.sobhyt);
        if(patient != null) {
            patientDao.remove(patient.getIdElement());
        }
    }
    
    private void saveToFhirDb(BenhNhan benhNhan) {
        if(benhNhan == null) return;
        
        try {                        
            var patient = benhNhan.toFhir();
            
            if(patient != null) {
                removeOldFhirData(benhNhan);
                patientDao.create(patient);
            }
            
        }catch(Exception e) {
            log.error("Cannot save benhNhan id=" + benhNhan.getId() + " to fhir DB", e);
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
            
            var result = DataConvertUtil.mapOf(
                "success" , true,
                "benhNhan", benhNhan 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }        
    }
}
