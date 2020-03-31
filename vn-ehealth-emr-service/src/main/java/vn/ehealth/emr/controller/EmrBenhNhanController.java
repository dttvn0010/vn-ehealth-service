package vn.ehealth.emr.controller;

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

import vn.ehealth.emr.model.EmrBenhNhan;
import vn.ehealth.emr.service.EmrBenhNhanService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;
import vn.ehealth.hl7.fhir.patient.dao.impl.PatientDao;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/benh_nhan")
public class EmrBenhNhanController {
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @Autowired private EmrBenhNhanService emrBenhNhanService;
    @Autowired private PatientDao patientDao;
    
    @GetMapping("/count_benhnhan")
    public long countBenhNhan(@RequestParam String keyword) {
        return emrBenhNhanService.countBenhNhan(keyword);
    }
    
    @GetMapping("/search_benhnhan")
    public ResponseEntity<?> searchBenhNhan(@RequestParam String keyword, 
                                            @RequestParam Optional<Integer> start, 
                                            @RequestParam Optional<Integer> count) {
        var emrBenhNhans = emrBenhNhanService.searchBenhNhan(keyword, start.orElse(-1), count.orElse(-1));
        return ResponseEntity.ok(emrBenhNhans);
    }
    
    @GetMapping("/get_benhnhan_by_id")
    public ResponseEntity<?> getBenhNhan(@RequestParam String id) {
        var benhNhan = emrBenhNhanService.getById(new ObjectId(id));
        return ResponseEntity.of(benhNhan);
    }
    
    private void saveToFhirDb(EmrBenhNhan emrBenhNhan) {
        try {
            if(emrBenhNhan == null) return;
            var patientDb = emrBenhNhan.getPatientInDB();
            var patient = emrBenhNhan.toFhir();
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
            var map = jsonParser.parseJson(jsonSt);
            var emrBenhNhan = objectMapper.convertValue(map, EmrBenhNhan.class);
            if(StringUtils.isEmpty(emrBenhNhan.iddinhdanhchinh)) {
                emrBenhNhan.iddinhdanhchinh = emrBenhNhan.idhis;
            }
            
            if(StringUtils.isEmpty(emrBenhNhan.iddinhdanhchinh)) {
                throw new RuntimeException("Empty iddinhdanhchinh");
            }
            
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            emrBenhNhan = emrBenhNhanService.createOrUpdate(userId, emrBenhNhan, jsonSt);
            
            // Save to FhirDB
            saveToFhirDb(emrBenhNhan);
            
            var result = mapOf(
                "success" , true,
                "emrBenhNhan", emrBenhNhan 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }        
    }
}
