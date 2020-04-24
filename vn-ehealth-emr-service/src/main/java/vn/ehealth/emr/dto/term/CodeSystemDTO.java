package vn.ehealth.emr.dto.term;

import java.util.List;

import org.hl7.fhir.r4.model.CodeSystem;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@JsonInclude(Include.NON_NULL)
public class CodeSystemDTO {

	@JsonInclude(Include.NON_NULL)
	public static class CodeSystemPropertyDTO {
		public String code, type;
		
		public static CodeSystemPropertyDTO fromFhir(CodeSystem.PropertyComponent obj) {
			if(obj == null) return null;
			
			var dto = new CodeSystemPropertyDTO();
			dto.code = obj.getCode();
			dto.type = obj.hasType()? obj.getType().toCode() : "";
			return dto;
		}
	}
	
	public String url, name, description, publisher;
	public List<CodeSystemPropertyDTO> property;
	public List<ConceptDTO> concept;
	
	public static CodeSystemDTO fromFhir(CodeSystem obj) {
		if(obj == null) return null;
		
		var dto = new CodeSystemDTO();
		dto.url = obj.getUrl();
		dto.name = obj.getName();
		dto.description = obj.getDescription();
		dto.publisher = obj.getPublisher();
		dto.property = transform(obj.getProperty(), CodeSystemPropertyDTO::fromFhir);
		return dto;
	}
}
