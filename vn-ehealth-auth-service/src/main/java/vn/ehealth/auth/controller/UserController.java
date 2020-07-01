package vn.ehealth.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import vn.ehealth.auth.model.User;
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
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired private UserService userService;
    public static Properties messages = new Properties();
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    
    static {
        try {
            var input = new ClassPathResource("static/message/user.properties").getInputStream();
            messages.load(new InputStreamReader(input, Charset.forName("UTF-8")));
        } catch (IOException e) {
            logger.error("Cannot read user.properties", e);
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
    	validateForm(user);
        try {
        	var passwordEncoder = new BCryptPasswordEncoder();
        	user.password = passwordEncoder.encode(user.password);
            user = userService.save(user);
            return ResponseEntity.ok(mapOf("success", true, "user", user));
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User body) {
        try {
        	var user = userService.getByUsername(body.username).get();
        	user.tenDayDu = body.tenDayDu;
        	user.email = body.email;
        	user.diaChi = body.diaChi;
        	user.soDienThoai = body.soDienThoai;
        	user = userService.save(user);
            return ResponseEntity.ok(mapOf("success", true, "user", user));
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @PutMapping("/change_password")
    public ResponseEntity<?> changePassword(@RequestBody User body) {
        try {
        	var passwordEncoder = new BCryptPasswordEncoder();
        	var user = userService.getByUsername(body.username).get();
        	user.password = passwordEncoder.encode(body.password);
        	user = userService.save(user);
            return ResponseEntity.ok(mapOf("success", true, "user", user));
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchUser(@RequestParam ("keyword") String keyword){
    	try {
    		var result = userService.search(keyword);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    private boolean validatePassword() {
    	var regexPassword = "^(?=.*[A-Z].*[A-Z])(?=.*[!@#$&*])(?=.*[0-9].*[0-9])(?=.*[a-z].*[a-z].*[a-z]).{8}$" ;
 	    
 	    Matcher matcher = null;
 	    Pattern pattern = null;
 	    
 	    matcher = pattern.matcher(regexPassword);
 	    return matcher.matches();
    }
    
    private boolean validateEmail() {
    	var regexEmail = "/[A-Z0-9._%+-]+@[A-Z0-9-]+.+.[A-Z]{2,4}/igm" ;
 	    
 	    Matcher matcher;
 	    Pattern pattern = null;
 	    
 	    matcher = pattern.matcher(regexEmail);
 	    return matcher.matches();
    }
    
    
    private Map<String, List<String>> validateForm(User body) {
	    var errors = new HashMap<String, List<String>>();
	    
	    
	    if(body.username == null || body.username.isBlank()) {
	        UserUtil.addError(errors, "username", MessageUtils.get("validate.required"));
	    }
	    
	    if(body.password == null || body.password.isBlank()) {
            UserUtil.addError(errors, "password", MessageUtils.get("validate.required"));
        }
	    
	    if(body.email != null || body.email.isBlank()) {
	    	UserUtil.addError(errors, "email", MessageUtils.get("validate.required"));	            
	    }
	    
	    if(!validatePassword()) {
	    	UserUtil.addError(errors, "validatePassword", MessageUtils.get("validate.password.required"));
	    }
	    
	    if(!validateEmail()) {
	    	UserUtil.addError(errors, "validateEmail", MessageUtils.get("validate.email.required"));
	    }
	    
	    return errors;
	}

}