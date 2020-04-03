package vn.ehealth.emr.controller.patient;

import java.util.HashMap;
import java.util.Optional;

import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.dto.patient.PatientDTO;
import vn.ehealth.hl7.fhir.patient.dao.impl.PatientDao;
import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/patient")
public class PatientController {
    
    @Autowired private PatientDao patientDao;
    
    private Criteria createCritera(Optional<String> keyword) {
        var params =  new HashMap<String, Object>();

        keyword.ifPresent(x -> {
            params.put("$or", listOf(
                            mapOf3("name.text", "$regex", x),
                            mapOf3("name.family", "$regex", x),
                            mapOf3("name.given", "$regex", x),
                            mapOf3("telecom.value", "$regex", x)
                    ));
        });
        
        return MongoUtils.createCriteria(params);
    }
    
    @GetMapping("/count")
    public long count(@RequestParam Optional<String> keyword) {
        var criteria = createCritera(keyword);
        return patientDao.countResource(criteria);
    }
    
    @GetMapping("/list")
    public ResponseEntity<?> list(@RequestParam Optional<String> keyword,
                                    @RequestParam Optional<Boolean> viewEntity,
                                    @RequestParam Optional<Integer> start,
                                    @RequestParam Optional<Integer> count) {
      
        var criteria = createCritera(keyword);
        var lst = patientDao.searchResource(criteria, start.orElse(-1), count.orElse(-1));
        
        if(viewEntity.orElse(false)) {
            var lstEnt = transform(lst, x -> fhirToEntity(x, PatientEntity.class));
            return ResponseEntity.ok(lstEnt);
        }else {
            var lstDto = transform(lst, PatientDTO::fromFhir);
            return ResponseEntity.ok(lstDto);
        }        
    }
  
    @GetMapping("/get_by_id/{id}")
    public ResponseEntity<?> getById(@PathVariable String id, @RequestParam Optional<Boolean> viewEntity) {
        
        var patient = patientDao.read(new IdType(id));
        
        if(patient == null) {
            return new ResponseEntity<>("No patient found", HttpStatus.NOT_FOUND);
        }
        
        if(viewEntity.orElse(false)) {
            var ent = fhirToEntity(patient, PatientEntity.class);
            return ResponseEntity.ok(ent);
        }else {
            var dto = PatientDTO.fromFhir(patient);
            return ResponseEntity.ok(dto);
        }
    }
}
