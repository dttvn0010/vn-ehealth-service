package vn.ehealth.emr.utils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmrUtils {
    private static Logger logger = LoggerFactory.getLogger(EmrUtils.class);
    
    public static ObjectMapper createObjectMapper() {
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setTimeZone(TimeZone.getDefault());
        return mapper;        
    }
    
    public static SimpleDateFormat createSimpleDateFormat(String format) {
        var sdf = new SimpleDateFormat(format);
        return sdf;
    }
    
    @SuppressWarnings("unchecked")
    public static List<Object> getFieldAsList(Object obj, String key) {
        var map = (Map<String, Object>) obj;
        return (List<Object>) map.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getFieldAsObject(Object obj, String key) {
        var map = (Map<String, Object>) obj;
        return (Map<String, Object>) map.get(key);
    }

    public static ResponseEntity<?> errorResponse(Exception e) {
        logger.error("Error in HttpRequest:", e);
        var result = mapOf("success", false, "error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
