package vn.ehealth.hl7.fhir.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.model.api.annotation.Child;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseComplexType;
import vn.ehealth.hl7.fhir.core.entity.BaseExtension;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseSimpleType;
import vn.ehealth.hl7.fhir.core.entity.BaseType;
import vn.ehealth.hl7.fhir.core.entity.SimpleExtension;

public class DataConvertUtil {
    private static Logger logger = LoggerFactory.getLogger(DataConvertUtil.class);
            
    @SafeVarargs
    public static<T> List<T> listOf(T...arr) {
        var lst = new ArrayList<T>();
        for(T obj: arr) {
            if(obj != null) lst.add(obj);
        }
        return lst;
    }
    
    public static<T> T getFirst(List<T> lst) {
        if(lst != null && lst.size() > 0) {
            return lst.get(0);
        }
        return null;
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
            if(key != null) {
                m.put(key, value);
            }
        }
        return m;
    }
    
    public static Map<String, ?> emptyMap() {
        return new HashMap<>();
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
            if(key != null) {
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
    
    public static String joinString(List<String> lst, String delimiter) {
        if(lst == null) return null;
        
        return lst.stream()
                    .collect(Collectors.joining(delimiter));
    }
    

    private static Method getMethod(Class<?> clazz, String methodName, Class<?>...paramsType) {
        try {
            return clazz.getMethod(methodName, paramsType);
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }
    
    private static Method getGetter(Class<?> clazz, String fieldName) {
        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return getMethod(clazz, getterName);
    }
    
    private static Method getHasFieldMethod(Class<?> clazz, String fieldName) {
        String hasFieldName = "has" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return getMethod(clazz, hasFieldName);
    }
    
    static List<Method> getSetters(Class<?> clazz, String fieldName) {
        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        var methods = new ArrayList<Method>();
        
        for(var method : clazz.getMethods()) {
            if(method.getParameterCount() != 1) continue;            
            
            if(setterName.equals(method.getName())){            
                methods.add(method);
            }
            
            if(fieldName.endsWith("_")) {
                var modifiedSetterName = setterName.substring(0, setterName.length()-1);
                if(modifiedSetterName.equals(method.getName())){            
                    methods.add(method);
                }
            }
        }
        return methods;
    }
    
    static List<Method> getAddMethods(Class<?> clazz, String fieldName) {
        String addMethodName = "add" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        var methods = new ArrayList<Method>();
        for(var method : clazz.getMethods()) {
            if(method.getParameterCount() != 1) continue;            
            if(!addMethodName.equals(method.getName())) continue;
            methods.add(method);
        }
        return methods;
    }
    
    private static Object newInstance(Class<?> clazz, Object input) {
        for(var constructor : clazz.getConstructors()) {
            if(constructor.getParameterCount() != 1 ) continue;
            
            if(isAssignable(input.getClass(), constructor.getParameters()[0].getType())) {
                try {
                    return constructor.newInstance(input);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    throw new RuntimeException("Cannot invoke constructor :" + constructor.getName(), e);
                }
            }
        }
        return null;
    }
    
    private static Object newInstance(Class<?> clazz) {
        try {
            var constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Cannot create new instance for class:" + clazz.getName());
        }
    }
    
    static Map<Class<?>, Class<?>> objClassToPrimitive = Map.of(
                Integer.class, int.class, 
                Long.class, long.class, 
                Short.class, short.class, 
                Double.class, double.class, 
                Boolean.class, boolean.class 
            );
    
    public static boolean isAssignable(Class<?> fromClass, Class<?> toClass) {
        if(fromClass == null || toClass == null) return false;
        return fromClass.equals(toClass) 
                || fromClass.equals(objClassToPrimitive.get(toClass))
                || toClass.equals(objClassToPrimitive.get(fromClass))
                || toClass.isAssignableFrom(fromClass);
    }
    
    private static boolean isObjectClass(Class<?> clazz) {
        if(clazz == null) return false;
        if(String.class.equals(clazz)) return false;
        if(objClassToPrimitive.containsKey(clazz)) return false;
        if(objClassToPrimitive.containsValue(clazz)) return false;
        
        return true;
    }
    
    public static List<Field> getAnnotedFields(Class<?> fhirType) {
        var fields = new ArrayList<Field>();
        
        for(var field : fhirType.getDeclaredFields()) {
            var anns = Arrays.asList(field.getAnnotations());
            
            if(FPUtil.anyMatch(anns, x -> x instanceof Child)) {
                fields.add(field);
            }
        }
        
        return fields;
    }
    
    public static Field getEntField(Class<?> entType, String fieldName) {
        var type = entType;
        while(type != null) {
            try {
                return type.getDeclaredField(fieldName);
            }catch (NoSuchFieldException | SecurityException e) {                
            }
            type = type.getSuperclass();
        }
        return null;
    }
    
    private static Object getEntFieldValue(Object obj, String fieldName) {
        if(obj == null) return null;        
        try {
            var field = getEntField(obj.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Cannot get field \"%s\" for class \"%s\"",
                        fieldName, obj.getClass().getName()), e);
        }
    }
    
    private static void setEntFieldValue(Object ent, String fieldName, Object fhirVal) {
        if(ent == null || fhirVal == null) return;
        
        try {
            var field = getEntField(ent.getClass(), fieldName);
            field.setAccessible(true);
            var fieldType = field.getType();
            Object cvtVal =  null;
            
            if(BaseSimpleType.class.equals(fieldType)) {
                cvtVal = BaseSimpleType.fromFhir((Type)fhirVal);
                
            }else if(BaseType.class.equals(fieldType)) {
                cvtVal = BaseType.fromFhir((Type) fhirVal);
                field.set(ent, cvtVal);
            }else {            
                cvtVal = fhirToEntity(fhirVal, fieldType);                
            }
            
            field.set(ent, cvtVal);
            
        } catch (Exception e) {
            throw new RuntimeException(String.format("Cannot set field \"%s\" for class \"%s\"",
                    fieldName, ent.getClass().getName()), e);            
        }
    }
    
    private static void setEntFieldValueList(Object ent, String fieldName, List<?> fhirVal) {
        if(ent == null || fhirVal == null) return;
        
        try {
            var field = getEntField(ent.getClass(), fieldName);
            field.setAccessible(true);
            
            var listType = (ParameterizedType) field.getGenericType();
            var itemType = (Class<?>) (listType.getActualTypeArguments()[0]);
            var cvtVal = FPUtil.transform(fhirVal, x -> fhirToEntity(x, itemType));
            field.set(ent, cvtVal);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Cannot set field \"%s\" for class \"%s\"",
                    fieldName, ent.getClass().getName()), e);            
        }
    }
    
    private static Object getFhirFieldValue(Object obj, String fieldName) {
        if(obj == null) return null;
        try {
            
            var hasFieldMethod = getHasFieldMethod(obj.getClass(), fieldName);
            Boolean hasField = true;
            if(hasFieldMethod != null) hasField = (Boolean) hasFieldMethod.invoke(obj);
            
            if(hasField != null && hasField) {
                
                if(fieldName.endsWith("_")) {
                    String modifiedFieldName = fieldName.substring(0, fieldName.length()-1);
                    var getter = getGetter(obj.getClass(), modifiedFieldName);
                    try {
                        return getter.invoke(obj);
                    }catch(Exception e) {
                        logger.error(String.format("Fail to execute method %s for class %s", 
                                        getter.getName(), obj.getClass().getName()));
                    }
                }
                
                var getter = getGetter(obj.getClass(), fieldName);
                return getter.invoke(obj);
            }
            return null;
        }catch (Exception e) {
            throw new RuntimeException(String.format("Cannot get field \"%s\" for class \"%s\"",
                    fieldName, obj.getClass().getName()), e);
        }        
    }
    
    private static void addFhirFieldValue(Object obj, String fieldName, Object entVal) {
        if(obj == null || entVal == null) return;
        
        var methods = getAddMethods(obj.getClass(), fieldName);
        
        for(var method : methods) {
            Object cvtValue = null;
            var inputType = method.getParameters()[0].getType();
            
            try {                
                cvtValue = entityToFhir(entVal, inputType);
            }catch(Exception e) {    
                logger.error(String.format("Cannot convert entity from class %s to fhir class %s",
                            entVal.getClass().getName(), inputType.getName()), e);
            }
            
            if(cvtValue != null) {
                try {
                    method.invoke(obj, cvtValue);
                    return;
                } catch (Exception e) {
                    throw new RuntimeException("Cannot invoke addfield ", e);
                }
            }
        } 
        throw new RuntimeException(String.format("Cannot addvalue \"%s\" for class \"%s\"",
                        fieldName, obj.getClass().getName())); 
    }
    
    private static void setFhirFieldValue(Object obj, String fieldName, Object entVal) {
        if(obj == null || entVal == null) return;
        
        var methods = getSetters(obj.getClass(), fieldName);
        
        for(var method : methods) {            
            var inputType = method.getParameters()[0].getType();
            Object cvtValue = null;
            
            if(inputType.equals(Type.class)) {
                if(entVal instanceof BaseType) {
                    cvtValue = BaseType.toFhir((BaseType) entVal);
                }else {
                    throw new RuntimeException(
                        String.format("Cannot cast %s to BaseType", entVal.getClass().getName()));
                }
            }else {
                try {                
                    cvtValue = entityToFhir(entVal, inputType);
                }catch(Exception e) {    
                    logger.error(String.format("Cannot convert entity from class %s to fhir class %s",
                                entVal.getClass().getName(), inputType.getName()));
                }
            }
                
            if(cvtValue != null) {
                try {
                    method.invoke(obj, cvtValue);
                    return;
                } catch (Exception e) {
                    throw new RuntimeException("Cannot invoke setter ", e);
                }
            }            
        }
        
        throw new RuntimeException(String.format("Cannot set field \"%s\" for class \"%s\"",
                fieldName, obj.getClass().getName()));
    }
    
    private static Object getValue(Object fhirObj) {
        if(fhirObj == null) return null;
        
        var getValueMethod = getMethod(fhirObj.getClass(), "getValue");            
        if(getValueMethod != null) {
            try {
                return getValueMethod.invoke(fhirObj);
            } catch (Exception e) {
                throw new RuntimeException("Cannot invoke getValue method", e);
            }
        }
        return null;
    }
    
    private static Object stringToCode(Class<?> fhirCodeType, String code) {
        try {
            var fromCodeMethod = getMethod(fhirCodeType, "fromCode", String.class);
            return fromCodeMethod.invoke(null, code);
        }catch (Exception e) {
            throw new RuntimeException(String.format("Cannot convert code \"%s\" to class \"%s\"",
                    code, fhirCodeType.getName()), e);            
        }
    }
    
    private static String codeToString(Object fhirCode) {
        if(fhirCode == null) return null;        
        try {
            var toCodeMethod = getMethod(fhirCode.getClass(), "toCode");
            return (String) toCodeMethod.invoke(fhirCode);
        }catch (Exception e) {
            throw new RuntimeException(String.format("Cannot convert fhirCode \"%s\" to String \"%s\"",
                    fhirCode), e);            
        }
    }
    
    public static Meta getMeta(BaseResource entity, String profile) {
        if(entity == null) return null;
        
        var meta = new Meta();
        if(entity._profile != null && entity._profile.size() > 0) {
            meta.setProfile(FPUtil.transform(entity._profile, x -> new CanonicalType(x)));
        }else {
            meta = new Meta().addProfile(ConstantKeys.ENTITY_PROFILE_V1 + profile);
        }
        
        meta.setSecurity(FPUtil.transform(entity._security, x -> entityToFhir(x, Coding.class)));
        meta.setTag(FPUtil.transform(entity._tag, x -> entityToFhir(x, Coding.class)));
        
        if(entity._resDeleted != null) {
            meta.setLastUpdated(entity._resDeleted);
        }else if(entity._resUpdated != null) {
            meta.setLastUpdated(entity._resUpdated);
        }else if(entity._resCreated != null) {
            meta.setLastUpdated(entity._resCreated);
        }
        if(entity._version != null) {
            meta.setVersionId(String.valueOf(entity._version));
        }
        
        return meta;
    }

    private static void setMetaExt(DomainResource obj, BaseResource ent) {
        if(obj != null && ent != null) {
            if(obj.hasMeta()) {
                if (obj.getMeta().hasProfile()) {
                    ent._profile = FPUtil.transform(obj.getMeta().getProfile() , x -> x.getValue());
                }
                if (obj.getMeta().hasSecurity()) {
                    ent._security = FPUtil.transform(obj.getMeta().getSecurity(), x -> fhirToEntity(x, BaseCoding.class));
                }
                if (obj.getMeta().hasTag()) {
                    ent._tag = FPUtil.transform(obj.getMeta().getTag(), x -> fhirToEntity(x, BaseCoding.class));
                }
            }
            
            if(obj.hasExtension()) {
                ent._extension = FPUtil.transform(obj.getExtension(), BaseExtension::fromExtension);
            }
            
            if(obj.hasModifierExtension()) {
                ent._modifierExtension = FPUtil.transform(obj.getModifierExtension(), BaseExtension::fromExtension);
            }
        }
    }
    
    private static void getMetaExt(BaseResource ent, DomainResource obj) {
        if(obj != null && ent != null) {
            obj.getMeta().setProfile(FPUtil.transform(ent._profile, x -> new CanonicalType(x)));
            obj.getMeta().setTag(FPUtil.transform(ent._tag, x -> entityToFhir(x, Coding.class)));
            obj.getMeta().setSecurity(FPUtil.transform(ent._security, x -> entityToFhir(x, Coding.class)));
            obj.setExtension(FPUtil.transform(ent._extension, BaseExtension::toExtension));
            obj.setModifierExtension(FPUtil.transform(ent._modifierExtension, BaseExtension::toExtension));
        }
    }
        
    @SuppressWarnings("unchecked")
    public static <T> T fhirToEntity(Object obj, Class<T> entType) {
        if(obj == null || entType == null) return null;
        
        // Direct assignment
        if(isAssignable(obj.getClass(), entType)) return  (T) obj;
        
        // Via getValue() method
        var objValue = getValue(obj);
        if(objValue != null && isAssignable(objValue.getClass(), entType)) {
            return (T) objValue;
        }
        
        // Via enum
        if(String.class.equals(entType)) {
            if(obj.getClass().isEnum()) return (T) codeToString(obj);
            if(objValue != null && objValue.getClass().isEnum()) return (T) codeToString(objValue);
        }
        
        // Object transform
        if(isObjectClass(entType)) {
            var ent = newInstance(entType);
            var objFields = getAnnotedFields(obj.getClass());
            
            for(var objField : objFields) {
                String fieldName = objField.getName();
                objField.setAccessible(true);
                
                var objChild = getFhirFieldValue(obj, fieldName);
                if(objChild == null) continue;
                
                if(objChild instanceof List) {
                    setEntFieldValueList(ent, fieldName, (List<?>) objChild);
                    
                }else {
                    setEntFieldValue(ent, fieldName, objChild);
                }
            }
            
            if(obj instanceof DomainResource && ent instanceof BaseResource) {
                setMetaExt((DomainResource)obj, (BaseResource)ent);
            }
            
            if(obj instanceof Type && ent instanceof BaseComplexType) {
                var resource = ((Type)obj);
                if(resource.hasExtension()) {
                    ((BaseComplexType)ent).extension = FPUtil.transform(resource.getExtension(), 
                                                        SimpleExtension::fromExtension);
                }
            }
            
            return (T) ent;
        }  
        
        throw new RuntimeException(String.format("Cannot convert fhir class %s  to entity class %s",
                    obj.getClass().getName(), entType.getName()));
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T entityToFhir(Object ent, Class<T> fhirType) {
        if(ent == null || fhirType == null) return null;
        
        // Direct assignment
        if(isAssignable(ent.getClass(), fhirType)) return  (T) ent;
        
        // Via constructor
        var newObj = newInstance(fhirType, ent);
        if(newObj != null) {
            return (T) newObj;
        }
        
        // Via enum
        if(ent instanceof String && fhirType.isEnum()) {
            return (T) stringToCode(fhirType, (String) ent);
        }
        
        // Object transform
        if(isObjectClass(fhirType)) {
            var obj = newInstance(fhirType);
            var objFields = getAnnotedFields(fhirType);
            
            for(var objField : objFields) {
                String fieldName = objField.getName();
                objField.setAccessible(true);
                
                var entChild = getEntFieldValue(ent, fieldName);
                if(entChild == null) continue;
                
                if(entChild instanceof List) {
                    ((List<?>)entChild).forEach(x -> addFhirFieldValue(obj, fieldName, x));
                }else {
                    setFhirFieldValue(obj, fieldName, entChild);
                }                                
            }
            
            if(obj instanceof DomainResource && ent instanceof BaseResource) {
                getMetaExt((BaseResource)ent, (DomainResource)obj);
            }
            
            if(obj instanceof Type && ent instanceof BaseComplexType) {
                var entExt = ((BaseComplexType)ent).extension;
                if(entExt != null && entExt.size() > 0) {
                    ((Type)obj).setExtension(FPUtil.transform(entExt, SimpleExtension::toExtension));
                }
            }
            
            return (T) obj;
        }  
        
        throw new RuntimeException(String.format("Cannot convert entity class %s  to fhir class %s",
                ent.getClass().getName(), fhirType.getName()));
        
    }   
    
}