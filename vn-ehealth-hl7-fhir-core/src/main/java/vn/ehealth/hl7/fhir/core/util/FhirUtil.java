package vn.ehealth.hl7.fhir.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Enumeration;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.StringType;

import ca.uhn.fhir.model.api.annotation.Child;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

public class FhirUtil {

    public static CodeableConcept findConceptBySystem(List<CodeableConcept> lst, @Nonnull String system) {
        if(lst == null) return null;
        
        for(var concept : lst) {
            if(FPUtil.anyMatch(concept.getCoding(), x -> system.equals(x.getSystem()))) {
                return concept;
            };
        }
        return null;
    }
    
    public static Extension findExtensionByURL(List<Extension> lst, @Nonnull String url) {
        return FPUtil.findFirst(lst, x -> url.equals(x.getUrl()));
    }
    
    public static Identifier createIdentifier(String value, String system) {
        var identifier = new Identifier();
        identifier.setValue(value);
        identifier.setSystem(system);
        return identifier;
    }
    
    public static Period createPeriod(Date start, Date end) {
        var period = new Period();
        period.setStart(start);
        period.setEnd(end);
        return period;
    }
    
    public static Identifier createIdentifier(String value, String system, Date start, Date end) {
        var identifier = new Identifier();
        identifier.setValue(value);
        identifier.setSystem(system);
        identifier.setPeriod(createPeriod(start, end));
        return identifier;
    }
    
    public static Address createAddress(String text) {
        if(text == null) return null;
        var address = new Address();
        address.setText(text);
        return address;
    }
    
    public static HumanName createHumanName(String text) {
        if(text == null) return null;
        var name = new HumanName();
        name.setText(text);
        return name;
    }
    
    
    public static ContactPoint createContactPoint(String value, ContactPointSystem system) {
        var contactPoint = new ContactPoint();
        contactPoint.setValue(value);
        contactPoint.setSystem(system);
        return contactPoint;
    }
    
    public static CodeableConcept createCodeableConcept(String text) {
        if(text == null) return null;
        var concept = new CodeableConcept();
        concept.setText(text);
        return concept;
    }
    
    public static Annotation createAnnotation(String text) {
        if(text == null) return null;
        var annotation = new Annotation();
        annotation.setText(text);
        return annotation;
    }
    
