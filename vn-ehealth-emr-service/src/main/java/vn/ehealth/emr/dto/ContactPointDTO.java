package vn.ehealth.emr.dto;

import org.hl7.fhir.r4.model.ContactPoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ContactPointDTO {

    public String value;
    public String system;
    
    public static ContactPointDTO fromFhir(ContactPoint obj) {
        if(obj == null) return null;
        
        var dto = new ContactPointDTO();
        dto.value = obj.getValue();
        
        if(obj.hasSystem()) {
            dto.system = obj.getSystem().toCode();
        }
        
        return dto;
    }
}
