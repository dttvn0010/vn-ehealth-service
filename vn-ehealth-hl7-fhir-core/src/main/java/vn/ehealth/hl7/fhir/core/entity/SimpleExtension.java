package vn.ehealth.hl7.fhir.core.entity;

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
    
    public static Type primitiveToFhir(BaseType val) {
        if(val == null) return null;
        
        if(val instanceof BasePrimitiveType) {
            return ((BasePrimitiveType)val).toFhir();
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
    
    public static BaseSimpleType getPrimitiveValue(Type fhirObj) {
        if(fhirObj == null) return null;
        
        if(fhirObj instanceof PrimitiveType<?>) {
            var val = ((PrimitiveType<?>) fhirObj).getValue();
            
            if(BasePrimitiveType.isSupported(val)) {
                return new BasePrimitiveType(val);
            }
            
            throw new RuntimeException("Unsupport data type:" + val.getClass().getName());
        }
        
        for(var entry : mapClasses.entrySet()) {
            var fhirClass = entry.getKey();
            var baseClass = entry.getValue();
            if(fhirClass.equals(fhirObj.getClass())) {
                return (BaseSimpleType) DataConvertUtil.fhirToEntity(fhirObj, baseClass);
            }
        }
        
        throw new RuntimeException("Unsupport data type :" + fhirObj.getClass().getName());
    }
    
    public static class RawExtension{
        public String url;
        public BaseSimpleType value;
        
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
    public BaseSimpleType value;
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
