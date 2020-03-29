package vn.ehealth.emr.dto.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.param.TokenParam;
import vn.ehealth.emr.model.dto.DotKhamBenh;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.EncounterType;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;


@RestController
@RequestMapping("/api/dot_kham_benh")
public class DotKhamBenhController {

    private static Logger logger = LoggerFactory.getLogger(BenhNhanController.class);
    
    @Autowired private EncounterDao encounterDao;
        
    private boolean isDotKhamBenh(Encounter obj) {
        if(obj != null && obj.hasType()) {
            for(var concept : obj.getType()) {
                boolean isDotKhamBenh = conceptHasCode(concept, EncounterType.DOT_KHAM, 
                                                CodeSystemValue.ENCOUTER_TYPE);                
                if(isDotKhamBenh) return true;
            }
        }
        return false;
    }
    
    @GetMapping("/get_by_id/{id}")
    public ResponseEntity<?> getById(@PathVariable String id,
    								@RequestParam Optional<Boolean> includePatient,
    								@RequestParam Optional<Boolean> includeServiceProvider) {
        var obj = encounterDao.read(new IdType(id));
        if(isDotKhamBenh(obj)) {
        	if(includeServiceProvider.orElse(false)) {
        		setReferenceResource(obj.getServiceProvider());
        	}
        	
        	if(includePatient.orElse(false)) {
        		setReferenceResource(obj.getSubject());
        	}
	        var dto = DotKhamBenh.fromFhir(obj);
	        return ResponseEntity.ok(dto);
        }
        return new ResponseEntity<>("No dotkhambenh with id:" + id, HttpStatus.BAD_REQUEST);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
    	try {
    		var obj = encounterDao.read(new IdType(id));
    		if(isDotKhamBenh(obj)) {
	    		encounterDao.remove(createIdType(id));
	    		return ResponseEntity.ok(mapOf("success", true));
    		}else {
    			return new ResponseEntity<>("No dotkhambenh with id:" + id, HttpStatus.BAD_REQUEST);
    		}
    	}catch(Exception e) {
    		var result = mapOf("success", false, "error", e.getMessage());
    		return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    	}    	
    }
    
    @GetMapping("/count")
    public long count(@RequestParam Optional<String> patientId) {
        var params = new HashMap<String, Object>();
        params.put("type", new TokenParam(CodeSystemValue.ENCOUTER_TYPE, EncounterType.DOT_KHAM));
        patientId.ifPresent(x -> params.put("subject", ResourceType.Patient + "/" +  x));
    	return encounterDao.count(params);
    }
    
    @GetMapping("/get_list")
    public ResponseEntity<?> getList(@RequestParam Optional<String> patientId, 
    									@RequestParam Optional<Boolean> includePatient,
    									@RequestParam Optional<Boolean> includeServiceProvider) {
    	
        var params = new HashMap<String, Object>();
        params.put("type", new TokenParam(CodeSystemValue.ENCOUTER_TYPE, EncounterType.DOT_KHAM));
        patientId.ifPresent(x -> params.put("subject", ResourceType.Patient + "/" + x));
        
        var includes = new HashSet<Include>();
       
        if(includeServiceProvider.orElse(false)) {
        	includes.add(new Include("Encounter:serviceProvider"));
        }
        
        if(includePatient.orElse(false)) {
        	includes.add(new Include("Encounter:subject"));
        }
        
        params.put("includes", includes);
        
    	var lst = encounterDao.search(params);
    	
    	var result = transform(lst, x -> DotKhamBenh.fromFhir((Encounter)x));
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody DotKhamBenh dto) {
        try {
            var obj = DotKhamBenh.toFhir(dto);            
            
            if(obj.hasId()) {
                obj = encounterDao.update(obj, obj.getIdElement());
            }else {
                obj = encounterDao.create(obj);
            }
            dto = DotKhamBenh.fromFhir(obj);
            var result = mapOf("success", true, "dto", dto);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save entity: ", e);
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = mapOf("success", false, "error", error);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
