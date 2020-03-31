package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@JsonInclude(Include.NON_NULL)
public class BaseCodeableConcept  extends BaseSimpleType  {

    public List<BaseCoding> coding;
    public String text;
    
    public Map<String, Object> getDto(Map<String, Object> options) {
        String display = text, code = "";
        if(coding != null && coding.size() > 0) {
            code = coding.get(0).code;
            display = coding.get(0).display;
        }
        return mapOf("display", (Object) display, "code", code);
    }
    
    public static Map<String, Object> toDto(BaseCodeableConcept ent, Map<String, Object> options) {
        if(ent == null) return null;        
        return ent.getDto(options);
    }
    
    public static Map<String, Object> toDto(BaseCodeableConcept ent) {
        return toDto(ent, null);
    }
}
