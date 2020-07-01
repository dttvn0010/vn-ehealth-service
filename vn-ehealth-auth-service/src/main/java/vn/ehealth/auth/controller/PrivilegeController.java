package vn.ehealth.auth.controller;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

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
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

@RequestMapping("/api/privilege")
@RestController
public class PrivilegeController {
	@Autowired
    private PriviligeService priviligeService;
	
	@PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Privilege privilege){
    	 try {
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
}
