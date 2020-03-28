package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;

public class FamilyMemberHistoryConditionEntity {

	public BaseCodeableConcept code;
	public BaseCodeableConcept outcome;
	public Boolean contributedToDeath;
	@JsonIgnore
	public Type onset;
	public List<BaseAnnotation> note;
}
