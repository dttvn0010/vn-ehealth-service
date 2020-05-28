package vn.ehealth.emr.controller.clinical;

import java.util.HashMap;
import java.util.Optional;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.dto.clinical.ProcedureDTO;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ProcedureDao;
import vn.ehealth.hl7.fhir.clinical.entity.ProcedureEntity;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.utils.MongoUtils;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/procedure")
public class ProcedureController {

    @Autowired private ProcedureDao procedureDao; 
    
    private Query createQuery(Optional<String> patientId, Optional<String> serviceTypeCode) {
        var params =  new HashMap<String, Object>();
        patientId.ifPresent(x -> params.put("subject.reference", ResourceType.Patient + "/" + x));
        
        if(serviceTypeCode.isPresent()) {
            params.put("category.coding.code", serviceTypeCode.get());
            params.put("category.coding.system", CodeSystemValue.DIAGNOSTIC_SERVICE_SECTIONS);
        };
        
        return MongoUtils.createQuery(params);
    }
    
    @GetMapping("/count")
    public long count(@RequestParam Optional<String> patientId,
                            @RequestParam Optional<String> serviceTypeCode) {
        
        var criteria = createQuery(patientId, serviceTypeCode);
        return procedureDao.countResource(criteria);
    }
    
    @GetMapping("/list")
    public ResponseEntity<?> getList(@RequestParam Optional<String> patientId,
                            @RequestParam Optional<String> serviceTypeCode,
                            @RequestParam Optional<Boolean> includeEncounter,
                            @RequestParam Optional<Boolean> includeServiceProvider,
                            @RequestParam Optional<Boolean> viewEntity,
                            @RequestParam Optional<Integer> start,
                            @RequestParam Optional<Integer> count) {
       
        var criteria = createQuery(patientId, serviceTypeCode);
        var lst = procedureDao.searchResource(criteria, start.orElse(-1), count.orElse(-1));
        
        lst.forEach(x -> DatabaseUtil.setReferenceResource(x.getBasedOn()));
        lst.forEach(x -> DatabaseUtil.setReferenceResource(x.getReport()));
        
        if(includeEncounter.orElse(false)) {
            lst.forEach(x -> DatabaseUtil.setReferenceResource(x.getEncounter()));
            
            if(includeServiceProvider.orElse(false)) {
                for(var procedure : lst) {
                    var encounter = (Encounter) procedure.getEncounter().getResource();
                    if(encounter != null) {
                        DatabaseUtil.setReferenceResource(encounter.getServiceProvider());
                    }
                }
            }
        }
        
        if(viewEntity.orElse(false)) {
            var lstEnt = transform(lst, x -> fhirToEntity(x, ProcedureEntity.class));
            return ResponseEntity.ok(lstEnt);
        }else {        
            var lstDto = transform(lst, ProcedureDTO::fromFhir);
            return ResponseEntity.ok(lstDto);
        }
    }    
}