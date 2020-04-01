package vn.ehealth.emr.dto;

import org.hl7.fhir.r4.model.HumanName;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class HumanNameDTO {

    public String fullname;
        
    public static HumanNameDTO fromFhir(HumanName obj) {
        if(obj == null) return null;
        
        var dto = new HumanNameDTO();
        dto.fullname = obj.getText();
        
        return dto;
    }
}
