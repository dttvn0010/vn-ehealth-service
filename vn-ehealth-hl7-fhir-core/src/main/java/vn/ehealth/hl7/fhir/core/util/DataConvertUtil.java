package vn.ehealth.hl7.fhir.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataConvertUtil {
    
    @SafeVarargs
    public static<T> List<T> listOf(T...arr) {
        var lst = new ArrayList<T>();
        for(T obj: arr) {
            if(obj != null) lst.add(obj);
        }
        return lst;
    }
    
    public static  Map<String, String> mapOf(String ...obj) {
        var m = new HashMap<String, String>();
        if(obj.length % 2 != 0) {
            throw new RuntimeException("Number of map items must be even");
        }
        int n = obj.length / 2;
        for(int i = 0; i < n; i++) {
            String key = obj[2*i];
            String value = obj[2*i + 1];
            if(key != null && value != null) {
                m.put(key, value);
            }
        }
        return m;
    }
    
    public static Map<String, Object> mapOf(Object ...obj) {
        var m = new HashMap<String, Object>();
        if(obj.length % 2 != 0) {
            throw new RuntimeException("Number of map items must be even");
        }
        int n = obj.length / 2;
        for(int i = 0; i < n; i++) {
            Object key = obj[2*i];
            Object value = obj[2*i + 1];
            if(key != null && value != null) {
                m.put(key.toString(), value);
            }
        }
        return m;
    }
    
    public static <T> Map<String, Map<String, T>> mapOf3(String keyLevel1, String keyLevel2, T value) {
        if(value != null) {
            return Map.of(keyLevel1, Map.of(keyLevel2, value));
        }
        return new HashMap<>();
    }
    
    public static <T, U> List<U> transform(List<T> lst, Function<T, U> func) {
        if(lst != null) {
            return lst.stream()
                      .filter(x -> x != null) 
                      .map(x -> func.apply(x))
                      .collect(Collectors.toList());
        }
        return null;
    }
}