package vn.ehealth.hl7.fhir.core.entity;

import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.r4.model.Age;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.PrimitiveType;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Range;
import org.hl7.fhir.r4.model.Ratio;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

@JsonInclude(Include.NON_NULL)
public class BaseSimpleType extends BaseType {

    static Map<Class<?>, Class<?>> mapClasses = new HashMap<Class<?>, Class<?>>() {
        private static final long serialVersionUID = 1L;
        {
            put(Coding.class, BaseCoding.class);            
            put(CodeableConcept.class, BaseCodeableConcept.class);
            put(Period.class, BasePeriod.class);
            put(Quantity.class, BaseQuantity.class);
            put(Ratio.class, BaseRatio.class);
            put(Range.class, BaseRange.class);
            put(Reference.class, BaseReference.class);
            put(Duration.class, BaseDuration.class);
            put(Age.class, BaseAge.class);
        }
    };
    
    public static Type toFhir(BaseSimpleType val) {
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
    
    public static BaseSimpleType fromFhir(Type fhirObj) {
        if(fhirObj == null) return null;
        
        if(fhirObj instanceof PrimitiveType<?>) {
            var primitiveObj = (PrimitiveType<?>) fhirObj; 

            if(BasePrimitiveType.isSupported(primitiveObj.getValue())) {
                return new BasePrimitiveType(primitiveObj);
            }
            
            throw new RuntimeException("Unsupport data type:" + fhirObj.getClass().getName());
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
    
}
