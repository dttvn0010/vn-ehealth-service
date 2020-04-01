package vn.ehealth.emr.dto;

import java.util.Date;

import org.hl7.fhir.r4.model.Identifier;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class IdentifierDTO {

    public String value;
    public String system;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date start;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date end;
    
    public static IdentifierDTO fromFhir(Identifier obj) {
        if(obj == null) return null;
        
        var dto = new IdentifierDTO();
        dto.value = obj.getValue();
        dto.system = obj.getSystem();
        
        if(obj.hasPeriod()) {
            dto.start = obj.getPeriod().getStart();
            dto.end = obj.getPeriod().getEnd();
        }
        
        return dto;
    }
}
