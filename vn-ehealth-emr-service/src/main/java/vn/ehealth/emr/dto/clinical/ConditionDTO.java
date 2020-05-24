package vn.ehealth.emr.dto.clinical;

import org.hl7.fhir.r4.model.Condition;

import vn.ehealth.hl7.fhir.clinical.entity.ConditionEntity;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

public class ConditionDTO extends ConditionEntity {

	public static ConditionDTO fromFhir(Condition obj) {
		if(obj == null) return null;
		
		return DataConvertUtil.fhirToEntity(obj, ConditionDTO.class);
	}
}
