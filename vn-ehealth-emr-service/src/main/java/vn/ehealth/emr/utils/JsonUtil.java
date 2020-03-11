package vn.ehealth.emr.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    
    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
            
    private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;
    
    private static ObjectMapper mapper = EmrUtils.createObjectMapper();
    
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
}
