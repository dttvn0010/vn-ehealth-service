package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.*;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

public class SimpleExtension {
    
    static Map<Class<?>, Class<?>> mapClasses = new HashMap<Class<?>, Class<?>>() {
        private static final long serialVersionUID = 1L;
        {
            put(Coding.class, BaseCoding.class);            
            put(CodeableConcept.class, BaseCodeableConcept.class);
            put(Period.class, BasePeriod.class);
            put(Quantity.class, BaseQuantity.class);
            put(Ratio.class, BaseRatio.class);
            put(Range.class, BaseRange.class);
        }
    };
    
    static Type primitiveToFhir(Object val) {
        if(val == null) return null;
        
        if(val instanceof Integer || val instanceof Short) {
            return new IntegerType((Integer)val);
            
        }else if(val instanceof Long 
                || val instanceof Float 
                || val instanceof Double
                || val instanceof BigDecimal) {
            
            return new DecimalType((BigDecimal)val);
            
        }else if(val instanceof Boolean) {            
            return new BooleanType((Boolean) val);
            
        }else if(val instanceof String) {
            return new StringType((String) val);
            
        }else if(val instanceof Date) {
            return new DateTimeType((Date) val);            
        }
        
        for(var entry : mapClasses.entrySet()) {
            var fhirClass = entry.getKey();
            var baseClass = entry.getValue();
            if(baseClass.equals(val.getClass())) {
                return (Type) DataConvertUtil.entityToFhir(val, fhirClass);
            }
        }
        
        throw new RuntimeException("Unsupport data type :" + val.getClass().getName());
    }
    
    private static Object getPrimitiveValue(Type fhirObj) {
        if(fhirObj == null) return null;
        
        if(fhirObj instanceof PrimitiveType<?>) {
            var val = ((PrimitiveType<?>) fhirObj).getValue();
            
            if(val instanceof Integer || val instanceof Short) {
                return ((Integer) val);
                
            }else if(val instanceof Long 
                    || val instanceof Float 
                    || val instanceof Double
                    || val instanceof BigDecimal) {
                
                return (BigDecimal)val;
                
            }else if(val instanceof Boolean) {            
                return (Boolean) val;
                
            }else if(val instanceof String) {
                return (String) val;
                
            }else if(val instanceof Date) {
                return (Date) val;            
            }
            throw new RuntimeException("Unsupport data type:" + val.getClass().getName());
        }
        
        for(var entry : mapClasses.entrySet()) {
            var fhirClass = entry.getKey();
            var baseClass = entry.getValue();
            if(fhirClass.equals(fhirObj.getClass())) {
                return DataConvertUtil.fhirToEntity(fhirObj, baseClass);
            }
        }
        
        throw new RuntimeException("Unsupport data type :" + fhirObj.getClass().getName());
    }
    
    public static class RawExtension{
        public String url;
        public Object value;
        
        public RawExtension() {
            
        }
        
        public RawExtension(Extension extension) {
            if(extension != null) {
                this.url = extension.getUrl();
                this.value = getPrimitiveValue(extension.getValue());
            }
        }
    }   

    public String url;
    public Object value;
    public List<RawExtension> extension;    
    
    
    public static SimpleExtension fromExtension(Extension obj) {
        var ent = new SimpleExtension();
        ent.url = obj.getUrl();
        
        if(obj.hasValue()) {
            ent.value = getPrimitiveValue(obj.getValue());
        }
        
        if(obj.hasExtension()) {
            ent.extension = FPUtil.transform(obj.getExtension(), x -> new RawExtension(x));
        }
        return ent;
    }
    
    public static Extension toExtension(SimpleExtension ent) {
        if(ent == null) return null;
        var obj = new Extension();
        obj.setUrl(ent.url);
        
        if(ent.value != null) {            
            var objValue = primitiveToFhir(ent.value);
            obj.setValue(objValue);
        }
        
        if(ent.extension != null) {
            for(var item : ent.extension) {
                var itemExt = new Extension();
                itemExt.setUrl(item.url);
                
                if(item.value != null) {
                    var itemObjValue = primitiveToFhir(item.value);
                    itemExt.setValue(itemObjValue);
                }
                
                obj.addExtension(itemExt);
            }
        }
        return obj;
    }
    
}
