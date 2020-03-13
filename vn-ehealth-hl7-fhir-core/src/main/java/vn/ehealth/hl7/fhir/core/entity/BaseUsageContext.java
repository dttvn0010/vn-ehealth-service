package vn.ehealth.hl7.fhir.core.entity;

import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.UsageContext;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseUsageContext {

    public BaseCoding code;
    @JsonIgnore public Type value;
    
    public static BaseUsageContext fromUsageContext(UsageContext obj) {
        if(obj == null) return null;
        var ent = new BaseUsageContext();
        ent.code = obj.hasCode()? BaseCoding.fromCoding(obj.getCode()) : null;
        ent.value = obj.hasValue()? obj.getValue() : null;
        return ent;
    }
    
    public static UsageContext toUsageContext(BaseUsageContext ent) {
        if(ent == null) return null;
        var obj = new UsageContext();
        obj.setCode(BaseCoding.toCoding(ent.code));
        obj.setValue(ent.value);
        return obj;
    }
}
