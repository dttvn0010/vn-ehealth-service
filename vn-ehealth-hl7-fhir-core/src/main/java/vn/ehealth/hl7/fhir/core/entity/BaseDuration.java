package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;

import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.Quantity.QuantityComparator;

public class BaseDuration extends BaseQuantity {

    public static BaseDuration fromDuration(Duration obj) {
        if(obj == null) return null;
        
        var ent = new BaseDuration();
        ent.value = obj.hasValue()? obj.getValue() : null;
        ent.comparator = obj.hasComparator()? obj.getComparator().toCode() : null;
        ent.unit = obj.hasUnit()? obj.getUnit() : null;
        ent.system = obj.hasSystem()? obj.getSystem() : null;
        ent.code = obj.hasCode()? obj.getCode() : null;
        ent.extension = obj.hasExtension()? obj.getExtension() : null;
        return ent;
    }
    
    public static Duration toDuration(BaseDuration entity) {
        if(entity == null) return null;
        
        var object = new Duration();
        object.setValue((BigDecimal) entity.value);
        object.setComparator(QuantityComparator.fromCode(entity.comparator));
        object.setUnit(entity.unit);
        object.setSystem(entity.system);
        object.setCode(entity.code);
        object.setExtension(entity.extension);
        return object;
    }
}
