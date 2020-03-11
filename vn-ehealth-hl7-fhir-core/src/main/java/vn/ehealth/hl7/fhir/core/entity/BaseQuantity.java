package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;

import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Quantity.QuantityComparator;

public class BaseQuantity {
    public Number value;
    public String comparator;
    public String unit;
    public String system;
    public String code;
  
    public static BaseQuantity fromQuantity(Quantity object) {
        if(object == null) return null;
        
        var entity = new BaseQuantity();
        entity.value = object.getValue();
        if(object.getComparator() != null) {
            entity.comparator = object.getComparator().toCode();
        }
        entity.unit = object.getUnit();
        entity.system = object.getSystem();
        entity.code = object.getCode();
        return entity;
    }
    
    public static Quantity toQuantity(BaseQuantity entity) {
        if(entity == null) return null;
        
        var object = new Quantity();
        object.setValue((BigDecimal) entity.value);
        object.setComparator(QuantityComparator.fromCode(entity.comparator));
        object.setUnit(entity.unit);
        object.setSystem(entity.system);
        object.setCode(entity.code);
        
        return object;
    }
}
