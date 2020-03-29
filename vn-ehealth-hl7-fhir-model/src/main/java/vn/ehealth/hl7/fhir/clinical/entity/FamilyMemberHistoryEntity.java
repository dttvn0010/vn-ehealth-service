package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "familyMemberHistory")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class FamilyMemberHistoryEntity extends BaseResource {
    
    public static class FamilyMemberHistoryCondition {
        public BaseCodeableConcept code;
        public BaseCodeableConcept outcome;
        public Boolean contributedToDeath;
        public BaseType onset;
        public List<BaseAnnotation> note;
    }

    
	@Id
	public ObjectId id;
	public List<BaseIdentifier> identifier;
	public List<String> instantiatesCanonical;
	public List<String> instantiatesUri;
	public String status;
	public BaseCodeableConcept dataAbsentReason;
	public BaseReference patient;
	public Date date;
	public String name;
	public BaseCodeableConcept relationship;
	public BaseCodeableConcept sex;
	public BaseType born;
	public BaseType age;
	public Boolean estimatedAge;
	public BaseType deceased;
	public List<BaseCodeableConcept> reasonCode;
	public List<BaseReference> reasonReference;
	public List<BaseAnnotation> note;
	public List<FamilyMemberHistoryCondition> condition;
}
