package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.FamilyMemberHistory.FamilyHistoryStatus;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "familyMemberHistory")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class FamilyMemberHistoryEntity extends BaseResource {
	@Id
	public ObjectId id;
	public List<BaseIdentifier> identifier;
	// TODO: add transform and base datatype
	public List<BaseReference> instantiatesCanonical;
	// TODO: add transform and base datatype
	public List<String> instantiatesUri;

	public String status;
	public BaseCodeableConcept dataAbsentReason;
	public BaseReference patient;
	public Date date;
	public String name;
	public BaseCodeableConcept relationship;
	public BaseCodeableConcept sex;
	@JsonIgnore
	public Type born;
	@JsonIgnore
	public Type age;
	public Boolean estimatedAge;
	@JsonIgnore
	public Type deceased;
	public List<BaseCodeableConcept> reasonCode;
	public List<BaseReference> reasonReference;
	public List<BaseAnnotation> note;
	public List<FamilyMemberHistoryConditionEntity> condition;

	public static FamilyMemberHistoryEntity from(FamilyMemberHistory obj) {

		if (obj == null)
			return null;

		var ent = new FamilyMemberHistoryEntity();
		ent.identifier = obj.hasIdentifier() ? BaseIdentifier.fromIdentifierList(obj.getIdentifier()) : null;
		ent.status = obj.hasStatus() ? obj.getStatus().toCode() : null;
		ent.dataAbsentReason = obj.hasDataAbsentReason()
				? BaseCodeableConcept.fromCodeableConcept(obj.getDataAbsentReason())
				: null;
		ent.patient = obj.hasPatient() ? BaseReference.fromReference(obj.getPatient()) : null;
		ent.date = obj.hasDate() ? obj.getDate() : null;
		ent.name = obj.hasName() ? obj.getName() : null;
		ent.relationship = obj.hasRelationship() ? BaseCodeableConcept.fromCodeableConcept(obj.getRelationship())
				: null;
		ent.sex = obj.hasSex() ? BaseCodeableConcept.fromCodeableConcept(obj.getSex()) : null;
		ent.born = obj.hasBorn() ? obj.getBorn() : null;
		ent.age = obj.hasAge() ? obj.getAge() : null;
		ent.estimatedAge = obj.hasEstimatedAge() ? obj.getEstimatedAge() : null;
		ent.deceased = obj.hasDeceased() ? obj.getDeceased() : null;
		ent.reasonCode = obj.hasReasonCode() ? BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode()) : null;
		ent.reasonReference = obj.hasReasonReference() ? BaseReference.fromReferenceList(obj.getReasonReference())
				: null;
		ent.note = obj.hasNote() ? BaseAnnotation.fromAnnotationList(obj.getNote()) : null;
		ent.condition = obj.hasCondition() ? FamilyMemberHistoryConditionEntity.from(obj.getCondition()) : null;
		return ent;
	}

	public static FamilyMemberHistory to(FamilyMemberHistoryEntity ent) {
		if (ent == null)
			return null;

		var obj = new FamilyMemberHistory();
		obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
		obj.setStatus(FamilyHistoryStatus.fromCode(ent.status));
		obj.setDataAbsentReason(BaseCodeableConcept.toCodeableConcept(ent.dataAbsentReason));
		obj.setPatient(BaseReference.toReference(ent.patient));
		obj.setDate(ent.date);
		obj.setName(ent.name);
		obj.setRelationship(BaseCodeableConcept.toCodeableConcept(ent.relationship));
		obj.setSex(BaseCodeableConcept.toCodeableConcept(ent.sex));
		obj.setBorn(ent.born);
		obj.setAge(ent.age);
		obj.setEstimatedAge(ent.estimatedAge != null ? ent.estimatedAge : null);
		obj.setDeceased(ent.deceased);
		obj.setReasonCode(BaseCodeableConcept.toCodeableConcept(ent.reasonCode));
		obj.setReasonReference(BaseReference.toReferenceList(ent.reasonReference));
		obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
		obj.setCondition(FamilyMemberHistoryConditionEntity.to(ent.condition));
		return obj;
	}
}
