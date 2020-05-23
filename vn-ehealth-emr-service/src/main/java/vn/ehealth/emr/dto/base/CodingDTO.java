package vn.ehealth.emr.dto.base;

import org.hl7.fhir.r4.model.CodeableConcept;

import vn.ehealth.hl7.fhir.core.util.FhirUtil;

public class CodingDTO {

	public String code;
	public String display;
	
	public static CodeableConcept toCodeableConcept(CodingDTO dto, String codeSystemUrl) {
		if(dto == null) return null;
		return FhirUtil.createCodeableConcept(dto.code, dto.display, codeSystemUrl);
	}
}
