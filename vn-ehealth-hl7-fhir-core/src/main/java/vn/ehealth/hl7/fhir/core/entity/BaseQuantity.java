package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;

import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Quantity.QuantityComparator;

public class BaseQuantity {
    public BigDecimal value;
    public String comparator;
    public String unit;
    public String system;
    public String code;
  
    public static BaseQuantity fromQuantity(Quantity obj) {
        if(obj == null) return null;
        
        var ent = new BaseQuantity();
        ent.value = obj.hasValue()? obj.getValue() : null;
        ent.comparator = obj.hasComparator()? obj.getComparator().toCode() : null;
        ent.unit = obj.hasUnit()? obj.getUnit() : null;
        ent.system = obj.hasSystem()? obj.getSystem() : null;
        ent.code = obj.hasCode()? obj.getCode() : null;
        return ent;
    }
    
    public static Quantity toQuantity(BaseQuantity entity) {
        if(entity == null) return null;
        
        var object = new Quantity();
        object.setValue(entity.value);
        object.setComparator(QuantityComparator.fromCode(entity.comparator));
        object.setUnit(entity.unit);
        object.setSystem(entity.system);
        object.setCode(entity.code);
        
        return object;
    }
}
