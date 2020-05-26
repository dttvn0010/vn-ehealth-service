package vn.ehealth.emr.dto.clinical;

import org.hl7.fhir.r4.model.AllergyIntolerance;

import vn.ehealth.hl7.fhir.clinical.entity.AllergyIntoleranceEntity;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

public class AllergyIntoleranceDTO extends AllergyIntoleranceEntity {
	
	public static AllergyIntoleranceDTO fromFhir(AllergyIntolerance obj) {
		return DataConvertUtil.fhirToEntity(obj, AllergyIntoleranceDTO.class);
	}
}
