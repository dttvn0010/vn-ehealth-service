package vn.ehealth.emr.utils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmrUtils {
    
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

}
