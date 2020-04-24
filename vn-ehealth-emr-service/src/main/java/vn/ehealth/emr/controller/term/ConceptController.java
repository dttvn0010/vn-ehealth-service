package vn.ehealth.emr.controller.term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.dto.term.ConceptDTO;
import vn.ehealth.hl7.fhir.term.dao.impl.CodeSystemDao;
import vn.ehealth.hl7.fhir.term.dao.impl.ConceptDao;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/concept")
public class ConceptController {

	@Autowired private CodeSystemDao codeSystemDao;
	@Autowired private ConceptDao conceptDao;
	
	private Criteria createCritera(String codeSystemUrl, Optional<String> keyword) {
		var params = new HashMap<String, Object>();
		
		var codeSytem = codeSystemDao.getByUrl(codeSystemUrl);
		if(codeSytem == null) {
			return null;
		}
		params.put("codeSystemId", codeSytem.getId());
		
		keyword.ifPresent(x -> {
            params.put("$or", 
		            		listOf(
		                        mapOf3("code", "$regex", x),
		                        mapOf3("display", "$regex", x)
		                    )
		               );
        	});
		
		return MongoUtils.createCriteria(params);
	}
	
	@GetMapping("/count")
	public int count(String codeSystemUrl, Optional<String> keyword) {
		
		var criteria = createCritera(codeSystemUrl, keyword);
		if(criteria != null) {
			return conceptDao.countEntity(criteria);
		}
		return 0;
	}
	
	@GetMapping("/list")
	public ResponseEntity<?> list(String codeSystemUrl,
								@RequestParam Optional<String> keyword,								
								@RequestParam Optional<Boolean> viewEntity,
								@RequestParam Optional<Integer> start,
					            @RequestParam Optional<Integer> count) {
		
		var criteria = createCritera(codeSystemUrl, keyword);
		if(criteria != null) {
			var lstEnt = conceptDao.searchEntity(criteria);
			
			if(viewEntity.orElse(false)) {
				return ResponseEntity.ok(lstEnt);
			}else {
				var lstDto = transform(lstEnt, ConceptDTO::fromEntity);
				return ResponseEntity.ok(lstDto);
			}
		}
		
		return ResponseEntity.ok(new ArrayList<>());
	}
}
