package vn.ehealth.emr.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.ehealth.emr.dto.UserRequestDTO;
import vn.ehealth.emr.model.Role;
import vn.ehealth.emr.service.EmailService;
import vn.ehealth.emr.service.UserService;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    public static Properties messages = new Properties();
    
    static {
        try {
            var input = new ClassPathResource("static/message/user.properties").getInputStream();
            messages.load(new InputStreamReader(input, Charset.forName("UTF-8")));
        } catch (IOException e) {
            logger.error("Cannot read user.properties", e);
        }
    }
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;

    @PostMapping("/create_user")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            var errors = new ArrayList<>();
            if(StringUtils.isEmpty(userRequestDTO.emrPerson.email)) { 
                errors.add(Map.of("field", "email", "message", messages.getProperty("error.email.required")));
            }
            
            if(StringUtils.isEmpty(userRequestDTO.emrPerson.tendaydu)) {
                errors.add(Map.of("field", "tendaydu", "message", messages.getProperty("error.tendaydu.required")));
            }
            
            if(errors.size() > 0) {
                var result =  Map.of("success", false, "errors", errors);
                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }
            
            var password =  RandomStringUtils.random(10, true, false);
            var user = userService.createUser(userRequestDTO.emrPerson, userRequestDTO.roleIds, password);
            var mailSubject = messages.getProperty("mail.subject");
            var mailContent = messages.getProperty("mail.content")
                                        .replace("[$NAME$]", userRequestDTO.emrPerson.tendaydu)
                                        .replace("[$EMAIL$]", userRequestDTO.emrPerson.email)
                                        .replace("[$PASSWORD$]", password);
            
            emailService.sendEmail(userRequestDTO.emrPerson.email, mailSubject, mailContent);

            var result = Map.of(
                    "success", true,
                    "user", user
            );

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = Map.of(
                    "success", false,
                    "errors", List.of(Map.of("unknown", error))
            );
            logger.error("Error save user: ", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getRolesByUsername")
    @ResponseBody
    public List<Role> getRolesByUsername(@RequestParam("username") String username) {
        return userService.getRolesByUsername(username);
    }

}