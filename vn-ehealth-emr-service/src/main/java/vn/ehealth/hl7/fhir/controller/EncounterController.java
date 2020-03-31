package vn.ehealth.hl7.fhir.controller;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import java.util.Optional;

import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.ehr.entity.EncounterEntity;
import vn.ehealth.utils.MongoUtils;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/encounter")
public class EncounterController {

    @Autowired private EncounterDao encounterDao;
    
    @GetMapping("/get_hsba")
    public ResponseEntity<?> getHsbaEncounter(@RequestParam Optional<String> patientId,
                                @RequestParam Optional<Boolean> includePatient,
                                @RequestParam Optional<Boolean> includeServiceProvider,
                                @RequestParam Optional<Boolean> viewEntity,
                                @RequestParam Optional<Integer> start,
                                @RequestParam Optional<Integer> count) {
     
        var params =  mapOf("partOf.reference", (Object) null);
        patientId.ifPresent(x -> params.put("subject.reference", ResourceType.Patient + "/" + x));
        var criteria = MongoUtils.createCriteria(params);
        var lst = encounterDao.searchEntity(criteria, start.orElse(-1), count.orElse(-1));

        if(includePatient.orElse(false)) {
            lst.forEach(x -> MongoUtils.fetchReferenceResource(x.subject));
        }
        
        if(includeServiceProvider.orElse(false)) {
            lst.forEach(x -> MongoUtils.fetchReferenceResource(x.serviceProvider));
        }
        
        if(viewEntity.orElse(false)) {
            return ResponseEntity.ok(lst);
        }else {
            var lstDto = transform(lst, EncounterEntity::toDto);        
            return ResponseEntity.ok(lstDto);
        }       
    }
}
