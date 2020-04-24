package vn.ehealth.emr.dto.term;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.CodeSystem.PropertyType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.entity.BasePrimitiveType;
import vn.ehealth.hl7.fhir.core.entity.BaseType;
import vn.ehealth.hl7.fhir.core.util.DateUtil;
import vn.ehealth.hl7.fhir.term.entity.ConceptEntity;

@JsonInclude(Include.NON_NULL)
public class ConceptDTO {

	private static BaseType convertPropertyValue(Object value, PropertyType type) {
		String valueString = String.valueOf(value);
		
		switch(type) {
		
			case INTEGER:
				return new BasePrimitiveType(new IntegerType(Integer.valueOf(valueString)));
			
			case DECIMAL:
				return new BasePrimitiveType(new DecimalType(Double.valueOf(valueString)));
				
			case BOOLEAN:
				return new BasePrimitiveType(new BooleanType(Boolean.valueOf(valueString)));
				
			case STRING:
				return new BasePrimitiveType(new StringType(valueString));
				
			case DATETIME:
				Date date = DateUtil.parseStringToDate(valueString, DateUtil.DATE_DB_FORMAT_Y_M_D_H);
				return new BasePrimitiveType(new DateTimeType(date));
				
			default:
				break;
		}
		
		return null;
		
	}
	
	@JsonInclude(Include.NON_NULL)
	public static class ConceptPropertyDTO {
		public String code;
		public Object value;
		public PropertyType type;
		
		public static ConceptPropertyDTO fromEntity(ConceptEntity.ConceptProperty ent) {
			if(ent == null) return null;
			
			var dto = new ConceptPropertyDTO();
			dto.code = ent.code;
			dto.value = ent.value;
			return dto;
		}
		
		public static ConceptEntity.ConceptProperty toEntity(ConceptPropertyDTO dto) {
			if(dto == null) return null;
			
			var ent = new ConceptEntity.ConceptProperty();
			ent.code = dto.code;
			ent.value = convertPropertyValue(dto.value, dto.type);
			return ent;
		}
	}
	
	public String code, display;
	public List<ConceptPropertyDTO> property;
	
	public static ConceptEntity toEntity(ConceptDTO dto) {
		if(dto == null) return null;
		
		var ent = new ConceptEntity();
		ent.code = dto.code;
		ent.display = dto.display;
		ent.property = transform(dto.property, ConceptPropertyDTO::toEntity);			
		return ent;
	}
	
	public static ConceptDTO fromEntity(ConceptEntity ent) {
		if(ent == null) return null;
		
		var dto = new ConceptDTO();
		dto.code = ent.code;
		dto.display = ent.display;
		dto.property = transform(ent.property, ConceptPropertyDTO::fromEntity);
		return dto;
	}
}
