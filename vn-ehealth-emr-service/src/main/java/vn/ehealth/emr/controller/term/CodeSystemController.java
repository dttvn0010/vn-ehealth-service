package vn.ehealth.emr.controller.term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
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
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.DateUtil;
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
	
	@GetMapping("/get_by_id/{id}")
	public ResponseEntity<?> getUrlById(@PathVariable String id) {
		var codeSystem = codeSystemDao._read(FhirUtil.createIdType(id), false);
		System.out.print(codeSystem);
		if(codeSystem != null) {
		    return ResponseEntity.ok(CodeSystemDTO.fromFhir(codeSystem));
		}else {
		    return ResponseEntity.ok(new HashMap<>());
		}
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
            String advanceSearch = "";
            
            if(maLoaiDVKT.isPresent()) {
                advanceSearch += "maLoai|" + "(" + maLoaiDVKT.get()+ ")";
            }
            if(maNhomDVKT.isPresent()) {
                advanceSearch += ",maNhom|" + "(" + maNhomDVKT.get()+ ")";
            }
            
            var parameters = createFindMatchParameters(CodeSystemValue.DICH_VU_KY_THUAT, keyword.orElse(""), advanceSearch);
            
            parameters.addParameter()
                       .setName(ConstantKeys.SP_PAGE)
                       .setValue(new IntegerType(offset.orElse(0)/ConstantKeys.DEFAULT_PAGE_SIZE));
            
            parameters.addParameter()
                        .setName(ConstantKeys.SP_COUNT)
                        .setValue(new IntegerType(limit.orElse(ConstantKeys.DEFAULT_PAGE_SIZE)));
            
            var result = codeSystemDao.findMatches(parameters);
            
            var match = result.getParameter()
                              .stream()
                              .filter(x -> "match".equals(x.getName()))
                              .findFirst()
                              .orElse(null);
                    
            List<ConceptDTO> conceptDTOList = new ArrayList<>();
            if(match != null) {
                for(var part : match.getPart()) {
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
                }
            }
            
            return ResponseEntity.ok(conceptDTOList);   
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
	
	private Integer parseInt(String st) {
	    try {
	        return Integer.valueOf(st);
	    }catch(NumberFormatException e) {
	        return null;
	    }
	}
	
	private Parameters createFindMatchParameters(String codeSystemUrl, String keyword, String advanceSearch) {
	    
	    var parameters = new Parameters();
	    
	    var codeSystem = codeSystemDao.getByUrl(codeSystemUrl);
	    
	    if(codeSystem == null) {
	        return parameters;
	    }
	    
	    var typesMap = new HashMap<String, String>();
        for(var prop : codeSystem.getProperty()) {
            var propCode = prop.getCode();
            var type = prop.getType().getDisplay();
            typesMap.put(propCode, type);            
        }
        
        var systemParam = parameters.addParameter();
        systemParam.setName("system").setValue(new UriType(codeSystemUrl));
        
        var exactParam = parameters.addParameter();
        exactParam.setName("exact").setValue(new BooleanType(false));
        
        if(!StringUtils.isBlank(keyword)) {
            var propParam = parameters.addParameter();
            propParam.setName("property");
            
            var codePart = propParam.addPart();
            codePart.setName("code").setValue(new CodeType("display"));
            
            var valuePart = propParam.addPart();
            valuePart.setName("value").setValue(new StringType(keyword));
        }
        
        if(!StringUtils.isBlank(advanceSearch)) {
            for(String codeValuePair : advanceSearch.split(",")) {
                String[] arr = codeValuePair.split("\\|");
                                    
                if(arr.length == 2) {                  
                    String code = arr[0];
                    String value = arr[1];
                    var type = typesMap.get(code);
                    
                    if("dateTime".equals(type) && value.contains("~")) {
                        arr = value.split("~");
                        
                        if(arr.length == 2) {
                            var fromDate = DateUtil.parseStringToDate(arr[0], "dd/MM/yyyy");
                            var toDate = DateUtil.parseStringToDate(arr[1], "dd/MM/yyyy");
                            toDate.setTime(toDate.getTime() + 24 * 3600 *1000 - 1);
                            
                            var fromParam = parameters.addParameter().setName("property");                                
                            fromParam.addPart().setName("code").setValue(new CodeType(code + "__from"));
                            fromParam.addPart().setName("value").setValue(new DateTimeType(fromDate));                                          
                            
                            var toParam = parameters.addParameter().setName("property");                                
                            toParam.addPart().setName("code").setValue(new CodeType(code + "__to"));
                            toParam.addPart().setName("value").setValue(new DateTimeType(toDate));
                        }
                    }else {
                        var propParam = parameters.addParameter();
                        propParam.setName("property");
                        
                        var codePart = propParam.addPart();
                        codePart.setName("code").setValue(new CodeType(code));
                        
                        var valuePart = propParam.addPart();
                        
                        
                        if("string".equals(type)) {
                            if(value.startsWith("[") && value.endsWith("]")) {
                                valuePart.setName("value").setValue(new CodeType(value.substring(1, value.length()-1)));
                            }else {
                                valuePart.setName("value").setValue(new StringType(value));
                            }
                            
                        }else if("integer".equals(type)) {
                            if(value.startsWith("[") && value.endsWith("]")) {
                                Integer valueInteger = parseInt(value.substring(1, value.length()-1));
                                if(valuePart != null) {
                                    valuePart.setName("value").setValue(new IntegerType(valueInteger));
                                }
                            }else {
                                Integer valueInteger = parseInt(value);
                                if(valueInteger != null) {
                                    valuePart.setName("value").setValue(new IntegerType(valueInteger));
                                }
                            }
                            
                        }else {
                            valuePart.setName("value").setValue(new StringType(value));
                        }
                        
                    }
                }
            }
        }
        
        return parameters;
	}
	
	@GetMapping("/count_match")
    public long countMatch(@RequestParam Optional<String> codeSystemId,
            @RequestParam Optional<String> codeSystemUrl,
            @RequestParam Optional<String> keyword,
            @RequestParam Optional<String> advanceSearch) {
        
	    String system = codeSystemUrl.orElse("");
        if(StringUtils.isBlank(system)) {
            var codeSystem = codeSystemDao._read(FhirUtil.createIdType(codeSystemId.orElseThrow()), false);
            system = codeSystem.getUrl();
        }
        
        var parameters = createFindMatchParameters(system, keyword.orElse(""), advanceSearch.orElse(""));
        return codeSystemDao.countMatches(parameters);
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
	            var codeSystem = codeSystemDao._read(FhirUtil.createIdType(codeSystemId.orElseThrow()), false);
	            system = codeSystem.getUrl();
	        }
	        
	        var parameters = createFindMatchParameters(system, keyword.orElse(""), advanceSearch.orElse(""));
	        
	        parameters.addParameter()
	                   .setName(ConstantKeys.SP_PAGE)
	                   .setValue(new IntegerType(offset.orElse(0)/ConstantKeys.DEFAULT_PAGE_SIZE));
	        
	        parameters.addParameter()
                        .setName(ConstantKeys.SP_COUNT)
                        .setValue(new IntegerType(limit.orElse(ConstantKeys.DEFAULT_PAGE_SIZE)));
            
            
    	    var result = codeSystemDao.findMatches(parameters);
    	    
    	    var match = result.getParameter()
    	                      .stream()
    	                      .filter(x -> "match".equals(x.getName()))
    	                      .findFirst()
    	                      .orElse(null);
    	    	    
    	    List<ConceptDTO> conceptDTOList = new ArrayList<>();
    	    if(match != null) {
    	        for(var part : match.getPart()) {
    	            var code = (Coding) part.getValue();
    	            var conceptDTO = ConceptDTO.fromCode(code);
    	            
    	            conceptDTO.property = new ArrayList<>();
    	            
    	            for(var prop : part.getPart()) {
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
