package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "media")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class MediaEntity extends BaseResource {
	@Id
    @Indexed(name = "_id_")
	public ObjectId id;
	public List<BaseIdentifier> identifier;	
	public List<BaseReference> basedOn;
	public List<BaseReference> partOf;
	public String status;
	public BaseCodeableConcept type;
	public BaseCodeableConcept modality;
	public BaseCodeableConcept view;
	public BaseReference subject;
	public BaseReference encounter;
	public BaseType created;
	public Date issued;
	public BaseReference operator;
	public List<BaseCodeableConcept> reasonCode;
	public BaseCodeableConcept bodySite;
	public String deviceName;
	public BaseReference device;
	public Integer height;
	public Integer width;
	public Integer frames;
	public BigDecimal duration;
	public BaseAttachment content;
	public List<BaseAnnotation> note;
}
