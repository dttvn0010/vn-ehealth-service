package vn.ehealth.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.ehealth.auth.model.User;
import vn.ehealth.auth.service.UserService;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;
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
    
    @PostMapping("/save")
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        try {
            user = userService.save(user);
            return ResponseEntity.ok(mapOf("success", true, "user", user));
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }

}