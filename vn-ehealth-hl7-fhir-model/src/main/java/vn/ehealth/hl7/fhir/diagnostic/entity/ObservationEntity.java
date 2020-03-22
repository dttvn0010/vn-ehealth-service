package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;


import java.util.List;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "observation")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1, 'basedOn.reference':1, 'subject.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class ObservationEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseReference> basedOn;
    public List<BaseReference> partOf;
    public String status;
    public List<BaseCodeableConcept> category;
    public BaseCodeableConcept code;
    public BaseReference subject;
    public BaseReference encounter;
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
        ent.identifier = obj.hasIdentifier()? BaseIdentifier.fromIdentifierList(obj.getIdentifier()) : null;
        ent.basedOn = obj.hasBasedOn()? BaseReference.fromReferenceList(obj.getBasedOn()) : null;
        ent.partOf = obj.hasPartOf()? BaseReference.fromReferenceList(obj.getPartOf()) : null;
        ent.status = obj.hasStatus()? obj.getStatus().toCode()  : null;
        ent.category = obj.hasCategory()? BaseCodeableConcept.fromCodeableConcept(obj.getCategory()) : null;
        ent.code = obj.hasCode()? BaseCodeableConcept.fromCodeableConcept(obj.getCode()) : null;
        ent.subject = obj.hasSubject()? BaseReference.fromReference(obj.getSubject()) : null;
        ent.encounter = obj.hasEncounter()? BaseReference.fromReference(obj.getSubject()) : null;
        ent.effective = obj.hasEffective()? obj.getEffective() : null;
        ent.issued = obj.hasIssued()? obj.getIssued() : null;
        ent.performer = obj.hasPerformer()? BaseReference.fromReferenceList(obj.getPerformer()) : null;
        ent.value = obj.hasValue()? obj.getValue() : null;
        ent.dataAbsentReason = obj.hasDataAbsentReason()? BaseCodeableConcept.fromCodeableConcept(obj.getDataAbsentReason()) : null;
        ent.interpretation = obj.hasInterpretation()? BaseCodeableConcept.fromCodeableConcept(obj.getInterpretation()) : null;
        ent.bodySite = obj.hasBodySite()? BaseCodeableConcept.fromCodeableConcept(obj.getBodySite()) : null;
        ent.method = obj.hasMethod()? BaseCodeableConcept.fromCodeableConcept(obj.getMethod()) : null;
        ent.specimen = obj.hasSpecimen()? BaseReference.fromReference(obj.getSpecimen()) : null;
        ent.device = obj.hasDevice()? BaseReference.fromReference(obj.getDevice()) : null;
        ent.referenceRange = obj.hasReferenceRange()? transform(obj.getReferenceRange(),
                                ObservationReferenceRangeEntity::fromObservationReferenceRangeComponent) : null;
        
        ent.component = obj.hasComponent()? transform(obj.getComponent(), 
                                ObservationComponentEntity::fromObservationComponentComponent) : null;
        
        return ent;
    }
    
    public static Observation toObservation(ObservationEntity ent) {
        if(ent == null) return null;
        
        var obj = new Observation();
        
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier ));
        obj.setBasedOn(BaseReference.toReferenceList(ent.basedOn));
        obj.setPartOf(BaseReference.toReferenceList(ent.partOf));
        obj.setStatus(ObservationStatus.fromCode(ent.status));
        obj.setCategory(BaseCodeableConcept.toCodeableConcept(ent.category));
        obj.setCode(BaseCodeableConcept.toCodeableConcept(ent.code));
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setEncounter(BaseReference.toReference(ent.encounter));
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
