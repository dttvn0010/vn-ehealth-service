package vn.ehealth.cdr.controller;

import java.util.Optional;

import org.bson.types.ObjectId;
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

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.cdr.model.BenhNhan;
import vn.ehealth.cdr.service.BenhNhanService;
import vn.ehealth.cdr.utils.CDRUtils;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.cdr.validate.JsonParser;
import vn.ehealth.hl7.fhir.patient.dao.impl.PatientDao;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/benh_nhan")
public class BenhNhanController {
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @Autowired private BenhNhanService benhNhanService;
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
    
    private void saveToFhirDb(BenhNhan benhNhan) {
        try {
            if(benhNhan == null) return;
            var patientDb = benhNhan.getPatientInDB();
            var patient = benhNhan.toFhir();
            if(patientDb != null) {
                patientDao.update(patient, patientDb.getIdElement());
            }else {
                patientDao.create(patient);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/create_or_update_benhnhan")
    public ResponseEntity<?> createOrUpdateBenhNhan(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = jsonParser.parseJson(jsonSt);
            var benhNhan = objectMapper.convertValue(map, BenhNhan.class);
            if(StringUtils.isEmpty(benhNhan.idDinhDanhChinh)) {
                benhNhan.idDinhDanhChinh = benhNhan.idhis;
            }
            
            if(StringUtils.isEmpty(benhNhan.idDinhDanhChinh)) {
                throw new RuntimeException("Empty idDinhDanhChinh");
            }
            
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            benhNhan = benhNhanService.createOrUpdate(userId, benhNhan, jsonSt);
            
            // Save to FhirDB
            saveToFhirDb(benhNhan);
            
            var result = mapOf(
                "success" , true,
                "benhNhan", benhNhan 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }        
    }
}
