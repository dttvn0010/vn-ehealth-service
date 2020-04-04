package vn.ehealth.hl7.fhir.document.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

@Document(collection = "documentReference")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class DocumentReferenceEntity {
	
	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;
	public BaseIdentifier masterIdentifier;
	public List<BaseIdentifier> identifier;
	public String status;
	public String docStatus;
	public BaseCodeableConcept type;
	public List<BaseCodeableConcept> category;
	public BaseReference patient;
	public List<BaseReference> author;
	public Date date;
	public BaseReference authenticator;
	public BaseReference custodian;
	public List<DocumentReferenceRelatesTo> relatesTo;
	public String description;
	public List<BaseCodeableConcept> securityLabel;
	public List<DocumentReferenceContent> content;
	public DocumentReferenceContext context;
	
	public static class DocumentReferenceRelatesTo {
		public String code;
		public BaseReference target;
	}
	
	public static class DocumentReferenceContent {
		public BaseAttachment attachment;
		public BaseCoding format;
	}
	
	public static class DocumentReferenceContext {
		public List<BaseReference> encounter;
		public List<BaseCodeableConcept> event;
		public BasePeriod period;
		public BaseCodeableConcept facilityType;
		public BaseCodeableConcept practiceSetting;
		public BaseReference sourcePatientInfo;
		public List<BaseReference> related;
	}
}
