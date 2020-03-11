package vn.ehealth.emr.utils;

import java.text.SimpleDateFormat;
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

}
