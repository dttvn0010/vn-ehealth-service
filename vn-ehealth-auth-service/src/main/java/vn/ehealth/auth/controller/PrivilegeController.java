package vn.ehealth.auth.controller;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.model.Privilege;
import vn.ehealth.auth.service.PriviligeService;
import vn.ehealth.auth.utils.MessageUtils;
import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

@RequestMapping("/api/privilege")
@RestController
public class PrivilegeController {
	@Autowired
    private PriviligeService priviligeService;
	
	@PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Privilege privilege){
    	 try {
    		 var errors = validateForm(privilege);
          	 if(errors.size() > 0) {
          		return ResponseEntity.ok(mapOf("success", false, "errors", errors));
          	 }
             privilege = priviligeService.save(privilege);
             return ResponseEntity.ok(mapOf("success", true, "privilege", privilege));
         }catch(Exception e) {
             return ResponseUtil.errorResponse(e);
         }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam ("keyword") String keyword){
    	try {
    		var result = priviligeService.search(keyword);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    private Map<String, List<String>> validateForm(Privilege body){
    	var errors = new HashMap<String, List<String>>();
    	
    	var privilege = priviligeService.getByCode(body.code).orElse(null);
    	
    	if(StringUtils.isEmpty(privilege.code)) {
            UserUtil.addError(errors, "code", MessageUtils.get("validate.required"));
        }
    	
    	if(StringUtils.isAllBlank(privilege.name)) {
            UserUtil.addError(errors, "name", MessageUtils.get("validate.required"));
        }
    	
    	if(privilege != null && !privilege.id.equals(body.id)) {
	    	UserUtil.addError(errors, "code", MessageUtils.get("validate.code.already.exist"));
	    }
    	
    	return errors;
    }
}
