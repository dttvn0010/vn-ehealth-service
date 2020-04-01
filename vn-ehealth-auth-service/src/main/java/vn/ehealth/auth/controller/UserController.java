package vn.ehealth.auth.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.ehealth.auth.dto.request.UserRequestDTO;
import vn.ehealth.auth.dto.request.UserUpdateDTO;
import vn.ehealth.auth.dto.response.ListResultDTO;
import vn.ehealth.auth.dto.response.UserDTO;
import vn.ehealth.auth.model.Role;
import vn.ehealth.auth.service.EmailService;
import vn.ehealth.auth.service.UserService;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
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

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/create_user")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            var errors = new ArrayList<>();
            if (StringUtils.isEmpty(userRequestDTO.emrPerson.email)) {
                errors.add(mapOf("field", "email", "message", messages.getProperty("error.email.required")));
            }

            if (StringUtils.isEmpty(userRequestDTO.emrPerson.tendaydu)) {
                errors.add(mapOf("field", "tendaydu", "message", messages.getProperty("error.tendaydu.required")));
            }

            if (errors.size() > 0) {
                var result = mapOf("success", false, "errors", errors);
                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }

            var password = RandomStringUtils.random(10, true, false);
            var user = userService.createUser(userRequestDTO.emrPerson, userRequestDTO.roleIds, password);
            var mailSubject = messages.getProperty("mail.subject");
            var mailContent = messages.getProperty("mail.content")
                    .replace("[$NAME$]", userRequestDTO.emrPerson.tendaydu)
                    .replace("[$EMAIL$]", userRequestDTO.emrPerson.email)
                    .replace("[$PASSWORD$]", password);

            emailService.sendEmail(userRequestDTO.emrPerson.email, mailSubject, mailContent);

            var result = mapOf(
                    "success", true,
                    "user", user
            );

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }

    @GetMapping("/getRolesByUsername")
    @ResponseBody
    public List<Role> getRolesByUsername(@RequestParam("username") String username) {
        return userService.getRolesByUsername(username);
    }


    @GetMapping("findAll")
    @ResponseBody
    public ResponseEntity<ListResultDTO<UserDTO>> findAll(@RequestParam("page") Integer page,
                                                          @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(userService.findAll(page, pageSize));
    }

    @GetMapping("search")
    @ResponseBody
    public ResponseEntity<ListResultDTO<UserDTO>> search(@RequestParam("page") Integer page,
                                                         @RequestParam("pageSize") Integer pageSize,
                                                         @RequestParam("keyword") String keyword,
                                                         @RequestParam("roleId") String roleId) {
        return ResponseEntity.ok(userService.search(roleId, keyword, page, pageSize));
    }

    @GetMapping("findById")
    @ResponseBody
    public ResponseEntity<UserDTO> findById(@RequestParam("id") String id){
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            var errors = new ArrayList<>();
            if (StringUtils.isEmpty(userUpdateDTO.getEmrPerson().email)) {
                errors.add(mapOf("field", "email", "message", messages.getProperty("error.email.required")));
            }

            if (StringUtils.isEmpty(userUpdateDTO.getEmrPerson().tendaydu)) {
                errors.add(mapOf("field", "tendaydu", "message", messages.getProperty("error.tendaydu.required")));
            }

            if (errors.size() > 0) {
                var result = mapOf("success", false, "errors", errors);
                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }

            var user = userService.update(userUpdateDTO);
            var result = mapOf(
                    "success", true,
                    "user", user
            );

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
}