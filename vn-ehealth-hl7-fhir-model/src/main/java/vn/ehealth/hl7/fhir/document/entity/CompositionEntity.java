package vn.ehealth.hl7.fhir.document.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseNarrative;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

@Document(collection = "composition")
//@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class CompositionEntity extends BaseResource {

	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;
	public BaseIdentifier identifier;
	public String status;
	public BaseCodeableConcept type;
	public List<BaseCodeableConcept> category;
	public BaseReference subject;
	public BaseReference encounter;
	public Date date;
	public List<BaseReference> author;
	public String title;
	public String confidentiality;
	public List<CompositionAttester> attester;
	public BaseReference custodian;
	public List<CompositionRelatesTo> relatesTo;
	public List<CompositionEvent> event;
	public List<CompositionSection> section;

	public static class CompositionAttester {
		public String mode;
		public Date time;
		public BaseReference party;
	}

	public static class CompositionRelatesTo {
		public String code;
		public BaseType target;
	}

	public static class CompositionEvent {
		public List<BaseCodeableConcept> code;
		public BasePeriod period;
		public List<BaseReference> detail;
	}

	public static class CompositionSection {
		public String title;
		public BaseCodeableConcept code;
		public List<BaseReference> author;
		public BaseReference focus;
		public BaseNarrative text;
		public String mode;
		public BaseCodeableConcept orderedBy;
		public List<BaseReference> entry;
		public BaseCodeableConcept emptyReason;
		public List<CompositionSection> section;
	}
}
