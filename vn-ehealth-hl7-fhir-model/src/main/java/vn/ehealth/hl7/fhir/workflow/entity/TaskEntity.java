package vn.ehealth.hl7.fhir.workflow.entity;

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
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

@Document(collection = "task")
//@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class TaskEntity extends BaseResource {

	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;

	public List<BaseIdentifier> identifier;
	public String instantiatesCanonical;
	public String instantiatesUri;
	public List<BaseReference> basedOn;
	public BaseIdentifier groupIdentifier;
	public List<BaseReference> partOf;
	public String status;
	public BaseCodeableConcept statusReason;
	public BaseCodeableConcept businessStatus;
	public String intent;
	public String priority;
	public BaseCodeableConcept code;
	public String description;
	public BaseReference focus;
	public BaseReference for_;
	public BaseReference encounter;
	public BasePeriod executionPeriod;
	public Date authoredOn;
	public Date lastModified;
	public BaseReference requester;
	public List<BaseCodeableConcept> performerType;
	public BaseReference owner;
	public BaseReference location;
	public BaseCodeableConcept reasonCode;
	public BaseReference reasonReference;
	public List<BaseReference> insurance;
	public List<BaseAnnotation> note;
	public List<BaseReference> relevantHistory;
	public TaskRestriction restriction;
	public List<TaskInput> input;
	public List<TaskOutput> output;

	public static class TaskRestriction {
		public int repetitions;
		public BasePeriod period;
		public List<BaseReference> recipient;
		
	}
	
	public static class TaskInput {
		public BaseCodeableConcept type;
		public BaseType value;
	}
	
	public static class TaskOutput {
		public BaseCodeableConcept type;
		public BaseType value;
	}
}