    public static Extension createExtension(String url, String text) {
        if(text == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(new StringType(text));
        return extension;
    }
    
    public static Extension createExtension(String url, CodeableConcept concept) {
        if(concept == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(concept);
        return extension;
    }
    
    public static CodeableConcept createCodeableConcept(String code, String name, String system) {
        var concept = new CodeableConcept();
        concept.setText(name);
        var coding = new Coding();
        coding.setCode(code);
        coding.setDisplay(name);
        coding.setSystem(system);
        concept.addCoding(coding);
        return concept;
    }
    
    public static Reference createReference(Resource resource) {
        if(resource == null) return null;
        return new Reference(resource.getResourceType() + "/" + resource.getId());
    }
    
    public static Reference createReference(ResourceType type, String id) {
        if(type == null || id == null) return null;
        return new Reference(type + "/" + id);
    }
    
    public static Reference createReference(String display) {
        if(display == null) return null;
        var ref = new Reference();
        ref.setDisplay(display);
        return ref;
    }
    
    public static IdType createIdType(String id) {
        return new IdType(id);
    }
    
    public static IdType idTypeFromRef(Reference ref) {
        var id = idFromRef(ref);
        return id != null? new IdType(id) : null;
    }
    
    public static String idFromRef(Reference ref) {
        if((ref != null && ref.hasReference())) {
            var arr = ref.getReference().split("/");
            if(arr.length == 2) {
                return arr[1];
            }
        }
        return null;
    }
    
    public static ResourceType getResourceType(Reference ref) {
    	if(ref != null && ref.hasReference()) {
    		var arr = ref.getReference().split("/");
            if(arr.length == 2) {
                return ResourceType.fromCode(arr[0]);
            }
    	}
    	return null;
    }
    
    public static IdType createIdType(Reference ref) {
        if((ref != null && ref.hasReference())) {
            var arr = ref.getReference().split("/");
            if(arr.length == 2) {
                return new IdType(arr[1]);
            }
        }
        return null;
    }
    
    public static boolean conceptHasCode(CodeableConcept concept, String code, String system) {
        if(concept != null && concept.hasCoding()) {
            return FPUtil.anyMatch(concept.getCoding(), 
                    x -> code.equals(x.getCode()) && system.equals(x.getSystem()));
        }
        return false;
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
    
    static Method getSetter(Class<?> clazz, String fieldName, Class<?> inputType) {
        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        
        for(var method : clazz.getMethods()) {
            if(method.getParameterCount() != 1) continue;            
            if(!setterName.equals(method.getName())) continue;
            
            if(isAssignable(inputType, method.getParameters()[0].getType())) {
                return method;
            }
        }
        return null;
    }
    
    static Method getAddMethod(Class<?> clazz, String fieldName, Class<?> inputType) {
        String addMethodName = "add" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        
        for(var method : clazz.getMethods()) {
            if(method.getParameterCount() != 1) continue;            
            if(!addMethodName.equals(method.getName())) continue;
            
            if(isAssignable(inputType, method.getParameters()[0].getType())) {
                return method;
            }
        }
        return null;
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
    
    private static List<Field> getAnnotedFields(Class<?> fhirType) {
        var fields = new ArrayList<Field>();
        
        for(var field : fhirType.getDeclaredFields()) {
            var anns = Arrays.asList(field.getAnnotations());
            
            if(FPUtil.anyMatch(anns, x -> x instanceof Child)) {
                fields.add(field);
            }
        }
        
        return fields;
    }
    
    private static Field getEntField(Class<?> entType, String fieldName) {
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
    
    private static void setEntFieldValue(Object obj, String fieldName, Object value) {
        if(obj == null) return;
        
        try {
            var field = getEntField(obj.getClass(), fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Cannot set field \"%s\" for class \"%s\"",
                    fieldName, obj.getClass().getName()), e);            
        }
    }
    
    private static Object getFhirFieldValue(Object obj, String fieldName) {
        if(obj == null) return null;
        try {
            var getter = getGetter(obj.getClass(), fieldName);
            
            var hasFieldMethod = getHasFieldMethod(obj.getClass(), fieldName);
            Boolean hasField = true;
            if(hasFieldMethod != null) hasField = (Boolean) hasFieldMethod.invoke(obj);
            
            if(hasField != null && hasField) {
                return getter.invoke(obj);
            }
            return null;
        }catch (Exception e) {
            throw new RuntimeException(String.format("Cannot get field \"%s\" for class \"%s\"",
                    fieldName, obj.getClass().getName()), e);
        }        
    }
    
    private static void addFhirFieldValue(Object obj, String fieldName, Object value) {
        if(obj == null) return;
        
        try {
            var addMethod = getAddMethod(obj.getClass(), fieldName, value.getClass());        
            addMethod.invoke(obj, value);
        }catch (Exception e) {
            throw new RuntimeException(String.format("Cannot set field \"%s\" for class \"%s\"",
                    fieldName, obj.getClass().getName()), e);            
        }
    }
    
    private static void setFhirFieldValue(Object obj, String fieldName, Object value) {
        if(obj == null) return;
        
        try {
            var setter = getSetter(obj.getClass(), fieldName, value.getClass());        
            setter.invoke(obj, value);
        }catch (Exception e) {
            throw new RuntimeException(String.format("Cannot set field \"%s\" for class \"%s\"",
                    fieldName, obj.getClass().getName()), e);            
        }
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
        if(entity.profile != null && entity.profile.size() > 0) {
            meta.setProfile(FPUtil.transform(entity.profile, x -> new CanonicalType(x)));
        }else {
            meta = new Meta().addProfile(ConstantKeys.ENTITY_PROFILE_V1 + profile);
        }
        
        meta.setSecurity(FPUtil.transform(entity.security, x -> entityToFhir(x, Coding.class)));
        meta.setTag(FPUtil.transform(entity.tag, x -> entityToFhir(x, Coding.class)));
        
        if(entity.resDeleted != null) {
            meta.setLastUpdated(entity.resDeleted);
        }else if(entity.resUpdated != null) {
            meta.setLastUpdated(entity.resUpdated);
        }else if(entity.resCreated != null) {
            meta.setLastUpdated(entity.resCreated);
        }
        if(entity.version != null) {
            meta.setVersionId(String.valueOf(entity.version));
        }
        
        return meta;
    }

    private static void setMetaExt(DomainResource obj, BaseResource ent) {
        if(obj != null && ent != null) {
            if(obj.hasMeta()) {
                if (obj.getMeta().hasProfile()) {
                    ent.profile = FPUtil.transform(obj.getMeta().getProfile() , x -> x.getValue());
                }
                if (obj.getMeta().hasSecurity()) {
                    ent.security = FPUtil.transform(obj.getMeta().getSecurity(), x -> fhirToEntity(x, BaseCoding.class));
                }
                if (obj.getMeta().hasTag()) {
                    ent.tag = FPUtil.transform(obj.getMeta().getTag(), x -> fhirToEntity(x, BaseCoding.class));
                }
            }
            
            ent.extension = obj.hasExtension()? obj.getExtension() : null;
            ent.modifierExtension = obj.hasModifierExtension()? obj.getModifierExtension(): null;
        }
    }
    
    private static void getMetaExt(BaseResource ent, DomainResource obj) {
        if(obj != null && ent != null) {
            obj.getMeta().setProfile(FPUtil.transform(ent.profile, x -> new CanonicalType(x)));
            obj.getMeta().setTag(FPUtil.transform(ent.tag, x -> entityToFhir(x, Coding.class)));
            obj.getMeta().setSecurity(FPUtil.transform(ent.security, x -> entityToFhir(x, Coding.class)));
            obj.setExtension(ent.extension);
            obj.setModifierExtension(ent.modifierExtension);
        }
    }
        
    @SuppressWarnings("unchecked")
    public static <T> T fhirToEntity(Object obj, Class<T> entType) {
        if(obj == null || entType == null) return null;
        
        // List transform
        if(obj instanceof List) {
            return (T) FPUtil.transform((List<Object>) obj, x -> fhirToEntity(x, entType));
        }
        
        // Enumeration
        if(obj instanceof Enumeration) {
            obj = ((Enumeration<?>) obj).getValue();
        }
        
        // Direct assignment
        if(isAssignable(obj.getClass(), entType)) return  (T) obj;
        
        // Via getValue() method
        var getValueMethod = getMethod(obj.getClass(), "getValue");            
        if(getValueMethod != null && isAssignable(getValueMethod.getReturnType(), entType)) {
            try {
                return (T) getValueMethod.invoke(obj);
            } catch (Exception e) {
                throw new RuntimeException("Error:", e);
            }
        }
        
        // Via enum
        if(String.class.equals(entType) && obj.getClass().isEnum()) {
            return (T) codeToString(obj);
        }
        
        // Object transform
        if(isObjectClass(entType)) {
            var ent = newInstance(entType);
            var objFields = getAnnotedFields(obj.getClass());
            
            for(var objField : objFields) {
                String fieldName = objField.getName();
                objField.setAccessible(true);                
                
                var entField = getEntField(entType, fieldName);
                
                var objChild = getFhirFieldValue(obj, fieldName);
                if(objChild == null) continue;
                
                var entChildType = entField.getType();
                
                if(objChild instanceof List) {
                    var entChildListType = (ParameterizedType) entField.getGenericType();
                    entChildType = (Class<?>) (entChildListType.getActualTypeArguments()[0]);                    
                }
                
                var entChild = fhirToEntity(objChild, entChildType);                
                setEntFieldValue(ent, fieldName, entChild);
            }
            
            if(obj instanceof DomainResource && ent instanceof BaseResource) {
                setMetaExt((DomainResource)obj, (BaseResource)ent);
            }
            
            return (T) ent;
        }  
        
        throw new RuntimeException(String.format("Cannot convert fhir class %s  to entity class %s",
                    obj.getClass().getName(), entType.getName()));
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T entityToFhir(Object ent, Class<T> fhirType) {
        if(ent == null || fhirType == null) return null;
        
        // List transform
        if(ent instanceof List) {
            return (T) FPUtil.transform((List<Object>) ent, x -> entityToFhir(x, fhirType));
        }
        
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
                
                var getter = getGetter(fhirType, fieldName);
                var objChildType = getter.getReturnType();
                
                if(entChild instanceof List) {
                    var returnType = getter.getGenericReturnType();
                    
                    while(returnType instanceof ParameterizedType) {
                        returnType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
                    }
                    
                    objChildType = (Class<?>) returnType;
                    var objChildList = (List<Object>) entityToFhir(entChild, objChildType);
                    objChildList.forEach(x -> addFhirFieldValue(obj, fieldName, x));
                }else {                
                    var objChild = entityToFhir(entChild, objChildType);
                    setFhirFieldValue(obj, fieldName, objChild);
                }                                
            }
            
            if(obj instanceof DomainResource && ent instanceof BaseResource) {
                getMetaExt((BaseResource)ent, (DomainResource)obj);
            }
            
            return (T) obj;
        }  
        
        throw new RuntimeException(String.format("Cannot convert entity class %s  to fhir class %s",
                ent.getClass().getName(), fhirType.getName()));
        
    }   
    
}
