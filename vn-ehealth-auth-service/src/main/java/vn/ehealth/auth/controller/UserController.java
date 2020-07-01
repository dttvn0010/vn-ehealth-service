package vn.ehealth.auth.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import vn.ehealth.auth.model.User;
import vn.ehealth.auth.service.RoleService;
import vn.ehealth.auth.service.UserService;
import vn.ehealth.auth.utils.MessageUtils;
import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired private UserService userService;
    @Autowired private RoleService roleService;
    public static Properties messages = new Properties();
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    static {
        try {
            var input = new ClassPathResource("static/message/user.properties").getInputStream();
            messages.load(new InputStreamReader(input, Charset.forName("UTF-8")));
        } catch (IOException e) {
            logger.error("Cannot read user.properties", e);
        }
    }
    
    private Map<String, List<String>> validateCreateUser(User body) {
        var errors = new HashMap<String, List<String>>();
        
        var user = userService.getByUsername(body.username).orElse(null);
        
        if(body.username == null || body.username.isBlank()) {
            UserUtil.addError(errors, "username", MessageUtils.get("validate.required"));
        }
        
        errors.putAll(validatePassword(body.password, body.password2));
        
        if(user != null) {
            UserUtil.addError(errors, "username", MessageUtils.get("validate.username.already.exist"));
        }
        
        if(StringUtils.isBlank(body.email)) {
            UserUtil.addError(errors, "email", MessageUtils.get("validate.required"));              
        }
        
        if(StringUtils.isBlank(body.tenDayDu)) {
            UserUtil.addError(errors, "tenDayDu", MessageUtils.get("validate.required"));              
        }
        
        if(StringUtils.isEmpty(body.roleCode)) {
            UserUtil.addError(errors, "roleCode", MessageUtils.get("validate.required"));              
        }
        
        if(roleService.getByCode(body.roleCode).isEmpty()) {
            UserUtil.addError(errors, "roleCode", MessageUtils.get("validate.role.invalid"));
        }
        
        return errors;
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            user.id = null;
            
            var errors = validateCreateUser(user);
            if(errors.size() > 0) {
                return ResponseEntity.ok(mapOf("success", false, "errors", errors));
            }
        	user.roleId = roleService.getByCode(user.roleCode).map(x -> x.id).orElse(null);
        	user.password = passwordEncoder.encode(user.password);
            user = userService.save(user);
            return ResponseEntity.ok(mapOf("success", true, "user", user));
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    private Map<String, List<String>> validateUpdateUser(User body) {
        var errors = new HashMap<String, List<String>>();
        
        var user = userService.getByUsername(body.username).orElse(null);
        
        if(user == null) {
            UserUtil.addError(errors, "username", MessageUtils.get("validate.user.invalid"));
        }
        
        if(StringUtils.isBlank(body.email)) {
            UserUtil.addError(errors, "email", MessageUtils.get("validate.required"));              
        }
        
        if(StringUtils.isBlank(body.tenDayDu)) {
            UserUtil.addError(errors, "tenDayDu", MessageUtils.get("validate.required"));              
        }
        
        if(StringUtils.isEmpty(body.roleCode)) {
            UserUtil.addError(errors, "roleCode", MessageUtils.get("validate.required"));              
        }
        
        if(roleService.getByCode(body.roleCode).isEmpty()) {
            UserUtil.addError(errors, "roleCode", MessageUtils.get("validate.role.invalid"));
        }
        
        return errors;
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User body) {
        try {
        	var errors = validateUpdateUser(body);
        	if(errors.size() > 0) {
        		return ResponseEntity.ok(mapOf("success", false, "errors", errors));
        	}
        	
        	var user = userService.getByUsername(body.username).get();
        	user.roleId = roleService.getByCode(user.roleCode).map(x -> x.id).orElse(null);
        	user.tenDayDu = body.tenDayDu;
        	user.email = body.email;
        	user.diaChi = body.diaChi;
        	user.soDienThoai = body.soDienThoai;
        	user.chungChiHanhNghe = body.chungChiHanhNghe;
        	user = userService.save(user);
            return ResponseEntity.ok(mapOf("success", true, "user", user));
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    static class ChangePasswordBody {
        public String username;
        public String oldPassword;
        public String password;
        public String password2;
    }
    
    private Map<String, List<String>> validateChangePassword(ChangePasswordBody body) {
        var errors = validatePassword(body.password, body.password2);
        
        var user = userService.getByUsername(body.username).orElse(null);
        
        if(user == null) {
            UserUtil.addError(errors, "username", MessageUtils.get("validate.user.invalid"));
        }else if(user.password.equals(passwordEncoder.encode(body.oldPassword))) {
            UserUtil.addError(errors, "oldPassword", MessageUtils.get("validate.oldPassword.wrong"));
        }
        
        return errors;
    }
        
    @PutMapping("/change_password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordBody body) {
        try {
        	var errors = validateChangePassword(body);
        	
        	if(errors.size() > 0) {
                return ResponseEntity.ok(mapOf("success", false, "errors", errors));
            }       
        	
        	var user = userService.getByUsername(body.username).get();
        	
        	user.password = passwordEncoder.encode(body.password);
        	user = userService.save(user);
            return ResponseEntity.ok(mapOf("success", true));
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchUser(@RequestParam ("keyword") Optional<String> keyword){
    	try {
    		var result = userService.search(keyword.orElse(""));
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
        
    private Map<String, List<String>> validatePassword(String password, String password2){
    	var errors = new HashMap<String, List<String>>();
    	
    	if(StringUtils.isEmpty(password)) {
            UserUtil.addError(errors, "password", MessageUtils.get("validate.required"));
        }
    	
    	if(errors.size() == 0 && password.length() < 6) {
            UserUtil.addError(errors, "password", MessageUtils.get("validate.password.too.short"));            
    	}
    
        if(errors.size() == 0) {
            boolean containAlpha = false;
            for(int i = 0; i < password.length(); i++) {
                char c = password.toUpperCase().charAt(i);
                if(c >= 'A' && c <= 'Z') {
                    containAlpha = true;
                    break;
                }
            }
            
            if(!containAlpha) {
                UserUtil.addError(errors, "password", MessageUtils.get("validate.password.no.alpha"));
            }
        }
        
        if(StringUtils.isEmpty(password2)) {
            UserUtil.addError(errors, "password2", MessageUtils.get("validate.required"));
        }else if(!password2.equals(password)) {
            UserUtil.addError(errors, "password2", MessageUtils.get("validate.password.confirm.wrong"));
        }
    	
    	return errors;
    }

}