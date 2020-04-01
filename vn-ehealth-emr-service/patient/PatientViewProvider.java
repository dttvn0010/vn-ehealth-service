package vn.ehealth.hl7.fhir.view.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import vn.ehealth.hl7.fhir.core.view.EntityView;
import vn.ehealth.hl7.fhir.patient.dao.impl.PatientDao;
import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/patient")
public class PatientViewProvider {

    @Autowired private PatientDao patientDao;
    
    @JsonView(EntityView.class)
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam Optional<String> keyword,
                                    @RequestParam Optional<Boolean> viewEntity,
                                    @RequestParam Optional<Integer> start,
                                    @RequestParam Optional<Integer> count) {
        
        var params =  new HashMap<String, Object>();
        
        keyword.ifPresent(x -> {
            params.put("$or", listOf(
                            mapOf3("name.text", "$regex", x),
                            mapOf3("name.family", "$regex", x),
                            mapOf3("name.given", "$regex", x),
                            mapOf3("telecom.value", "$regex", x)
                    ));
        });
        
        var criteria = MongoUtils.createCriteria(params);
        var lst = patientDao.searchEntity(criteria, start.orElse(-1), count.orElse(-1));
        
        if(viewEntity.orElse(false)) {
            return ResponseEntity.ok(lst);
        }else {
            var lstDto = transform(lst, PatientEntity::toDto);
            return ResponseEntity.ok(lstDto);
        }        
    }
}
