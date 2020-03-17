package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Ratio;

public class BaseRatio {

    public BaseQuantity numerator;
    public BaseQuantity denominator;
    public List<Extension> extension;
    
    public static BaseRatio fromRation(Ratio obj) {
        if(obj == null) return null;
        
        var ent = new BaseRatio();
        
        if(obj.hasNumerator())
            ent.numerator = BaseQuantity.fromQuantity(obj.getNumerator());
        
        if(obj.hasDenominator())
            ent.denominator = BaseQuantity.fromQuantity(obj.getDenominator());
        
        if(obj.hasExtension())
            ent.extension = obj.getExtension();
        
        return ent;
    }
    
    public static Ratio toRatio(BaseRatio ent) {
        if(ent == null) return null;        
        var obj = new Ratio();
        obj.setNumerator(BaseQuantity.toQuantity(ent.numerator));
        obj.setDenominator(BaseQuantity.toQuantity(ent.denominator));
        obj.setExtension(ent.extension);
        return obj;
    }
}
