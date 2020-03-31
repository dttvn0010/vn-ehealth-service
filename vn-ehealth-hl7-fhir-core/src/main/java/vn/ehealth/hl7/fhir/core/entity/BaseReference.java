package vn.ehealth.hl7.fhir.core.entity;

import java.util.Map;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@JsonInclude(Include.NON_NULL)
public class BaseReference extends BaseComplexType {
	public String reference;
	public String type;
	public BaseIdentifier identifier;
	public String display;
	
	@Transient
	public BaseResource resource;
	
    public Map<String, Object> getDto(Map<String, Object> options) {
        var dto = mapOf(
                "reference", (Object) reference
            );
        
        if(resource != null) {
            dto.put("resource", resource.getDto(options));
        }
        return dto;
    }
	
	public static Map<String, Object> toDto(BaseReference ref, Map<String, Object> options) {
	    if(ref == null) return null;
	    return ref.getDto(options);
	}
	
	public static Map<String, Object> toDto(BaseReference ref) {
	    return toDto(ref, null);
	}
}
