package vn.ehealth.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

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

}