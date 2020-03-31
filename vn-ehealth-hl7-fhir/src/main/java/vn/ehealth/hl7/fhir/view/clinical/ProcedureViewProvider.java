package vn.ehealth.hl7.fhir.view.clinical;

import java.util.HashMap;
import java.util.Optional;

import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.hl7.fhir.clinical.dao.impl.ProcedureDao;
import vn.ehealth.hl7.fhir.clinical.entity.ProcedureEntity;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.ehr.entity.EncounterEntity;
import vn.ehealth.utils.MongoUtils;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/procedure")
public class ProcedureViewProvider {

    @Autowired private ProcedureDao procedureDao; 
    
    @GetMapping("/get_list")
    public ResponseEntity<?> getProcedureList(@RequestParam Optional<String> patientId,
                            @RequestParam Optional<String> serviceTypeCode,
                            @RequestParam Optional<Boolean> includeEncounter,
                            @RequestParam Optional<Boolean> includeServiceProvider,
                            @RequestParam Optional<Boolean> viewEntity,
                            @RequestParam Optional<Integer> start,
                            @RequestParam Optional<Integer> count) {
        
        var params =  new HashMap<String, Object>();
        patientId.ifPresent(x -> params.put("subject.reference", ResourceType.Patient + "/" + x));
        
        if(serviceTypeCode.isPresent()) {
            params.put("category.coding.code", serviceTypeCode.get());
            params.put("category.coding.system", CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
        };
        
        var criteria = MongoUtils.createCriteria(params);
        var lst = procedureDao.searchEntity(criteria, start.orElse(-1), count.orElse(-1));
        
        lst.forEach(x -> MongoUtils.fetchReferenceResource(x.basedOn));
        lst.forEach(x -> MongoUtils.fetchReferenceResource(x.report));
        
        if(includeEncounter.orElse(false)) {
            lst.forEach(x -> MongoUtils.setReferenceResource(x.encounter));
            
            if(includeServiceProvider.orElse(false)) {
                for(var ent : lst) {
                    var encounter = (EncounterEntity) ent.encounter.resource;
                    if(encounter != null) {
                        MongoUtils.setReferenceResource(encounter.serviceProvider);
                    }
                }
            }
        }
        
        if(viewEntity.orElse(false)) {
            return ResponseEntity.ok(lst);
        }else {        
            var lstDto = transform(lst, ProcedureEntity::toDto);
            return ResponseEntity.ok(lstDto);
        }
    }    
}
