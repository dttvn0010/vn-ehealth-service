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
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import java.util.List;

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
    
}
