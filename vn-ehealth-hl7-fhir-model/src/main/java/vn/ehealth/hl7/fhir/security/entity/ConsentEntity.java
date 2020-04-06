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
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

@Document(collection = "consent")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ConsentEntity extends BaseResource {
	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;	
	
	public List<BaseIdentifier> identifier;
	public String status;
	public BaseCodeableConcept scope;
	public List<BaseCodeableConcept> category;
	public BaseReference patient;
	public Date datetime;
	public List<BaseReference> performer;
	public List<BaseReference> organization;
	public BaseType source;
	public List<ConsentPolicy> policy;
	public BaseCodeableConcept policyRule;
	public List<ConsentVerification> verification;
	public ConsentProvision provision;
	
	public static class ConsentPolicy {
		public String authority;
		public String uri;
	}
	
	public static class ConsentVerification {
		public boolean verified;
		public BaseReference verifiedWith;
		public Date verificationDate;
	}
	
	public static class ConsentProvision {
		public String type;
		public BasePeriod period;
		public List<ConsentProvisionActor> actor;
		public List<BaseCodeableConcept> action;
		public List<BaseCoding> securityLabel;
		public List<BaseCoding> purpose;
		public List<BaseCoding> class_;
		public List<BaseCodeableConcept> code;
		public BasePeriod dataPeriod;
		public List<ConsentProvisionData> data;
		public List<ConsentProvision> provision;
		
		public static class ConsentProvisionActor {
			public BaseCodeableConcept role;
			public BaseReference reference;
		}	
		
		public static class ConsentProvisionData {
			public String meaning;
			public BaseReference reference;
		}
	}
}
