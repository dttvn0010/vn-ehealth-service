package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.hl7.fhir.r4.model.Base64BinaryType;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.MarkdownType;
import org.hl7.fhir.r4.model.PositiveIntType;
import org.hl7.fhir.r4.model.PrimitiveType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.TimeType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.UnsignedIntType;
import org.hl7.fhir.r4.model.UriType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BasePrimitiveType extends BaseSimpleType {

    public Object value;
    public String className;
    
    public BasePrimitiveType() {
        
    }
    
    public BasePrimitiveType(PrimitiveType<?> obj) {
        if(obj != null) {
            this.value = obj.getValue();
            this.className = obj.getClass().getName();
        }
    }
    
    public static boolean isSupported(Object val) {
        if(val instanceof Short) return true;
        if(val instanceof Integer) return true;
        if(val instanceof Long) return true;
        if(val instanceof Float) return true;
        if(val instanceof Double) return true;
        if(val instanceof Boolean) return true;
        if(val instanceof String) return true;
        if(val instanceof BigDecimal) return true;
        if(val instanceof Date) return true;
        if(val instanceof byte[]) return true;
        return false;
    }
    
    public Type toFhir() {
        if(value == null) return null;
        
        if(IntegerType.class.getName().equals(className)) {
            return new IntegerType((int) value);
        }
        
        if(UnsignedIntType.class.getName().equals(className)) {
            return new UnsignedIntType((int) value);
        }
        
        if(PositiveIntType.class.getName().equals(className)) {
            return new PositiveIntType((int) value);
        }
        
        if(DecimalType.class.getName().equals(className)) {
            if(value instanceof String) {
                System.out.println("??????===========================" + value + "," + className);
                return new DecimalType(Double.valueOf((String) value));
            }
            return new DecimalType((BigDecimal) value);
        }
        
        if(BooleanType.class.getName().equals(className)) {
            return new BooleanType((boolean) value);
        }
        
        if(StringType.class.getName().equals(className)) {
            return new StringType((String) value);
        }
        
        if(UriType.class.getName().equals(className)) {
            return new UriType((String) value);
        }
        
        if(CanonicalType.class.getName().equals(className)) {
            return new UriType((String) value);
        }
        
        if(MarkdownType.class.getName().equals(className)) {
            return new MarkdownType((String) value);
        }
        
        if(CodeType.class.getName().equals(className)) {
            return new CodeType((String) value);
        }
        
        if(TimeType.class.getName().equals(className)) {
            return new TimeType((String) value);
        }
        
        if(DateType.class.getName().equals(className)) {
            return new DateType((Date) value);
        }
        
        if(DateTimeType.class.getName().equals(className)) {
            return new DateTimeType((Date) value);
        }
        
        if(InstantType.class.getName().equals(className)) {
            return new InstantType((Date) value);
        }
        
        if(Base64BinaryType.class.getName().equals(className)) {
            return new Base64BinaryType((byte[]) value);
        }
        
        throw new RuntimeException("Do not know to handle type: " + className);
    }
}
