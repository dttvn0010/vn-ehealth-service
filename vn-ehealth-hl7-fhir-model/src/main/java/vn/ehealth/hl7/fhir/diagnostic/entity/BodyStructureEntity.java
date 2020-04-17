package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "bodyStructure")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class BodyStructureEntity extends BaseResource{
	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;
	
	public List<BaseIdentifier> identifier;
	public BaseReference patient;
	public boolean active;
	public BaseCodeableConcept morphology;
	public BaseCodeableConcept location;
	public List<BaseCodeableConcept> locationQualifier;
	public String description;
	public List<BaseAttachment> image;
}
