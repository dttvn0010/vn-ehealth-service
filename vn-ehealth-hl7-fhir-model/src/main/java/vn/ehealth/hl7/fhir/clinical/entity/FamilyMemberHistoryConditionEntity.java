package vn.ehealth.hl7.fhir.clinical.entity;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

import java.util.List;

import org.hl7.fhir.r4.model.FamilyMemberHistory.FamilyMemberHistoryConditionComponent;
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

	public static FamilyMemberHistoryConditionEntity from(FamilyMemberHistoryConditionComponent obj) {
		if (obj == null)
			return null;

		var ent = new FamilyMemberHistoryConditionEntity();
		ent.code = obj.hasCode() ? BaseCodeableConcept.fromCodeableConcept(obj.getCode()) : null;
		ent.outcome = obj.hasOutcome() ? BaseCodeableConcept.fromCodeableConcept(obj.getOutcome()) : null;
		ent.contributedToDeath = obj.hasContributedToDeath() ? obj.getContributedToDeath() : null;
		ent.onset = obj.hasOnset() ? obj.getOnset() : null;
		ent.note = obj.hasNote() ? BaseAnnotation.fromAnnotationList(obj.getNote()) : null;
		return ent;
	}

	public static List<FamilyMemberHistoryConditionEntity> from(List<FamilyMemberHistoryConditionComponent> lst) {
		return transform(lst, x -> from(x));
	}

	public static FamilyMemberHistoryConditionComponent to(FamilyMemberHistoryConditionEntity ent) {
		if (ent == null)
			return null;

		var obj = new FamilyMemberHistoryConditionComponent();
		obj.setCode(BaseCodeableConcept.toCodeableConcept(ent.code));
		obj.setOutcome(BaseCodeableConcept.toCodeableConcept(ent.outcome));
		obj.setContributedToDeath(ent.contributedToDeath != null ? ent.contributedToDeath : null);
		if (ent.onset != null) {
			obj.setOnset(ent.onset);
		}
		if (ent.note != null && ent.note.size() > 0) {
			obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
		}
		return obj;
	}

	public static List<FamilyMemberHistoryConditionComponent> to(List<FamilyMemberHistoryConditionEntity> lst) {
		return transform(lst, x -> to(x));
	}
}
