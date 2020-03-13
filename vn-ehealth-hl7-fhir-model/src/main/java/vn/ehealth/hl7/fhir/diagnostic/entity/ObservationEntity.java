package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;


import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "observation")
public class ObservationEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseReference> basedOn;
    public String status;
    public List<BaseCodeableConcept> category;
    public BaseCodeableConcept code;
    public BaseReference subject;
    //public BaseReference context;
    @JsonIgnore public Type effective;
    public Date issued;
    public List<BaseReference> performer;
    @JsonIgnore public Type value;
    public BaseCodeableConcept dataAbsentReason;
    public List<BaseCodeableConcept> interpretation;
    //public String comment;
    public BaseCodeableConcept bodySite;
    public BaseCodeableConcept method;
    public BaseReference specimen;
    public BaseReference device;
    public List<ObservationReferenceRangeEntity> referenceRange;
    ////public List<ObservationRelatedEntity> related;
    public List<ObservationComponentEntity> component;
    
    public static ObservationEntity fromObservation(Observation obj) {
        if(obj == null) return null;
        
        var ent = new ObservationEntity();
        
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.basedOn = BaseReference.fromReferenceList(obj.getBasedOn());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.category = BaseCodeableConcept.fromCodeableConcept(obj.getCategory());
        ent.code = BaseCodeableConcept.fromCodeableConcept(obj.getCode());
        ent.subject = BaseReference.fromReference(obj.getSubject());
        ent.effective = obj.getEffective();
        ent.issued = obj.getIssued();
        ent.performer = BaseReference.fromReferenceList(obj.getPerformer());
        ent.value = obj.getValue();
        ent.dataAbsentReason = BaseCodeableConcept.fromCodeableConcept(obj.getDataAbsentReason());
        ent.interpretation = BaseCodeableConcept.fromCodeableConcept(obj.getInterpretation());
        ent.bodySite = BaseCodeableConcept.fromCodeableConcept(obj.getBodySite());
        ent.method = BaseCodeableConcept.fromCodeableConcept(obj.getMethod());
        ent.specimen = BaseReference.fromReference(obj.getSpecimen());
        ent.device = BaseReference.fromReference(obj.getDevice());
        ent.referenceRange = transform(obj.getReferenceRange(),
                                ObservationReferenceRangeEntity::fromObservationReferenceRangeComponent);
        
        ent.component = transform(obj.getComponent(), 
                                ObservationComponentEntity::fromObservationComponentComponent);
        
        return ent;
    }
    
    public static Observation toObservation(ObservationEntity ent) {
        if(ent == null) return null;
        
        var obj = new Observation();
        
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier ));
        obj.setBasedOn(BaseReference.toReferenceList(ent.basedOn));
        obj.setStatus(ObservationStatus.fromCode(ent.status));
        obj.setCategory(BaseCodeableConcept.toCodeableConcept(ent.category));
        obj.setCode(BaseCodeableConcept.toCodeableConcept(ent.code));
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setEffective(ent.effective);
        obj.setIssued(ent.issued);
        obj.setPerformer(BaseReference.toReferenceList(ent.performer));
        obj.setValue(ent.value);
        obj.setDataAbsentReason(BaseCodeableConcept.toCodeableConcept(ent.dataAbsentReason));
        obj.setInterpretation(BaseCodeableConcept.toCodeableConcept(ent.interpretation));
        obj.setBodySite(BaseCodeableConcept.toCodeableConcept(ent.bodySite));
        obj.setMethod(BaseCodeableConcept.toCodeableConcept(ent.method));
        obj.setSpecimen(BaseReference.toReference(ent.specimen));
        obj.setDevice(BaseReference.toReference(ent.device));
        obj.setReferenceRange(transform(ent.referenceRange,
                                ObservationReferenceRangeEntity::toObservationReferenceRangeComponent));
        
        obj.setComponent(transform(ent.component, 
                                ObservationComponentEntity::toObservationComponentComponent));
        return obj;
    }
}
