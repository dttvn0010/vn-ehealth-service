package vn.ehealth.emr.dto.term;

import org.hl7.fhir.r4.model.CodeSystem;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.term.entity.CodeSystemEntity;

@JsonInclude(Include.NON_NULL)
public class CodeSystemDTO extends CodeSystemEntity {

	public static CodeSystemDTO fromFhir(CodeSystem obj) {
		return DataConvertUtil.fhirToEntity(obj, CodeSystemDTO.class);
	}
}
