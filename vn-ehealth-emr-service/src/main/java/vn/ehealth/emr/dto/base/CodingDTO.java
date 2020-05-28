package vn.ehealth.emr.dto.base;

import org.hl7.fhir.r4.model.CodeableConcept;

import vn.ehealth.hl7.fhir.core.util.FhirUtil;

public class CodingDTO {

	public String code;
	public String display;
	
	public static CodingDTO fromText(String text) {
	    if(text != null && text.contains("|")) {
	        var dto = new CodingDTO();
	        String[] arr = text.split("\\|");
	        dto.display = arr[0];
	        dto.code = arr[1].replace("(", "").replace(")", "");
	        return dto;
	    }
	    return null;
	}
	
	public static CodeableConcept toCodeableConcept(CodingDTO dto, String codeSystemUrl) {
		if(dto == null) return null;
		return FhirUtil.createCodeableConcept(dto.code, dto.display, codeSystemUrl);
	}
}
