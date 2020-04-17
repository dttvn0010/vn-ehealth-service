package vn.ehealth.hl7.fhir.definitionalartifact.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactDetail;
import vn.ehealth.hl7.fhir.core.entity.BaseDosage;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseRelatedArtifact;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;
import vn.ehealth.hl7.fhir.core.entity.BaseUsageContext;

@Document(collection = "activityDefinition")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class ActivityDefinitionEntity extends BaseResource {

	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;

	public String url;
	public List<BaseIdentifier> identifier;
	public String version;
	public String name;
	public String title;
	public String subtitle;
	public String status;
	public boolean experimental;
	public BaseType subject;
	public Date date;
	public String publisher;
	public List<BaseContactDetail> contact;
	public String description;
	public BaseUsageContext useContext;
	public List<BaseCodeableConcept> jurisdiction;
	public String purpose;
	public String usage;
	public String copyright;
	public Date approvalDate;
	public Date lastReviewDate;
	public BasePeriod effectivePeriod;
	public List<BaseCodeableConcept> topic;
	public List<BaseContactDetail> author;
	public List<BaseContactDetail> editor;
	public List<BaseContactDetail> reviewer;
	public List<BaseContactDetail> endorser;
	public List<BaseRelatedArtifact> relatedArtifact;
	public List<String> library;
	public String kind;
	public String profile;
	public BaseCodeableConcept code;
	public String intent;
	public String priority;
	public boolean doNotPerform;
	public BaseType timing;
	public BaseReference location;
	public List<ActivityDefinitionParticipant> participant;
	public BaseType product;
	public BaseQuantity quantity;
	public List<BaseDosage> dosage;
	public List<BaseCodeableConcept> bodySite;
	public List<BaseReference> specimenRequirement;
	public List<BaseReference> observationRequirement;
	public List<BaseReference> observationResultRequirement;
	public String transform;
	public List<ActivityDefinitionDynamicValue> dynamicValue;

	public static class ActivityDefinitionParticipant {
		public String type;
		public BaseCodeableConcept role;

	}

	public static class ActivityDefinitionDynamicValue {
		public String path;
		public String expression;
	}

}
