package vn.ehealth.emr.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldUtil {

    Logger logger = LoggerFactory.getLogger(FieldUtil.class);
    
    static String camelToSnake(String s) {
        return s.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
    }
    
    private static Object _getField(Object obj, String fieldName) {
        if(obj == null) return null;
        
        Field field;
        try {
            field = obj.getClass().getField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            Log.error("Cannot set field : ", e);
        }
        return null;
    }
    
    public static Object getField(Object obj, String fieldName) {
        String[] arrs = fieldName.split("\\.");
        for(String field: arrs) obj = _getField(obj, field);
        return obj;
    }
    
    public static Map<String, Object> getProjection(@Nonnull Object obj, String... fields) {
        var m = new HashMap<String, Object>();
        for(String key : fields) {
            m.put(key, getField(obj, key));
        }
        return m;
    }
    
    public static void setFields(Object obj, Map<String, Object> record) {
        var fields = obj.getClass().getFields();
        var mapFields = new HashMap<String, Field>();
        
        for(var field : fields) {
            mapFields.put(field.getName(), field);
            mapFields.put(field.getName().toLowerCase(), field);
            mapFields.put(camelToSnake(field.getName()), field);
        }
        
        for(String param : record.keySet()) {
            Object value = record.get(param);
            try {
                Field field = mapFields.get(param);
                
                if(field == null) {
                    field = mapFields.get(param.replace("_", ""));
                }
                
                if(field != null) {
                    if(value instanceof BigDecimal) {
                        field.set(obj, Double.valueOf(String.valueOf(value)));
                    }else {
                        field.set(obj, value);
                    }
                }else {
                    System.out.println(String.format("Field %s not found for class %s", param, obj.getClass().getName()));
                }
            } catch (SecurityException | IllegalAccessException  e) {
                e.printStackTrace();
            }
        }
    }
}
