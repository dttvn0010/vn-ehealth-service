package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
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
}
