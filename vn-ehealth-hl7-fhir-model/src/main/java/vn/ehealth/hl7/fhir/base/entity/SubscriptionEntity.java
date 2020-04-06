package vn.ehealth.hl7.fhir.base.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "subscription")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class SubscriptionEntity extends BaseResource {

	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;

	public String status;
	public List<BaseContactPoint> contact;
	public Date end;
	public String reason;
	public String criteria;
	public String error;
	public SubscriptionChannel channel;

	public static class SubscriptionChannel {
		public String type;
		public String endpoint;
		public String payload;
		public List<String> header;
	}
}
