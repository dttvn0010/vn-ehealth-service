package vn.ehealth.emr.controller.term;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.dto.base.CodingDTO;
import vn.ehealth.emr.dto.term.CodeSystemDTO;
import vn.ehealth.emr.dto.term.ConceptDTO;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.term.dao.impl.CodeSystemDao;
import vn.ehealth.hl7.fhir.term.dao.impl.ConceptDao;
import vn.ehealth.hl7.fhir.term.entity.CodeSystemEntity;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/code_system")
public class CodeSystemController {

	@Autowired private CodeSystemDao codeSystemDao;
	@Autowired private ConceptDao conceptDao;
	
	private Query createQuery(Optional<String> keyword) {
	    var params = mapOf("status", (Object) "active");
	    
	    keyword.ifPresent(x -> {
	        params.putAll(mapOf3("name", "$regex", x));
	    });
	    
	    return MongoUtils.createQuery(params);
	}
	
	@GetMapping("/get_url_by_id/{id}")
	public ResponseEntity<?> getUrlById(@PathVariable String id) {
		var codeSystem = codeSystemDao.read(FhirUtil.createIdType(id));
		var result = mapOf("url", codeSystem.getUrl());
		return ResponseEntity.ok(result);
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
		var lst = codeSystemDao.searchResource(criteria, start.orElse(-1), count.orElse(-1));
		
		if(viewEntity.orElse(false)) {
			var lstEnt = transform(lst, x -> fhirToEntity(x, CodeSystemEntity.class));
			return ResponseEntity.ok(lstEnt);
		}else {
			var lstDto = transform(lst, CodeSystemDTO::fromFhir);
			return ResponseEntity.ok(lstDto);
		}
	}
	
