package vn.ehealth.emr.dto.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.param.TokenParam;
import vn.ehealth.emr.model.dto.VaoKhoa;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.EncounterType;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.conceptHasCode;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.setReferenceResource;


@RestController
@RequestMapping("/api/vao_khoa")
public class VaoKhoaController {

private static Logger logger = LoggerFactory.getLogger(BenhNhanController.class);
    
    @Autowired private EncounterDao encounterDao;
        
    private boolean isVaoKhoa(Encounter obj) {
        if(obj != null && obj.hasType()) {
            for(var concept : obj.getType()) {
                boolean isVaoKhoa = conceptHasCode(concept, EncounterType.VAO_KHOA, 
                                                CodeSystemValue.ENCOUTER_TYPE);                
                if(isVaoKhoa) return true;
            }
        }
        return false;
    }
    
    @GetMapping("/get_by_id/{id}")
    public ResponseEntity<?> getById(@PathVariable String id,
						    		@RequestParam Optional<Boolean> includePatient,
									@RequestParam Optional<Boolean> includePractitioner,
									@RequestParam Optional<Boolean> includeServiceProvider) {
    	
        var obj = encounterDao.read(new IdType(id));
        if(isVaoKhoa(obj)) {
        	if(includeServiceProvider.orElse(false)) {
        		setReferenceResource(obj.getServiceProvider());
        	}
        	
        	if(includePatient.orElse(false)) {
        		setReferenceResource(obj.getSubject());
        	}
        	
        	if(includePractitioner.orElse(false) && obj.hasParticipant()) {
        		obj.getParticipant().forEach(x -> setReferenceResource(x.getIndividual()));
        	}
	        var dto = VaoKhoa.fromFhir(obj);
	        return ResponseEntity.ok(dto);
        }
        return new ResponseEntity<>("No vaoKhoa with id:" + id, HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping("/get_by_parent_id/{parentId}")
    public ResponseEntity<?> getByParentId(@PathVariable String parentId,
							    		@RequestParam Optional<Boolean> includePatient,
							    		@RequestParam Optional<Boolean> includePractitioner,
										@RequestParam Optional<Boolean> includeServiceProvider) {
    	
    	var params = mapOf(
    					"type", new TokenParam(CodeSystemValue.ENCOUTER_TYPE, EncounterType.VAO_KHOA),
    					"partOf", ResourceType.Encounter + "/" + parentId
    				);
    	
    	var includes = new HashSet<Include>();
        
        if(includeServiceProvider.orElse(false)) {
        	includes.add(new Include("Encounter:serviceProvider"));
        }
        
        if(includePatient.orElse(false)) {
        	includes.add(new Include("Encounter:subject"));
        }
        
        if(includePractitioner.orElse(false)) {
        	includes.add(new Include("Encounter:participant:individual"));
        }
        
        params.put("includes", includes);
        
        var lst = encounterDao.search(params);
        var result = transform(lst, x -> VaoKhoa.fromFhir((Encounter) x));
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody VaoKhoa dto) {
        try {
            var obj = VaoKhoa.toFhir(dto);            
            
            if(obj.hasId()) {
                obj = encounterDao.update(obj, obj.getIdElement());
            }else {
                obj = encounterDao.create(obj);
            }
            dto = VaoKhoa.fromFhir(obj);
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
