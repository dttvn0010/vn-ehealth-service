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
import vn.ehealth.hl7.fhir.core.entity.BaseBackboneElement;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseDuration;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "specimen")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1'}", name = "index_by_default")
public class SpecimenEntity extends BaseResource {
    
    public static class SpecimenCollection extends BaseBackboneElement {
        public BaseReference collector;
        @JsonIgnore public BaseType collected;
        public BaseDuration duration;
        public BaseQuantity quantity;
        public BaseCodeableConcept method;
        public BaseCodeableConcept bodySite;
        @JsonIgnore public BaseType fastingStatus;
    }
    
    public static class SpecimenProcessing extends BaseBackboneElement {
        public String description;
        public BaseCodeableConcept procedure;
        public List<BaseReference> additive;
        public BaseType time;
    }
    
    public static class SpecimenContainer extends BaseBackboneElement {
        public List<BaseIdentifier> identifier;
        public String description;
        public BaseCodeableConcept type;
        public BaseQuantity capacity;
        public BaseQuantity specimenQuantity;
        @JsonIgnore public BaseType additive;        
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
