package vn.ehealth.hl7.fhir.core.entity;

import org.hl7.fhir.r4.model.Range;

public class BaseRange {
    public BaseQuantity low;
    public BaseQuantity high;
   
    public static BaseRange fromRange(Range range) {
        if(range == null) return null;
        
        var entity = new BaseRange();
        entity.low = BaseQuantity.fromQuantity(range.getLow());
        entity.high = BaseQuantity.fromQuantity(range.getHigh());
        return entity;
    }
    
    public static Range toRange(BaseRange entity) {
        if(entity == null) return null;
        
        var range = new Range();
        range.setLow(BaseQuantity.toQuantity(entity.low));
        range.setHigh(BaseQuantity.toQuantity(entity.high));
        return range;
    }

}
