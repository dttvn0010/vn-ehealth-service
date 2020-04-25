package vn.ehealth.emr.controller.term;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.dto.term.CodeSystemDTO;
import vn.ehealth.hl7.fhir.term.dao.impl.CodeSystemDao;
import vn.ehealth.hl7.fhir.term.entity.CodeSystemEntity;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/code_system")
public class CodeSystemController {

	@Autowired private CodeSystemDao codeSystemDao;
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
}
