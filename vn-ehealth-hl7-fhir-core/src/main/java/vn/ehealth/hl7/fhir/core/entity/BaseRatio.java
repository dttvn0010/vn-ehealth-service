package vn.ehealth.hl7.fhir.core.entity;

import org.hl7.fhir.r4.model.Ratio;

public class BaseRatio {

    public BaseQuantity numerator;
    public BaseQuantity denominator;
    
    public static BaseRatio fromRation(Ratio obj) {
        if(obj == null) return null;
        
        var ent = new BaseRatio();
        
        if(obj.hasNumerator())
            ent.numerator = BaseQuantity.fromQuantity(obj.getNumerator());
        
        if(obj.hasDenominator())
            ent.denominator = BaseQuantity.fromQuantity(obj.getDenominator());
        
        return ent;
    }
    
    public static Ratio toRatio(BaseRatio ent) {
        if(ent == null) return null;        
        var obj = new Ratio();
        obj.setNumerator(BaseQuantity.toQuantity(ent.numerator));
        obj.setDenominator(BaseQuantity.toQuantity(ent.denominator));
        return obj;
    }
}
