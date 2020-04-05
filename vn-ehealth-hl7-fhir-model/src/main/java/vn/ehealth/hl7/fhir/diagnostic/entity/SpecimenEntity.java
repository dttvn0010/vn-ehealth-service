package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;


import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseDuration;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "specimen")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1'}", name = "index_by_default")
public class SpecimenEntity extends BaseResource {
    
    public static class SpecimenCollection {
        public BaseReference collector;
        public BaseType collected;
        public BaseDuration duration;
        public BaseQuantity quantity;
        public BaseCodeableConcept method;
        public BaseCodeableConcept bodySite;
        public BaseType fastingStatus;
    }
    
    public static class SpecimenProcessing {
        public String description;
        public BaseCodeableConcept procedure;
        public List<BaseReference> additive;
        public BaseType time;
    }
    
    public static class SpecimenContainer {
        public List<BaseIdentifier> identifier;
        public String description;
        public BaseCodeableConcept type;
        public BaseQuantity capacity;
        public BaseQuantity specimenQuantity;
        public BaseType additive;        
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public List<BaseIdentifier> identifier;
    public BaseIdentifier accessionIdentifier;
    public String status;
    public BaseCodeableConcept type;
    public BaseReference subject;
    public Date receivedTime;
    public List<BaseReference> parent;
    public List<BaseReference> request;
    public SpecimenCollection collection;
    public List<SpecimenProcessing> processing;
    public List<SpecimenContainer> container;
    public List<BaseCodeableConcept> condition;
    public List<BaseAnnotation> note;
}
