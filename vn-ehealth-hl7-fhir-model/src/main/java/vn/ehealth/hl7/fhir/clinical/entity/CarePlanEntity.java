package vn.ehealth.hl7.fhir.clinical.entity;

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
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "carePlan")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class CarePlanEntity extends BaseResource {
    
    public static class CarePlanActivityDetail {
        public String kind;
        public List<String> instantiatesCanonical;
        public List<String> instantiatesUri;
        public BaseCodeableConcept code;
        public List<BaseCodeableConcept> reasonCode;
        public List<BaseReference> reasonReference;
        public List<BaseReference> reasonBaseReference;
        public List<BaseReference> goal;
        public String status;
        public BaseCodeableConcept statusReason;
        public Boolean doNotPerform;
        public BaseType scheduled;
        public BaseReference location;
        public List<BaseReference> performer;
        public BaseType product;
        public BaseQuantity dailyAmount;
        public BaseQuantity quantity;
        public String description;
    }
    
    public static class CarePlanActivity {        
        public List<BaseCodeableConcept> outcomeCodeableConcept;
        public List<BaseReference> outcomeReference;
        public List<BaseAnnotation> progress;
        public BaseReference reference;
        public CarePlanActivityDetail detail;
        
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<String> instantiatesCanonical;
    public List<String> instantiatesUri;
    public List<BaseReference> basedOn;
    public List<BaseReference> replaces;
    public List<BaseReference> partOf;
    public String status;
    public String intent;
    public List<BaseCodeableConcept> category;
    public String title;
    public String description;
    public BaseReference subject;
    public BaseReference encounter;    
    public BasePeriod period;
    public Date created;
    public BaseReference author;
    public List<BaseReference> contributor;
    public List<BaseReference> careTeam;
    public List<BaseReference> addresses;
    public List<BaseReference> supportingInfo;
    public List<BaseReference> goal;
    public List<CarePlanActivity> activity;
    public List<BaseAnnotation> note;
    
}