	@GetMapping("/find_dvkt_items")
    public ResponseEntity<?> findDVKTItems(@RequestParam Optional<String> maLoaiDVKT,
            @RequestParam Optional<String> maNhomDVKT,            
            @RequestParam Optional<String> keyword, 
            @RequestParam Optional<Integer> offset,
            @RequestParam Optional<Integer> limit) {
        try {
            String system = CodeSystemValue.DICH_VU_KY_THUAT;
            
            Parameters params = new Parameters();
            
            var systemParam = params.addParameter();
            systemParam.setName("system").setValue(new UriType(system));
            
            var exactParam = params.addParameter();
            exactParam.setName("exact").setValue(new BooleanType(false));
            
            if(keyword.isPresent()) {
                var propParam = params.addParameter();
                propParam.setName("property");
                
                var codePart = propParam.addPart();
                codePart.setName("code").setValue(new CodeType("display"));
                                
                var valuePart = propParam.addPart();
                valuePart.setName("value").setValue(new StringType(keyword.get()));
            }
            
            if(maLoaiDVKT.isPresent()) {
                var propParam = params.addParameter();
                propParam.setName("property");                
            
                var codePart = propParam.addPart();
                codePart.setName("code").setValue(new CodeType("maLoai"));
                
                var valuePart = propParam.addPart();
                valuePart.setName("value").setValue(new StringType("(" + maLoaiDVKT.get()+ ")"));
            }
            
            if(maNhomDVKT.isPresent()) {
                var propParam = params.addParameter();
                propParam.setName("property");
                
                var codePart = propParam.addPart();
                codePart.setName("code").setValue(new CodeType("maNhom"));
                
                var valuePart = propParam.addPart();
                valuePart.setName("value").setValue(new StringType("(" + maNhomDVKT.get() + ")"));
            }
            
            var result = codeSystemDao.findMatches(params);
            
            var match = result.getParameter()
                              .stream()
                              .filter(x -> "match".equals(x.getName()))
                              .findFirst()
                              .orElse(null);
                    
            List<ConceptDTO> conceptDTOList = new ArrayList<>();
            if(match != null) {
                int skip = 0;
                for(var part : match.getPart()) {
                    if(offset.isPresent() && skip < offset.get()) {
                        skip += 1;
                        continue;
                    }
                    var code = (Coding) part.getValue();
                    var conceptDTO = ConceptDTO.fromCode(code);
                    
                    conceptDTO.property = new ArrayList<>();
                    for(var prop : part.getPart()) {
                        if("display".equals(prop.getName())) continue;
                        
                        var propDTO = new ConceptDTO.ConceptPropertyDTO();
                        propDTO.code = prop.getName();
                        
                        if(prop.getValue() instanceof IntegerType) {
                            propDTO.value = ((IntegerType) prop.getValue()).getValue();
                        }else if(prop.getValue() instanceof Coding) {
                            propDTO.value = CodingDTO.fromCoding((Coding) prop.getValue());
                        } else {
                            propDTO.value = prop.getValue().primitiveValue();
                        }
                        conceptDTO.property.add(propDTO);
                    }
                    
                    conceptDTOList.add(conceptDTO);
                    
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
	
	@GetMapping("/count_match")
    public long countMatch(@RequestParam Optional<String> codeSystemId,
            @RequestParam Optional<String> codeSystemUrl,
            @RequestParam Optional<String> keyword,
            @RequestParam Optional<String> advanceSearch) {
        
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
        
        if(keyword.isPresent()) {
            var propParam = params.addParameter();
            propParam.setName("property");
            
            var codePart = propParam.addPart();
            codePart.setName("code").setValue(new CodeType("display"));
            
            var valuePart = propParam.addPart();
            valuePart.setName("value").setValue(new StringType(keyword.get()));
        }
        
        if(advanceSearch.isPresent()) {
            for(String codeValuePair : advanceSearch.get().split(",")) {
                String[] arr = codeValuePair.split("|");
                String code = arr[0];
                String value = arr[1];
                
                if(arr.length == 2) {
                    var propParam = params.addParameter();
                    propParam.setName("property");
                    
                    var codePart = propParam.addPart();
                    codePart.setName("code").setValue(new CodeType(code));
                    
                    var valuePart = propParam.addPart();
                    valuePart.setName("value").setValue(new StringType(value));
                }
            }
        }
        
        
        var result = codeSystemDao.findMatches(params);
        
        var match = result.getParameter()
                          .stream()
                          .filter(x -> "match".equals(x.getName()))
                          .findFirst()
                          .orElse(null);
        if(match != null) {
            return match.getPart().size();
        }
        
        return 0;
        
    }
	
	@GetMapping("/find_match")
	public ResponseEntity<?> findMatch(@RequestParam Optional<String> codeSystemId,
	        @RequestParam Optional<String> codeSystemUrl,
	        @RequestParam Optional<String> keyword,
	        @RequestParam Optional<String> advanceSearch,
	        @RequestParam Optional<Integer> offset,
	        @RequestParam Optional<Integer> limit) {
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
    	    
    	    if(keyword.isPresent()) {
        	    var propParam = params.addParameter();
        	    propParam.setName("property");
        	    
        	    var codePart = propParam.addPart();
        	    codePart.setName("code").setValue(new CodeType("display"));
        	    
        	    var valuePart = propParam.addPart();
        	    valuePart.setName("value").setValue(new StringType(keyword.get()));
    	    }
    	    
    	    if(advanceSearch.isPresent()) {
    	        for(String codeValuePair : advanceSearch.get().split(",")) {
    	            String[] arr = codeValuePair.split("\\|");
    	            String code = arr[0];
    	            String value = arr[1];
    	            
    	            if(arr.length == 2) {
    	                var propParam = params.addParameter();
    	                propParam.setName("property");
    	                
    	                var codePart = propParam.addPart();
    	                codePart.setName("code").setValue(new CodeType(code));
    	                
    	                var valuePart = propParam.addPart();
    	                valuePart.setName("value").setValue(new StringType(value));
    	            }
    	        }
    	    }
    	    
    	    var result = codeSystemDao.findMatches(params);
    	    
    	    var match = result.getParameter()
    	                      .stream()
    	                      .filter(x -> "match".equals(x.getName()))
    	                      .findFirst()
    	                      .orElse(null);
    	    	    
    	    List<ConceptDTO> conceptDTOList = new ArrayList<>();
    	    if(match != null) {
    	        int skip = 0;
    	        for(var part : match.getPart()) {
    	            if(offset.isPresent() && skip < offset.get()) {
    	                skip += 1;
    	                continue;
    	            }
    	            var code = (Coding) part.getValue();
    	            var conceptDTO = ConceptDTO.fromCode(code);
    	            
    	            conceptDTO.property = new ArrayList<>();
    	            for(var prop : part.getPart()) {
    	                if("display".equals(prop.getName())) continue;
    	                
    	                var propDTO = new ConceptDTO.ConceptPropertyDTO();
    	                propDTO.code = prop.getName();
    	                
    	                if(prop.getValue() instanceof IntegerType) {
                            propDTO.value = ((IntegerType) prop.getValue()).getValue();
                        }else if(prop.getValue() instanceof Coding) {
                            propDTO.value = CodingDTO.fromCoding((Coding) prop.getValue());
                        } else if (prop.getValue() != null) {
                            propDTO.value = prop.getValue().primitiveValue();
                        }
    	                conceptDTO.property.add(propDTO);
    	            }
    	            
    	            conceptDTOList.add(conceptDTO);
    	            
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
	
	@GetMapping("/get_items")
	public ResponseEntity<?> getItems(@RequestParam String codeSystemUrl) {
		var codeSystem = codeSystemDao.getByUrl(codeSystemUrl);
		var result = new ArrayList<>();
		if(codeSystem != null) {
			var concepts = conceptDao.getByCodeSystem(codeSystem.getId());
			for(var concept : concepts) {				
				var item = mapOf("code", concept.code, "display", concept.display);				
				result.add(item);
			}
		}
		
		return ResponseEntity.ok(result);		
	}	
	
}
