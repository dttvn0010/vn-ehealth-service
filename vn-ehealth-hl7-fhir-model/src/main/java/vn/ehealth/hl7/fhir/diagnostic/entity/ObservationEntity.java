package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;


import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseBackboneElement;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseRange;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "observation")
//@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1, 'basedOn.reference':1, 'subject.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class ObservationEntity extends BaseResource {
    
    public static class ObservationReferenceRange extends BaseBackboneElement {

        public BaseQuantity low;
        public BaseQuantity high;
        public BaseCodeableConcept type;
        public List<BaseCodeableConcept> appliesTo;
        public BaseRange age;
        public String text;
    }
    
    public static class ObservationComponent extends BaseBackboneElement {
        public BaseCodeableConcept code;
        public BaseType value;
        public BaseCodeableConcept dataAbsentReason;
        public List<BaseCodeableConcept> interpretation;
        public List<ObservationReferenceRange> referenceRange;
    }
    
    @Id
    @Indexed(name = "_id_")
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseReference> basedOn;
    public List<BaseReference> partOf;
    public String status;
    public List<BaseCodeableConcept> category;
    public BaseCodeableConcept code;
    public BaseReference subject;
    public List<BaseReference> focus;
    public BaseReference encounter;
    public BaseType effective;
    public Date issued;
    public List<BaseReference> performer;
    public BaseType value;
    public BaseCodeableConcept dataAbsentReason;
    public List<BaseCodeableConcept> interpretation;
    public List<BaseAnnotation> note;
    public BaseCodeableConcept bodySite;
    public BaseCodeableConcept method;
    public BaseReference specimen;
    public BaseReference device;
    public List<ObservationReferenceRange> referenceRange;
    public List<BaseReference> hasMember;
    public List<BaseReference> derivedFrom;
    public List<ObservationComponent> component;
}
