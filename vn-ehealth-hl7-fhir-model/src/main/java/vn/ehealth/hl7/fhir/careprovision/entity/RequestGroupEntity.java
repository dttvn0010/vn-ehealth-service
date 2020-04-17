package vn.ehealth.hl7.fhir.careprovision.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseExpression;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseRelatedArtifact;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

@Document(collection = "requestGroup")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class RequestGroupEntity extends BaseResource {

	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;

	public List<BaseIdentifier> identifier;
	public List<String> instantiatesCanonical;
	public List<String> instantiatesUri;
	public List<BaseReference> basedOn;
	public List<BaseReference> replaces;
	public BaseIdentifier groupIdentifier;
	public String status;
	public String intent;
	public String priority;
	public BaseCodeableConcept code;
	public BaseReference subject;
	public BaseReference encounter;
	public Date authoredOn;
	public BaseReference author;
	public List<BaseCodeableConcept> reasonCode;
	public List<BaseReference> reasonReference;
	public List<BaseAnnotation> note;
	public List<RequestGroupAction> action;

	public static class RequestGroupAction {
		public String prefix;
		public String title;
		public String description;
		public String textEquivalent;
		public String priority;
		public List<BaseCodeableConcept> code;
		public List<BaseRelatedArtifact> documentation;
		public List<RequestGroupActionCondition> condition;
		public List<RequestGroupActionRelatedAction> relatedAction;
		public BaseType timing;
		public List<BaseReference> participant;
		public BaseCodeableConcept type;
		public String groupingBehavior;
		public String selectionBehavior;
		public String requiredBehavior;
		public String precheckBehavior;
		public String cardinalityBehavior;
		public BaseReference resource;
		public List<RequestGroupAction> action;

		public static class RequestGroupActionCondition {
			public String kind;
			public BaseExpression expression;
		}

		public static class RequestGroupActionRelatedAction {
			public String actionId;
			public String relationship;
			public BaseType offset;
		}
	}

}
