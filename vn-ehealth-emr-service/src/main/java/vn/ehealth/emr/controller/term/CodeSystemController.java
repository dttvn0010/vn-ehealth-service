package vn.ehealth.emr.controller.term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.PropertyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.emr.dto.term.CodeSystemDTO;
import vn.ehealth.emr.dto.term.ConceptDTO;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.term.dao.impl.CodeSystemDao;
import vn.ehealth.hl7.fhir.term.dao.impl.ConceptDao;
import vn.ehealth.hl7.fhir.term.entity.CodeSystemEntity;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/code_system")
public class CodeSystemController {

	private Logger log = LoggerFactory.getLogger(CodeSystemController.class);
	@Autowired private CodeSystemDao codeSystemDao;
	@Autowired private ConceptDao conceptDao;
	
	private ObjectMapper objectMapper = EmrUtils.createObjectMapper();

	@GetMapping("/count")
	public int count() {
		var criteria = MongoUtils.createCriteria(new HashMap<String, Object>());
		return codeSystemDao.countResource(criteria);
	}
	
	@GetMapping("/list")
	public ResponseEntity<?> list(@RequestParam Optional<Boolean> viewEntity,
								@RequestParam Optional<Integer> start,
                                @RequestParam Optional<Integer> count) {
		var criteria = MongoUtils.createCriteria(new HashMap<String, Object>());
		var lst = codeSystemDao.searchResource(criteria);
		
		if(viewEntity.orElse(false)) {
			var lstEnt = transform(lst, x -> fhirToEntity(x, CodeSystemEntity.class));
			return ResponseEntity.ok(lstEnt);
		}else {
			var lstDto = transform(lst, CodeSystemDTO::fromFhir);
			return ResponseEntity.ok(lstDto);
		}
	}
	
	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestBody CodeSystemDTO codeSystem) {
		try {
			var concepts = codeSystem.concept != null? codeSystem.concept : new ArrayList<ConceptDTO>();
			var codeSystemEnt = objectMapper.convertValue(codeSystem, CodeSystemEntity.class);
			var codeSystemObj = entityToFhir(codeSystemEnt, CodeSystem.class);	
			
			var types = new HashMap<String, PropertyType>();
			for(var property : codeSystemObj.getProperty()) {
				types.put(property.getCode(), property.getType());
			}
			
			for(var concept : concepts) {
				if(concept.property != null) {
					concept.property.forEach(x -> x.type = types.get(x.code));
				}
			}
			
			var conceptEntList = transform(concepts, ConceptDTO::toEntity);
								
			codeSystemObj = codeSystemDao.create(codeSystemObj);
			for(var conceptEnt : conceptEntList) {
				conceptEnt.codeSystemId = codeSystemObj.getId();
				conceptEnt.version = codeSystemObj.getVersion();
				conceptEnt = conceptDao.create(conceptEnt);
				log.info("Create concept with id:" + conceptEnt._fhirId);
			}			
			
			return ResponseEntity.ok(mapOf("success", true));
			
		}catch(Exception e) {
			return ResponseUtil.errorResponse(e);
		}
	}	
	
}
