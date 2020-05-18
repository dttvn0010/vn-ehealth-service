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
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

@Document(collection = "auditEvent")
//@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class AuditEventEntity extends BaseResource {
	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;

	public BaseCoding type;
	public List<BaseCoding> subtype;
	public String action;
	public BasePeriod period;
	public Date recorded;
	public String outcome;
	public String outcomeDesc;
	public List<BaseCodeableConcept> purposeOfEvent;
	public List<AuditEventAgent> agent;
	public AuditEventSource source;
	public List<AuditEventEntry> entity;

	public static class AuditEventAgent {
		public BaseCodeableConcept type;
		public List<BaseCodeableConcept> role;
		public BaseReference who;
		public String altId;
		public String name;
		public boolean requestor;
		public BaseReference location;
		public List<String> policy;
		public BaseCoding media;
		public AuditEventAgentNetwork network;
		public List<BaseCodeableConcept> purposeOfUse;

		public static class AuditEventAgentNetwork {
			public String address;
			public String type;
		}
	}
	
	public static class AuditEventSource {
		public String site;
		public BaseReference observer;
		public List<BaseCoding> type;
	}
	
	public static class AuditEventEntry {
		public BaseReference what;
		public BaseCoding type;
		public BaseCoding role;
		public BaseCoding lifecycle;
		public List<BaseCoding> securityLabel;
		public String name;
		public String description;
		public byte[] query;
		public List<AuditEventEntryDetail> detail;
		
		public static class AuditEventEntryDetail {
			public String type;
			public BaseType value;
		}
	}
}
