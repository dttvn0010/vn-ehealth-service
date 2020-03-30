package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BasePrimitiveType extends BaseSimpleType {

    public Object value;
    
    public BasePrimitiveType() {
        
    }
    
    public BasePrimitiveType(Object value) {
        this.value = value;
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
        return false;
    }
    
    public Type toFhir() {
        if(value == null) return null;
        
        if(value instanceof Integer || value instanceof Short) {
            return new IntegerType((Integer)value);
            
        }else if(value instanceof Long 
                || value instanceof Float 
                || value instanceof Double
                || value instanceof BigDecimal) {
            
            return new DecimalType((BigDecimal)value);
            
        }else if(value instanceof Boolean) {            
            return new BooleanType((Boolean) value);
            
        }else if(value instanceof String) {
            return new StringType((String) value);
            
        }else if(value instanceof Date) {
            return new DateTimeType((Date) value);            
        }
        
        throw new RuntimeException("Unsupported data type: " + value.getClass().getName());
    }
}
