package vn.ehealth.hl7.fhir.base.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "binary")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class BinaryEntity extends BaseResource {
	
	@Id
	@Indexed(name = "_id_")
	@JsonIgnore public ObjectId id;
	
	public String contentType;
	public BaseReference securityContext;
	public byte[] data;
}
