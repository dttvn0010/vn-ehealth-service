package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@JsonInclude(Include.NON_NULL)
public class BaseHumanName extends BaseComplexType {
    public String use;
    public String text;
    public String family;
    public List<String> given;
    public List<String> prefix;
    public List<String> suffix;
    public BasePeriod period;    
    
    public Map<String, Object> getDto(Map<String, Object> options) {
        var simple = false;
        
        if(options != null && options.get("simple") != null) {
            simple = (Boolean) options.get("simple");
        }
        
        if(simple) return mapOf("text", (Object) text);
        
        return mapOf(
                    "text", (Object) text,
                    "family", family,
                    "given", getFirst(given),
                    "prefix", getFirst(prefix),
                    "sufix", getFirst(suffix)                    
                );
    }
    
    public static Map<String, Object> toDto(BaseHumanName name, Map<String, Object> options) {
        if(name == null) return null;
        return name.getDto(options);
    }
    
    public static Map<String, Object> toDto(BaseHumanName name) {
        return toDto(name, null);
    }
}
