package vn.ehealth.emr.controller.noitru;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.Encounter.EncounterStatus;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.dto.ehr.EncounterDTO;
import vn.ehealth.emr.dto.patient.PatientDTO;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.utils.MongoUtils;

@RestController
@RequestMapping("/api/noitru/danhsach_benhnhan")
public class DanhSachBenhNhanController {

@Autowired private EncounterDao encounterDao;
       
    private Criteria createCriteria(Optional<String> falcutyCode, Optional<String> chiefComplaintICD, Optional<String> keyword) {
    	var status = List.of(
    		EncounterStatus.ARRIVED,
    		EncounterStatus.TRIAGED,
    		EncounterStatus.INPROGRESS
    	);
    	
        var params =  mapOf3("status", "$in", status);
        
        if(falcutyCode.isPresent()) {
        	params.put("extension.value.display", mapOf("$regex", "(" + falcutyCode.get() + ")"));
        }
        
        if(chiefComplaintICD.isPresent()) {
        	params.put("diagnosis.condition.display", mapOf("$regex", "(" + chiefComplaintICD.get() + ")"));
        }
        
        if(keyword.isPresent()) {
        	params.put("subject.display", mapOf("$regex", keyword.get()));
        }
        
        return MongoUtils.createCriteria(params);
    }
    
    @GetMapping("/count")
    public long count(@RequestParam Optional<String> falcutyCode, Optional<String> chiefComplaintICD, Optional<String> keyword) {
    	
        var criteria = createCriteria(falcutyCode, chiefComplaintICD, keyword);
        return encounterDao.countResource(criteria);
    }
   
    
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam Optional<String> falcutyCode, 
    							Optional<String> chiefComplaintICD, 
    							Optional<String> keyword,
                                @RequestParam Optional<Integer> start,
                                @RequestParam Optional<Integer> count) {
        
        var criteria = createCriteria(falcutyCode, chiefComplaintICD, keyword);
        var encList = encounterDao.searchResource(criteria, start.orElse(-1), count.orElse(-1));

        encList.forEach(x -> DatabaseUtil.setReferenceResource(x.getSubject()));
        
        var encDtoList = transform(encList, EncounterDTO::fromFhir);
        
        for(var encDto : encDtoList) {
            if(encDto.subject == null) continue;
            
            var patient = (Patient) encDto.subject.resource;
            var patientDto = PatientDTO.fromFhir(patient);
            encDto.computes.put("patient", patientDto);
        }
        
        return ResponseEntity.ok(encDtoList);
    }
}
