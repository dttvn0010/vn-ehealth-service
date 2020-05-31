package vn.ehealth.emr.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.hl7.fhir.core.entity.BaseExtension;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

public class EmrUtils {

	public static ObjectMapper createObjectMapper() {
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setTimeZone(TimeZone.getDefault());
        return mapper;        
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
    
    public static BaseExtension findExtensionByURL(List<BaseExtension> lst, @Nonnull String url) {
        return FPUtil.findFirst(lst, x -> url.equals(x.url));
    }
    
    public static void addError(@Nonnull Map<String, List<String>> errors, String field, String message) {
        if(!errors.containsKey(field)) {
            errors.put(field, new ArrayList<>());
        }
        errors.get(field).add(message);
    }
}
