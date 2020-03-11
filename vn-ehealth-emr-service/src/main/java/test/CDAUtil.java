package test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.openhealthtools.mdht.uml.cda.Act;
import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.Entry;
import org.openhealthtools.mdht.uml.cda.EntryRelationship;
import org.openhealthtools.mdht.uml.cda.Section;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.CE;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.emr.utils.EmrUtils;

public class CDAUtil {
    private static Logger logger = LoggerFactory.getLogger(CDAUtil.class);
    
    static CDAFactory cdaFactory =  CDAFactory.eINSTANCE;
    static DatatypesFactory dataFactory = DatatypesFactory.eINSTANCE;
    static CDATemplate cdaTemplate;
    
    static {
        try {
            ObjectMapper mapper = EmrUtils.createObjectMapper();
            var file = new ClassPathResource("static/json/cda_template.json").getInputStream();
            var jsonSt = new String(file.readAllBytes());
            file.close();
            
            cdaTemplate =  mapper.readValue(jsonSt, CDATemplate.class);
        }catch(Exception e) {
            logger.error("Fail to load CDA template:", e);
        }
    }
    
    static Optional<Method> findMethod(Class<?> cl, String methodName) {
        return Arrays.stream(cl.getMethods())
                    .filter(x -> methodName.equals(x.getName()) && x.getParameterCount() == 0)
                    .findFirst();
    }
    
    public static Object createObject(String dataType) throws ReflectiveOperationException {
        var methodName = "create" + dataType;
        
        var method = findMethod(DatatypesFactory.class, methodName);
        if(method.isPresent()) {
            return method.get().invoke(dataFactory);
        }
        
        method = findMethod(CDAFactory.class, methodName);
        if(method.isPresent()) {
            return method.get().invoke(cdaFactory);
        }        
        
        return null;
    }
    
    public static Object createObject(Class<?> cl) throws ReflectiveOperationException {
        var className = cl.getName();
        var dataType = className.substring(className.lastIndexOf(".") + 1);
        return createObject(dataType);
    }
    
    static Method getGetMethod(Class<?> cl, String field) throws ReflectiveOperationException {
        var methodName = "get" + field.substring(0,1).toUpperCase() + field.substring(1);
        return cl.getMethod(methodName);
    }
    
    static Method getSetMethod(Class<?>cl, String field, Class<?> fieldClass) throws ReflectiveOperationException {        
        var methodName = "set" + field.substring(0,1).toUpperCase() + field.substring(1);
        return cl.getMethod(methodName, fieldClass);        
    }
    
    static @Nonnull Object getFieldSafe(Object obj, String field) throws ReflectiveOperationException {
        Method getMethod = getGetMethod(obj.getClass(), field);
        var fieldClass = getMethod.getReturnType();
        var fieldValue = getMethod.invoke(obj);
        if(fieldValue == null) {
            fieldValue = createObject(fieldClass);
            var setMethod = getSetMethod(obj.getClass(), field, fieldClass);
            setMethod.invoke(obj, fieldValue);                
        }
        return fieldValue;
    }
    
    static void setField(@Nonnull Object obj, String field, Object value) throws ReflectiveOperationException {
        var getMethod = getGetMethod(obj.getClass(), field);
        var fieldClass = getMethod.getReturnType();
        var setMethod = getSetMethod(obj.getClass(), field, fieldClass);
        setMethod.invoke(obj, value);
        
    }
    
    public static void setProperty(@Nonnull Object obj, String property, Object value) throws ReflectiveOperationException {
        var fields = property.split("\\.");
        if(fields.length > 0) {
            for(int i = 0; i < fields.length - 1; i++) {
                obj = getFieldSafe(obj, fields[i]);
            }
            
            setField(obj, fields[fields.length-1], value);
        }
    }    
    
    public static String getValue(String element ) {
    	return "";
    }

    public static String getTemplateId(String element) {
    	return getValue(element + ".templateId.root");    
    }
    
    public static II getII(String element) {
    	var root =  getValue(element + ".root");
    	var extension = getValue(element + ".extension");
    	return DataUtil.createII(root, extension);
    }
    
    public static CE getCodeCE(String element) {
    	var code = getValue(element + ".code.code");
    	var codeSystem = getValue(element + ".code.codeSystem");
    	var codeSystemName = getValue(element + ".code.codeSystemName");
    	var displayName = getValue(element + ".code.displayName");
    	return DataUtil.createCE(code, codeSystem, codeSystemName, displayName);
    }
    
    public static CD getCodeCD(String element) {
    	var code = getValue(element + ".code.code");
    	var codeSystem = getValue(element + ".code.codeSystem");
    	var codeSystemName = getValue(element + ".code.codeSystemName");
    	var displayName = getValue(element + ".code.displayName");
    	return DataUtil.createCD(code, codeSystem, codeSystemName, displayName);
    }
    
    public static List<Entry> getListEntryByActTemplateIds(Section section, @Nonnull String actTemplateId) {
    	var lst = new ArrayList<Entry>();
    	
		if(section != null && section.getEntries() != null) {
			for(var entry : section.getEntries()) {
				var templateId = Optional.ofNullable(entry)
						.map(x -> x.getAct())
						.map(x -> x.getTemplateIds())
						.map(x -> x.size() > 0? x.get(0):null)
						.map(x -> x.getRoot())
						.orElse("");
				if(actTemplateId.equals(templateId)) {
					lst.add(entry);
				}
			}
		}
		return lst;
	}
    
    public static Entry getEntryByActTemplateIds(Section section, @Nonnull String actTemplateId) {
    	var lst = getListEntryByActTemplateIds(section, actTemplateId);
    	return lst.size() > 0? lst.get(0): null;    	
    }
    
    public static List<EntryRelationship> getListEntryRelationshipByObsTemplateIds(Act act, @Nonnull String obsTemplateIds) {
		var lst = new ArrayList<EntryRelationship>();
		if(act != null && act.getEntryRelationships() != null) {
			for(var entryRelationship : act.getEntryRelationships()) {
				var templateId = Optional.ofNullable(entryRelationship)
										.map(x -> x.getObservation())
										.map(x -> x.getTemplateIds())
										.map(x -> x.size() > 0? x.get(0): null)
										.map(x -> x.getRoot())
										.orElse("");
				
				if(obsTemplateIds.equals(templateId)) {
					lst.add(entryRelationship);
				}				
			}			
		}
		return lst;
	}
    
    public static EntryRelationship getEntryRelationshipByObsTemplateIds(Act act, @Nonnull String obsTemplateIds) {
    	var lst = getListEntryRelationshipByObsTemplateIds(act, obsTemplateIds);
    	return lst.size() > 0? lst.get(0): null;
    }
}
