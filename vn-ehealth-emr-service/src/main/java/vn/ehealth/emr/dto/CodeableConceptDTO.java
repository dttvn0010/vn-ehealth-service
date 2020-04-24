package vn.ehealth.emr.dto;

import org.hl7.fhir.r4.model.CodeableConcept;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CodeableConceptDTO {

    public String text;
    public String code;
    
    public static CodeableConceptDTO fromFhir(CodeableConcept obj) {
        if(obj == null) return null;
        
        var dto = new CodeableConceptDTO();
        if(obj.hasCoding()) {
            dto.code = obj.getCodingFirstRep().getCode();
            dto.text = obj.getCodingFirstRep().getDisplay();
        }else{
            dto.text = obj.getText();
        }
        
        return dto;
    }
}
