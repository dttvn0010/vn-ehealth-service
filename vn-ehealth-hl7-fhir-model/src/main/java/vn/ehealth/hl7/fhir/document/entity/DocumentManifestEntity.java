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
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "documentManifest")
//@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class DocumentManifestEntity extends BaseResource {

	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;
	public BaseIdentifier masterIdentifier;
	public List<BaseIdentifier> identifier;
	public String status;
	public BaseCodeableConcept type;
	public BaseReference subject;
	public Date created;
	public List<BaseReference> author;
	public List<BaseReference> recipient;
	public String source;
	public String description;
	public List<BaseReference> content;
	public List<DocumentManifestRelated> related;

	public static class DocumentManifestRelated {
		public BaseIdentifier identifier;
		public BaseReference ref;
	}
}
