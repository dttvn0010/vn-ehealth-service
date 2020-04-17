package vn.ehealth.hl7.fhir.security.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseSignature;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

@Document(collection = "provenance")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class ProvenanceEntity extends BaseResource {
	
	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;	
	
	public List<BaseReference> target;
	public BaseType occurred;
	public Date recorded;
	public List<String> policy;
	public BaseReference location;
	public List<BaseCodeableConcept> reason;
	public BaseCodeableConcept activity;
	public List<ProvenanceAgent> agent;
	public List<ProvenanceEntry> entity;
	public List<BaseSignature> signature;
	
	public static class ProvenanceAgent {
		public BaseCodeableConcept type;
		public List<BaseCodeableConcept> role;
		public BaseReference who;
		public BaseReference onBehalfOf;
	}
	
	public static class ProvenanceEntry {
		public String role;
		public BaseReference what;
		
	}
}
