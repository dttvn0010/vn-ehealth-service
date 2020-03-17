package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Range;

public class BaseRange {
    public BaseQuantity low;
    public BaseQuantity high;
    public List<Extension> extension;
   
    public static BaseRange fromRange(Range obj) {
        if(obj == null) return null;
        
        var ent = new BaseRange();
        ent.low = obj.hasLow()? BaseQuantity.fromQuantity(obj.getLow()) : null;
        ent.high = obj.hasHigh()? BaseQuantity.fromQuantity(obj.getHigh()) : null;
        ent.extension = obj.hasExtension()? obj.getExtension() : null;
        return ent;
    }
    
    public static Range toRange(BaseRange ent) {
        if(ent == null) return null;
        
        var obj = new Range();
        obj.setLow(BaseQuantity.toQuantity(ent.low));
        obj.setHigh(BaseQuantity.toQuantity(ent.high));
        obj.setExtension(ent.extension);
        return obj;
    }

}
