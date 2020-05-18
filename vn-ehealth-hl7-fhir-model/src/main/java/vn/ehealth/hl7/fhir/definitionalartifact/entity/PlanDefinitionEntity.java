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
import vn.ehealth.hl7.fhir.core.entity.BaseDataRequirement;
import vn.ehealth.hl7.fhir.core.entity.BaseDuration;
import vn.ehealth.hl7.fhir.core.entity.BaseExpression;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseRelatedArtifact;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseTriggerDefinition;
import vn.ehealth.hl7.fhir.core.entity.BaseType;
import vn.ehealth.hl7.fhir.core.entity.BaseUsageContext;

@Document(collection = "planDefinition")
//@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class PlanDefinitionEntity extends BaseResource {

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
	public BaseCodeableConcept type;
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
	public List<PlanDefinitionGoal> goal;
	public List<PlanDefinitionAction> action;

	public static class PlanDefinitionGoal {
		public BaseCodeableConcept category;
		public BaseCodeableConcept description;
		public BaseCodeableConcept priority;
		public BaseCodeableConcept start;
		public List<BaseCodeableConcept> addresses;
		public List<BaseRelatedArtifact> documentation;
		public List<PlanDefinitionGoalTarget> target;

		public static class PlanDefinitionGoalTarget {
			public BaseCodeableConcept measure;
			public BaseType detail;
			public BaseDuration due;
		}
	}

	public static class PlanDefinitionAction {
		public String prefix;
		public String title;
		public String description;
		public String textEquivalent;
		public String priority;
		public List<BaseCodeableConcept> code;
		public List<BaseCodeableConcept> reason;
		public List<BaseRelatedArtifact> documentation;
		public List<String> goalId;
		public BaseType subject;
		public List<BaseTriggerDefinition> trigger;
		public List<PlanDefinitionActionCondition> condition;
		public List<BaseDataRequirement> input;
		public List<BaseDataRequirement> output;
		public List<PlanDefinitionActionRelatedAction> relatedAction;
		public BaseType timing;
		public List<PlanDefinitionActionParticipant> participant;
		public BaseCodeableConcept type;
		public String groupingBehavior;
		public String selectionBehavior;
		public String requiredBehavior;
		public String precheckBehavior;
		public String cardinalityBehavior;
		public BaseType definition;
		public String transform;
		public List<PlanDefinitionActionDynamicValue> dynamicValue;
		public List<PlanDefinitionAction> action;

		public static class PlanDefinitionActionCondition {
			public String kind;
			public BaseExpression expression;
		}

		public static class PlanDefinitionActionRelatedAction {
			public String actionId;
			public String relationship;
			public BaseType offset;
		}

		public static class PlanDefinitionActionParticipant {
			public String type;
			public BaseCodeableConcept role;
		}

		public static class PlanDefinitionActionDynamicValue {
			public String path;
			public BaseExpression expression;
		}
	}

}
