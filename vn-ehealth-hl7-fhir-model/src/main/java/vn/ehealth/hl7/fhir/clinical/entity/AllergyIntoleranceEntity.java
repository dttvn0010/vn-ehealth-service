package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceCategory;
import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceCriticality;
import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceType;
import org.hl7.fhir.r4.model.Enumeration;
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

@Document(collection = "allergyIntolerance")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class AllergyIntoleranceEntity extends BaseResource {
	@Id
	public ObjectId id;
	public List<BaseIdentifier> identifier;

	public BaseCodeableConcept clinicalStatus;
	public BaseCodeableConcept verificationStatus;

	public String type;
	public List<String> category;
	public String criticality;
	public BaseCodeableConcept code;
	public BaseReference patient;
	public BaseReference encounter;
	@JsonIgnore
	public Type onset;
	public Date recordedDate;

	public BaseReference recorder;
	public BaseReference asserter;

	public Date lastOccurrence;
	public List<BaseAnnotation> note;
	public List<AllergyIntoleranceReactionEntity> reaction;

	public static AllergyIntoleranceEntity from(AllergyIntolerance obj) {
		if (obj == null)
			return null;

		var ent = new AllergyIntoleranceEntity();
		ent.identifier = obj.hasIdentifier() ? BaseIdentifier.fromIdentifierList(obj.getIdentifier()) : null;
		ent.clinicalStatus = obj.hasClinicalStatus() ? BaseCodeableConcept.fromCodeableConcept(obj.getClinicalStatus())
				: null;
		ent.verificationStatus = obj.hasVerificationStatus()
				? BaseCodeableConcept.fromCodeableConcept(obj.getVerificationStatus())
				: null;
		ent.type = obj.hasType() ? obj.getType().toCode() : null;
		if (obj.hasCategory()) {
			for (Enumeration<AllergyIntoleranceCategory> item : obj.getCategory()) {
				ent.category.add(item.getCode());
			}
		}
		ent.criticality = obj.hasType() ? obj.getType().toCode() : null;
		ent.code = obj.hasCode() ? BaseCodeableConcept.fromCodeableConcept(obj.getCode()) : null;
		ent.patient = obj.hasPatient() ? BaseReference.fromReference(obj.getPatient()) : null;
		ent.encounter = obj.hasEncounter() ? BaseReference.fromReference(obj.getEncounter()) : null;
		ent.onset = obj.hasOnset() ? obj.getOnset() : null;
		ent.recordedDate = obj.hasRecordedDate() ? obj.getRecordedDate() : null;
		ent.note = obj.hasNote() ? BaseAnnotation.fromAnnotationList(obj.getNote()) : null;
		ent.reaction = obj.hasReaction() ? AllergyIntoleranceReactionEntity.from(obj.getReaction()) : null;

		return ent;
	}
	
	public static AllergyIntolerance to(AllergyIntoleranceEntity ent) {
		if (ent == null)
			return null;

		var obj = new AllergyIntolerance();
		obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
		obj.setClinicalStatus(BaseCodeableConcept.toCodeableConcept(ent.clinicalStatus));
		obj.setVerificationStatus(BaseCodeableConcept.toCodeableConcept(ent.verificationStatus));
		obj.setType(AllergyIntoleranceType.fromCode(ent.type));
		if (ent.category != null && ent.category.size() > 0) {
			// TODO: set category back
		}
		obj.setCriticality(AllergyIntoleranceCriticality.fromCode(ent.type));
		obj.setPatient(BaseReference.toReference(ent.patient));
		obj.setEncounter(BaseReference.toReference(ent.encounter));
		obj.setCode(BaseCodeableConcept.toCodeableConcept(ent.code));
		obj.setOnset(ent.onset);
		obj.setRecordedDate(ent.recordedDate);
		obj.setRecorder(BaseReference.toReference(ent.recorder));
		obj.setAsserter(BaseReference.toReference(ent.asserter));
		obj.setLastOccurrence(ent.lastOccurrence);
		obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
		obj.setReaction(AllergyIntoleranceReactionEntity.to(ent.reaction));
		
		return obj;
	}
}
