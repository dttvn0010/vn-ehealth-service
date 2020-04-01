package vn.ehealth.emr.dto;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Reference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ReferenceDTO {

    public String display;
    public String reference;
    public BaseDTO resourceDTO;
    
    public static ReferenceDTO fromFhir(Reference obj) {
        if(obj == null) return null;
        
        var dto = new ReferenceDTO();
        
        dto.display = obj.getDisplay();
        dto.reference = obj.getReference();
        
        if(obj.getResource() != null) {
            dto.resourceDTO = BaseDTO.fromResource((DomainResource)obj.getResource());
        }
        
        return dto;
    }
}
