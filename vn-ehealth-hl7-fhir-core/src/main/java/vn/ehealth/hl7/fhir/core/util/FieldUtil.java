package vn.ehealth.hl7.fhir.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldUtil {

    private static Logger log = LoggerFactory.getLogger(FieldUtil.class);
    
    public static Object getFieldValue(Object obj, String fieldName){
        try{
            Field field = obj.getClass().getDeclaredField(fieldName);
            if(field !=null){
                field.setAccessible(true);
                return field.get(obj);
            }
        } catch(NoSuchFieldException e){
            log.debug("No field \"" + fieldName + "\" for class " + obj.getClass().getName());
        } catch (IllegalArgumentException e) {
            log.error(e.toString());
        } catch (IllegalAccessException e) {
            log.error(e.toString());
        }
        
        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);        
        try{            
            Method method = obj.getClass().getMethod(methodName);
            return method.invoke(obj);
        } catch(NoSuchMethodException e){
            log.error("No method \"" + methodName + "\" for class " + obj.getClass().getName());            
        } catch(IllegalArgumentException e){
            log.error(e.toString());
        }catch (IllegalAccessException e) {
            log.error(e.toString());
        } catch (InvocationTargetException e) {
            log.error(e.toString());
        }
        
        methodName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try{
            Method method = obj.getClass().getMethod(methodName);
            return method.invoke(obj);
        } catch(NoSuchMethodException e){
            log.error("No method \"" + methodName + "\" for class " + obj.getClass().getName());            
        } catch(IllegalArgumentException e){
            log.error(e.toString());
        }catch (IllegalAccessException e) {
            log.error(e.toString());
        } catch (InvocationTargetException e) {
            log.error(e.toString());
        }
        return null;
    }
    
    @SuppressWarnings("rawtypes")
    public static long[] getArrayLongField(List data, String fieldName){
        long[] fieldData = new long[data.size()];
        for(int i = 0; i < fieldData.length; i++){
            if(data.get(i) != null){
                fieldData[i] = (Long) getFieldValue(data.get(i), fieldName);
            }else{
                fieldData[i] = 0l;
            }
        }
        return fieldData;
    }
    
    @SuppressWarnings("rawtypes")
    public static String[] getArrayStringField(List data, String fieldName){
        String[] fieldData = new String[data.size()];
        for(int i = 0; i < fieldData.length; i++){
            if(data.get(i) != null){
                fieldData[i] = (String) getFieldValue(data.get(i), fieldName);
            }else{
                fieldData[i] = "";
            }
        }
        return fieldData;
    }
    
    public static Map<String, Object> getObjectProjector(Object obj, String[] fieldsName){
        Map<String, Object> projector = new HashMap<String, Object>();
        for(String fieldName : fieldsName){
            projector.put(fieldName, getFieldValue(obj, fieldName));
        }
        return projector;
    }
    
    @SuppressWarnings("rawtypes")
    public static Map[] getListProjector(List list, String[] fieldsName){
        Map[] projector = new Map[list.size()];
        for(int i = 0; i < list.size(); i++){
            projector[i] = getObjectProjector(list.get(i), fieldsName);
        }
        return projector;
    }
}
