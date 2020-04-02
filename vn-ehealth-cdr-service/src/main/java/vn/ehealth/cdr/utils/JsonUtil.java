package vn.ehealth.cdr.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    
    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
            
    private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;
    
    private static ObjectMapper mapper = CDRUtils.createObjectMapper();
    
    private static Properties fieldsConvertProp = new Properties();
    
    static {
        try {
            fieldsConvertProp.load(new ClassPathResource("fields_convert.properties").getInputStream());
        } catch (IOException e) {
            logger.error("Cannot read fieldsConvert properties", e);
        }
        
    }
    
    
    public static String dumpObject(Object obj) {
        mapper.setDateFormat(sdf);
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("Cannot dump object to json ", e);
        }
        return "";
    }
    
     public static <T> T parseObject(String jsonSt, Class<T> cl) {
        mapper.setDateFormat(sdf);
        try {
            return mapper.readValue(jsonSt, cl);
        } catch (IOException e) {
            logger.error("Cannot parse object from json ", e);
        }
        return null;
    }
     
     @SuppressWarnings("unchecked")
	 public static Map<String, Object> objectToMap(Object obj) {
         return mapper.convertValue(obj, Map.class);
     }
     
     public static String preprocess(String message) {
         if(message != null) {
             for(var entry: fieldsConvertProp.entrySet()) {
                 String field = (String) entry.getKey();
                 String fieldReplace = (String) entry.getValue();
                 message = message.replace("\"" + field + "\"", "\"" + fieldReplace + "\"");
             }
             return message;
         }
         return null;
     }
     
     @SuppressWarnings("unchecked")
     public static Map<String, Object> parseJson(String jsonSt) throws JsonParseException, JsonMappingException, IOException {
         mapper.setDateFormat(sdf);
         return mapper.readValue(jsonSt, Map.class);
     }
}
