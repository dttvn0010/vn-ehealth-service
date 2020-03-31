package vn.ehealth.hl7.fhir.core.entity;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.view.DTOView;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@JsonInclude(Include.NON_NULL)
public class BaseReference extends BaseComplexType {
	public String reference;
	public String type;
	public BaseIdentifier identifier;
	public String display;
	
	public BaseResource resource;
	
	@JsonView(DTOView.class)
    public Map<String, Object> getDto() {
        var dto = mapOf(
                "reference", (Object) reference,
                "type", type,
                "display", display
            );
        
        if(resource != null) {
            dto.put("resource", resource.getDto());
        }
        return dto;
    }
	
	public static Map<String, Object> toDto(BaseReference ref) {
	    if(ref == null) return null;
	    return ref.getDto();
	}
}
