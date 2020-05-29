package vn.ehealth.emr.dto.medication;

import org.hl7.fhir.r4.model.Immunization;

import vn.ehealth.hl7.fhir.medication.entity.ImmunizationEntity;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

public class ImmunizationDTO extends ImmunizationEntity {
	
	public static ImmunizationDTO fromFhir(Immunization obj) {
		if(obj == null) return null;
		
		return DataConvertUtil.fhirToEntity(obj, ImmunizationDTO.class);
	}
}
