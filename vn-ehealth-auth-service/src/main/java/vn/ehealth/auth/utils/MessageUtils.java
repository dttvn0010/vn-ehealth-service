package vn.ehealth.auth.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class MessageUtils {
	 private static Properties messages = new Properties();
	    
	    private static Logger logger = LoggerFactory.getLogger(MessageUtils.class);
	            
	    static {
	        try {
	            var input = new ClassPathResource("messages.properties").getInputStream();
	            messages.load(new InputStreamReader(input, Charset.forName("UTF-8")));
	        } catch (IOException e) {
	            logger.error("Cannot read messages.properties", e);
	        }
	    }
	    
	    public static String get(String property) {
	        return messages.getProperty(property, property);
	    }
}
