package vn.ehealth.auth.controller;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.model.Role;
import vn.ehealth.auth.service.RoleService;
import vn.ehealth.auth.utils.MessageUtils;
import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;


@RequestMapping("/api/role")
@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/getAll")
    @ResponseBody
    public List<Role> getAll() {
        return roleService.getAll();
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Role role){
    	 try {
    		 var errors = validateForm(role);
         	 if(errors.size() > 0) {
         		return ResponseEntity.ok(mapOf("success", false, "errors", errors));
         	 }
             role = roleService.save(role);
             return ResponseEntity.ok(mapOf("success", true, "role", role));
         }catch(Exception e) {
             return ResponseUtil.errorResponse(e);
         }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam ("keyword") Optional<String> keyword){
    	try {
    		var result = roleService.search(keyword.get());
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    private Map<String, List<String>> validateForm(Role body){
    	var errors = new HashMap<String, List<String>>();
    	
    	if(StringUtils.isEmpty(body.code)) {
            UserUtil.addError(errors, "code", MessageUtils.get("validate.required"));
        }
    	
    	if(StringUtils.isEmpty(body.name)) {
            UserUtil.addError(errors, "name", MessageUtils.get("validate.required"));
        }
    	var role = roleService.getByCode(body.code).orElse(null);
    	if(role != null && !role.id.equals(body.id)) {
	    	UserUtil.addError(errors, "code", MessageUtils.get("validate.code.already.exist"));
	    }
    	return errors;
    }
    
}
