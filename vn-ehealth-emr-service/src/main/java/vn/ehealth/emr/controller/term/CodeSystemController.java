package vn.ehealth.emr.controller.term;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.dto.term.CodeSystemDTO;
import vn.ehealth.emr.dto.term.ConceptDTO;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.term.dao.impl.CodeSystemDao;
import vn.ehealth.hl7.fhir.term.entity.CodeSystemEntity;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/code_system")
public class CodeSystemController {

	@Autowired private CodeSystemDao codeSystemDao;
	
	private Query createQuery(Optional<String> keyword) {
	    var params = mapOf("status", (Object) "active");
	    
	    keyword.ifPresent(x -> {
	        params.putAll(mapOf3("name", "$regex", x));
	    });
	    
	    return MongoUtils.createQuery(params);
	}
	
	@GetMapping("/count")
	public int count(@RequestParam Optional<String> keyword) {
		var criteria = createQuery(keyword);
		return codeSystemDao.countResource(criteria);
	}
	
	@GetMapping("/list")
	public ResponseEntity<?> list(@RequestParam Optional<String> keyword,
	                            @RequestParam Optional<Boolean> viewEntity,	        
								@RequestParam Optional<Integer> start,
                                @RequestParam Optional<Integer> count) {
		
	    var criteria = createQuery(keyword);
		var lst = codeSystemDao.searchResource(criteria);
		
		if(viewEntity.orElse(false)) {
			var lstEnt = transform(lst, x -> fhirToEntity(x, CodeSystemEntity.class));
			return ResponseEntity.ok(lstEnt);
		}else {
			var lstDto = transform(lst, CodeSystemDTO::fromFhir);
			return ResponseEntity.ok(lstDto);
		}
	}
	
	@GetMapping("/find_match")
	public ResponseEntity<?> findMatch(@RequestParam Optional<String> codeSystemId,
	        @RequestParam Optional<String> codeSystemUrl,
	        String keyword, @RequestParam Optional<Integer> limit) {
	    try {
	        String system = codeSystemUrl.orElse("");
	        if(StringUtils.isBlank(system)) {
	            var codeSystem = codeSystemDao.read(FhirUtil.createIdType(codeSystemId.orElseThrow()));
	            system = codeSystem.getUrl();
	        }
    	    
    	    Parameters params = new Parameters();
    	    
    	    var systemParam = params.addParameter();
    	    systemParam.setName("system").setValue(new UriType(system));
    	    
    	    var exactParam = params.addParameter();
    	    exactParam.setName("exact").setValue(new BooleanType(false));
    	    
    	    var propParam = params.addParameter();
    	    propParam.setName("property");
    	    
    	    var codePart = propParam.addPart();
    	    codePart.setName("code").setValue(new CodeType("slug"));
    	    
    	    var valuePart = propParam.addPart();
    	    valuePart.setName("value").setValue(new StringType(keyword));
    	    
    	    var result = codeSystemDao.findMatches(params);
    	    
    	    var match = result.getParameter()
    	                      .stream()
    	                      .filter(x -> "match".equals(x.getName()))
    	                      .findFirst()
    	                      .orElse(null);
    	    	    
    	    List<ConceptDTO> conceptDTOList = new ArrayList<>();
    	    if(match != null) {
    	        for(var part : match.getPart()) {
    	            var code = (Coding) part.getValue();
    	            conceptDTOList.add(ConceptDTO.fromCode(code));
    	            
    	            if(limit.isPresent() && conceptDTOList.size() >= limit.get()) {
    	                break;
    	            }
    	        }
    	    }
    	    
    	    return ResponseEntity.ok(conceptDTOList);	
	    }catch(Exception e) {
	        return ResponseUtil.errorResponse(e);
	    }
	}
	
}
