package vn.ehealth.hl7.fhir.core.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.*;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

public class BaseExtension {

    private static Map<Class<?>, Class<?>> mapClasses = new HashMap<Class<?>, Class<?>>() {
        private static final long serialVersionUID = 1L;
        {
            put(Coding.class, BaseCoding.class);            
            put(CodeableConcept.class, BaseCodeableConcept.class);
            put(Period.class, BasePeriod.class);
            put(Quantity.class, BaseQuantity.class);
            put(Ratio.class, BaseRatio.class);
            put(Range.class, BaseRange.class);
            
            put(Address.class, BaseAddress.class);            
            put(Annotation.class, BaseAnnotation.class);
            put(Attachment.class, BaseAttachment.class);
            put(CodeableConcept.class, BaseCodeableConcept.class);
            put(Coding.class, BaseCoding.class);
            put(ContactPoint.class, BaseContactPoint.class);
            put(Dosage.class, BaseDosage.class);
            put(Duration.class, BaseDuration.class);
            put(HumanName.class, BaseHumanName.class);
            put(Identifier.class, BaseIdentifier.class);
            put(Period.class, BasePeriod.class);
            put(Quantity.class, BaseQuantity.class);
            put(Range.class, BaseRange.class);
            put(Ratio.class, BaseRatio.class);
            put(Reference.class, BaseReference.class);
            put(Timing.class, BaseTiming.class);
            put(UsageContext.class, UsageContext.class);
            
        }        
    };
    
    public static BaseType getBaseValue(Type fhirObj) {
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
                return (BaseType) DataConvertUtil.fhirToEntity(fhirObj, baseClass);                
            }
        }
        
        throw new RuntimeException("Unsupport data type :" + fhirObj.getClass().getName());
    }
    
    public static Type baseValueToFhir(BaseType val) {
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
    
    
    public static class RawExtension{
        public String url;
        public BaseType value;
        
        public RawExtension() {
            
        }
        
        public RawExtension(Extension extension) {
            if(extension != null) {
                this.url = extension.getUrl();
                this.value = getBaseValue(extension.getValue());
            }
        }
    }
    
    public String url;
    public BaseType value;
    public List<RawExtension> extension;
    
    
    public static BaseExtension fromExtension(Extension obj) {
        var ent = new BaseExtension();
        ent.url = obj.getUrl();
        
        if(obj.hasValue()) {
            ent.value = getBaseValue(obj.getValue());
        }
        
        if(obj.hasExtension()) {
            ent.extension = FPUtil.transform(obj.getExtension(), x -> new RawExtension(x));
        }
        return ent;
    }
    
    public static Extension toExtension(BaseExtension ent) {
        if(ent == null) return null;
        var obj = new Extension();
        obj.setUrl(ent.url);
        obj.setValue(baseValueToFhir(ent.value));
        
        if(ent.extension != null) {
            for(var item : ent.extension) {
                var itemExt = new Extension();
                itemExt.setUrl(item.url);
                itemExt.setValue(baseValueToFhir(item.value));
                obj.addExtension(itemExt);
            }
        }
        return obj;
    }
}
