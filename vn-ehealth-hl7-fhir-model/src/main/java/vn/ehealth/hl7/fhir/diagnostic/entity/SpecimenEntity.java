package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;


import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.Specimen.SpecimenStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "specimen")
public class SpecimenEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public BaseIdentifier accessionIdentifier;
    public String status;
    public CodeableConcept type;
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
        
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.accessionIdentifier = BaseIdentifier.fromIdentifier(obj.getAccessionIdentifier());
        ent.status =  Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.type = obj.getType();
        ent.subject = BaseReference.fromReference(obj.getSubject());
        ent.receivedTime = obj.getReceivedTime();
        ent.parent = BaseReference.fromReferenceList(obj.getParent());
        ent.request = BaseReference.fromReferenceList(obj.getRequest());
        ent.collection = SpecimenCollectionEntity.fromSpecimenCollectionComponent(obj.getCollection());
        
        ent.processing = transform(obj.getProcessing(),
                                SpecimenProcessingEntity::fromSpecimenProcessingComponent);
        
        ent.container = transform(obj.getContainer(), 
                                SpecimenContainerEntity::fromSpecimenContainerComponent);
        
        ent.note = BaseAnnotation.fromAnnotationList(obj.getNote());
        
        
        return ent;
    }
    
    public static Specimen toSpecimen(SpecimenEntity ent) {
        if(ent == null) return null;
        
        var obj = new Specimen();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setAccessionIdentifier(BaseIdentifier.toIdentifier(ent.accessionIdentifier));
        obj.setStatus(SpecimenStatus.fromCode(ent.status));
        obj.setType(ent.type);
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setReceivedTime(ent.receivedTime);
        obj.setParent(BaseReference.toReferenceList(ent.parent));
        obj.setCollection(SpecimenCollectionEntity.toSpecimenCollectionComponent(ent.collection));
        
        obj.setProcessing(transform(ent.processing, 
                                SpecimenProcessingEntity::toSpecimenProcessingComponent));
        
        obj.setContainer(transform(ent.container, 
                                SpecimenContainerEntity::toSpecimenContainerComponent));
        
        return obj;
    }
}
