package vn.ehealth.emr.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    
    private static Object _getField(Object obj, String prop) {
        if(obj == null) return null;
        
        Field field;
        try {
            field = obj.getClass().getField(prop);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            String message = String.format("No such field %s for class %s", prop, obj.getClass().getName());
            Log.error(message, e);
        }
        return null;
    }
    
    private static Object getField(Object obj, String[] propChain) {
        for(String prop: propChain) obj = _getField(obj, prop);
        return obj;
    }
    
    public static Object getField(Object obj, String fieldName) {
        return getField(obj, fieldName.split("\\."));
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getProjection(@Nonnull Object obj, String... fields) {
        var map = new HashMap<String, Object>();
        
        for(String field : fields) {            
            String[] propChain = field.split("\\.");
            Map<String, Object> node = map;            
            for(int i = 0; i < propChain.length - 1; i++) {
                String prop = propChain[i];
                if(!node.containsKey(prop)) {
                    node.put(prop, new HashMap<String, Object>());
                }
                node = (Map<String, Object>) (node.get(prop));
            }
            String lastProp = propChain[propChain.length-1];
            var value = getField(obj, propChain);
            node.put(lastProp, value);
        }
        return map;
    }
    
    @SuppressWarnings("rawtypes")
    public static List  getListProjection(@Nonnull List lst, String... fields) {
        if(lst != null) {
            var result = new ArrayList<>();
            for(var obj : lst) {
                result.add(getProjection(obj, fields));
            }
            return result;
        }
        return null;
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
