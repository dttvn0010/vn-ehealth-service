package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;


import java.util.List;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.Specimen.SpecimenStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "specimen")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1', 'request.reference':1, 'basedOn.reference':1, 'subject.reference':1}", name = "index_by_default")
public class SpecimenEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public BaseIdentifier accessionIdentifier;
    public String status;
    public BaseCodeableConcept type;
    public BaseReference subject;
    public Date receivedTime;
    public List<BaseReference> parent;
    public List<BaseReference> request;
    public SpecimenCollectionEntity collection;
    public List<SpecimenProcessingEntity> processing;
    public List<SpecimenContainerEntity> container;
    public List<BaseAnnotation> note;
    
    public static SpecimenEntity fromSpecimen(Specimen obj) {
        if(obj == null) return null;
        
        var ent = new SpecimenEntity();
        
        ent.identifier = obj.hasIdentifier()?  BaseIdentifier.fromIdentifierList(obj.getIdentifier()) : null;
        ent.accessionIdentifier = obj.hasAccessionIdentifier()? BaseIdentifier.fromIdentifier(obj.getAccessionIdentifier()) : null;
        ent.status = obj.hasStatus()?  obj.getStatus().toCode() : null;
        ent.type = obj.hasType()? BaseCodeableConcept.fromCodeableConcept(obj.getType()) : null;
        ent.subject = obj.hasSubject()? BaseReference.fromReference(obj.getSubject()) : null;
        ent.receivedTime = obj.hasReceivedTime()? obj.getReceivedTime() : null;
        ent.parent = obj.hasParent()? BaseReference.fromReferenceList(obj.getParent()) : null;
        ent.request = obj.hasRequest()? BaseReference.fromReferenceList(obj.getRequest()) : null;
        ent.collection = obj.hasCollection()? SpecimenCollectionEntity.fromSpecimenCollectionComponent(obj.getCollection()) : null;
        ent.processing = obj.hasProcessing()? transform(obj.getProcessing(),
                                SpecimenProcessingEntity::fromSpecimenProcessingComponent) : null;
        ent.container = obj.hasContainer()? transform(obj.getContainer(), 
                                SpecimenContainerEntity::fromSpecimenContainerComponent) : null;
        ent.note = obj.hasNote()? BaseAnnotation.fromAnnotationList(obj.getNote()) : null;
        
        
        return ent;
    }
    
    public static Specimen toSpecimen(SpecimenEntity ent) {
        if(ent == null) return null;
        
        var obj = new Specimen();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setAccessionIdentifier(BaseIdentifier.toIdentifier(ent.accessionIdentifier));
        obj.setStatus(SpecimenStatus.fromCode(ent.status));
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setReceivedTime(ent.receivedTime);
        obj.setParent(BaseReference.toReferenceList(ent.parent));
        obj.setRequest(BaseReference.toReferenceList(ent.request));
        obj.setCollection(SpecimenCollectionEntity.toSpecimenCollectionComponent(ent.collection));
        
        obj.setProcessing(transform(ent.processing, 
                                SpecimenProcessingEntity::toSpecimenProcessingComponent));
        
        obj.setContainer(transform(ent.container, 
                                SpecimenContainerEntity::toSpecimenContainerComponent));
        
        return obj;
    }
}
