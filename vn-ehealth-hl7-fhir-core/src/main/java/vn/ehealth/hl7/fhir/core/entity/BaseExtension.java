package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

public class BaseExtension {
    public String url;
    @JsonIgnore public Type value;
    public static BaseExtension fromExtension(Extension obj) {
        if(obj == null) return null;
        var ent = new BaseExtension();
        ent.url = obj.hasUrl()? obj.getUrl() : null;
        ent.value = obj.hasValue()? obj.getValue(): null;
        return ent;
    }
    
    public static List<BaseExtension> fromExtensionList(List<Extension> lst) {
        return DataConvertUtil.transform(lst, x -> fromExtension(x));
    }
    
    public static Extension toExtension(BaseExtension ent) {
        if(ent == null) return null;
        var obj = new Extension();
        obj.setUrl(ent.url);
        obj.setValue(ent.value);
        return obj;
    }
    
    public static List<Extension> toExtensionList(List<BaseExtension> entLst) {
        return DataConvertUtil.transform(entLst, x -> toExtension(x));
    }
}
