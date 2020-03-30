package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;
import org.hl7.fhir.r4.model.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.FPUtil;

@JsonInclude(Include.NON_NULL)
public class BaseExtension {
    
    @JsonInclude(Include.NON_NULL)
    public static class RawExtension{
        public String url;
        public BaseType value;
        
        public RawExtension() {
            
        }
        
        public RawExtension(Extension extension) {
            if(extension != null) {
                this.url = extension.getUrl();
                this.value = BaseType.fromFhir(extension.getValue());
            }
        }
    }
    
    public String url;
    public BaseType value;
    public List<RawExtension> extension;
    
    
    public static BaseExtension fromExtension(Extension obj) {
        var ent = new BaseExtension();
        ent.url = obj.getUrl();
        
        if(obj.hasValue()) {
            ent.value = BaseType.fromFhir(obj.getValue());
        }
        
        if(obj.hasExtension()) {
            ent.extension = FPUtil.transform(obj.getExtension(), x -> new RawExtension(x));
        }
        return ent;
    }
    
    public static Extension toExtension(BaseExtension ent) {
        if(ent == null) return null;
        var obj = new Extension();
        obj.setUrl(ent.url);
        obj.setValue(BaseType.toFhir(ent.value));
        
        if(ent.extension != null) {
            for(var item : ent.extension) {
                var itemExt = new Extension();
                itemExt.setUrl(item.url);
                itemExt.setValue(BaseType.toFhir(item.value));
                obj.addExtension(itemExt);
            }
        }
        return obj;
    }
}
