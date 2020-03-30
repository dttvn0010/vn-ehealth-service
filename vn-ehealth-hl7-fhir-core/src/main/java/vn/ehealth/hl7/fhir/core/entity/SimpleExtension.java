package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;
import org.hl7.fhir.r4.model.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.FPUtil;

@JsonInclude(Include.NON_NULL)
public class SimpleExtension {
    
    @JsonInclude(Include.NON_NULL)
    public static class RawExtension{
        public String url;
        public BaseSimpleType value;
        
        public RawExtension() {
            
        }
        
        public RawExtension(Extension extension) {
            if(extension != null && extension.hasUrl()) {
                this.url = extension.getUrl();                
            }
            
            if(extension != null && extension.hasValue()) {
                this.value = BaseSimpleType.fromFhir(extension.getValue());
            }
        }
    }   

    public String url;
    public BaseSimpleType value;
    public List<RawExtension> extension;    
    
    
    public static SimpleExtension fromExtension(Extension obj) {
        if(obj == null) return null;
        
        var ent = new SimpleExtension();
        
        if(obj.hasUrl()) {
            ent.url = obj.getUrl();
        }
        
        if(obj.hasValue()) {
            ent.value = BaseSimpleType.fromFhir(obj.getValue());
        }
        
        if(obj.hasExtension()) {
            ent.extension = FPUtil.transform(obj.getExtension(), x -> new RawExtension(x));
        }
        return ent;
    }
    
    public static Extension toExtension(SimpleExtension ent) {
        if(ent == null) return null;
        var obj = new Extension();
        obj.setUrl(ent.url);
        
        if(ent.value != null) {            
            obj.setValue(BaseSimpleType.toFhir(ent.value));
        }
        
        if(ent.extension != null) {
            for(var item : ent.extension) {
                var itemExt = new Extension();
                itemExt.setUrl(item.url);
                
                if(item.value != null) {
                    itemExt.setValue(BaseSimpleType.toFhir(item.value));
                }
                
                obj.addExtension(itemExt);
            }
        }
        return obj;
    }
    
}
