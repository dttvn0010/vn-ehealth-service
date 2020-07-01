package vn.ehealth.auth.controller;

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

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> saveRoles(@RequestBody Role role){
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
    public ResponseEntity<?> searchRoles(@RequestParam ("keyword") String keyword){
    	try {
    		var result = roleService.search(keyword);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    private Map<String, List<String>> validateForm(Role body){
    	var errors = new HashMap<String, List<String>>();
    	
    	var role = roleService.getByMa(body.ma).orElse(null);
    	
    	if(body.ma == null || body.ma.isBlank()) {
            UserUtil.addError(errors, "ma", MessageUtils.get("validate.required"));
        }
    	
    	if(body.ten == null || body.ten.isBlank()) {
            UserUtil.addError(errors, "ten", MessageUtils.get("validate.required"));
        }
    	
    	if(role != null && !role.id.equals(body.id)) {
	    	UserUtil.addError(errors, "codeExist", MessageUtils.get("code.already.exist"));
	    }
    	
    	return errors;
    }
    
}
