package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Media;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.Media.MediaStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "media")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class MediaEntity extends BaseResource {
	@Id
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
	@JsonIgnore
	public Type created;
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
	
	public static MediaEntity from(Media obj) {
		if (obj == null)
			return null;

		var ent = new MediaEntity();
		ent.identifier = obj.hasIdentifier() ? BaseIdentifier.fromIdentifierList(obj.getIdentifier()) : null;
		ent.basedOn = obj.hasBasedOn() ? BaseReference.fromReferenceList(obj.getBasedOn()) : null;
		ent.partOf = obj.hasPartOf() ? BaseReference.fromReferenceList(obj.getPartOf()) : null;
		ent.status = obj.hasStatus() ? obj.getStatus().toCode() : null;
		ent.type = obj.hasType() ? BaseCodeableConcept.fromCodeableConcept(obj.getType()) : null;
		ent.modality = obj.hasModality() ? BaseCodeableConcept.fromCodeableConcept(obj.getModality()) : null;
		ent.view = obj.hasView() ? BaseCodeableConcept.fromCodeableConcept(obj.getView()) : null;
		ent.subject = obj.hasSubject() ? BaseReference.fromReference(obj.getSubject()) : null;
		ent.encounter = obj.hasBasedOn() ? BaseReference.fromReference(obj.getEncounter()) : null;
		ent.created = obj.hasCreated() ? obj.getCreated() : null;
		ent.issued = obj.hasIssued() ? obj.getIssued() : null;
		ent.operator = obj.hasBasedOn() ? BaseReference.fromReference(obj.getOperator()) : null;
		ent.reasonCode = obj.hasReasonCode() ? BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode()) : null;
		ent.bodySite = obj.hasBodySite() ? BaseCodeableConcept.fromCodeableConcept(obj.getBodySite()) : null;
		ent.deviceName = obj.hasDeviceName() ? obj.getDeviceName() : null;
		ent.device = obj.hasDevice() ? BaseReference.fromReference(obj.getDevice()) : null;
		ent.height = obj.hasHeight() ? obj.getHeight() : null;
		ent.width = obj.hasWidth() ? obj.getWidth() : null;
		ent.frames = obj.hasFrames() ? obj.getFrames() : null;
		ent.duration = obj.hasDuration()? obj.getDuration() : null;
		ent.content = obj.hasContent() ? BaseAttachment.fromAttachment(obj.getContent()) : null;
		ent.note = obj.hasNote() ? BaseAnnotation.fromAnnotationList(obj.getNote()) : null;
		return ent;
	}
	
	public static Media to(MediaEntity ent) {
		if (ent == null)
			return null;
		var obj = new Media();
		obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
		obj.setBasedOn(BaseReference.toReferenceList(ent.basedOn));
		obj.setPartOf(BaseReference.toReferenceList(ent.partOf));
		obj.setStatus(MediaStatus.fromCode(ent.status));
		obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
		obj.setModality(BaseCodeableConcept.toCodeableConcept(ent.modality));
		obj.setView(BaseCodeableConcept.toCodeableConcept(ent.view));
		obj.setSubject(BaseReference.toReference(ent.subject));
		obj.setEncounter(BaseReference.toReference(ent.encounter));
		obj.setCreated(ent.created);
		obj.setIssued(ent.issued);
		obj.setOperator(BaseReference.toReference(ent.operator));
		obj.setReasonCode(BaseCodeableConcept.toCodeableConcept(ent.reasonCode));
		obj.setBodySite(BaseCodeableConcept.toCodeableConcept(ent.bodySite));
		obj.setDeviceName(ent.deviceName);
		obj.setDevice(BaseReference.toReference(ent.device));
		obj.setHeight(ent.height);
		obj.setWidth(ent.width);
		obj.setFrames(ent.frames);
		obj.setDuration(ent.duration);
		obj.setContent(BaseAttachment.toAttachment(ent.content));
		obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
		return obj;
	}
}
