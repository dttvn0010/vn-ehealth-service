package vn.ehealth.hl7.fhir.user.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

@Document(collection = "group")
//@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class GroupEntity extends BaseResource {

	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;	
	
	public List<BaseIdentifier> identifier;
	public boolean active;
	public String type;
	public boolean actual;
	public BaseCodeableConcept code;
	public String name;
	public int quantity;
	public BaseReference managingEntity;
	public List<GroupCharacteristic> characteristic;
	public List<GroupMember> member;
	
	public static class GroupCharacteristic {
		public BaseCodeableConcept code;
		public BaseType value;
		public boolean exclude;
		public BasePeriod period;
	}
	
	public static class GroupMember {
		public BaseReference entity;
		public BasePeriod period;
		public boolean inactive;
	}
}
