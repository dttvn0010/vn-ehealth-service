package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.Date;


import java.util.List;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "goal")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class GoalEntity extends BaseResource {
    
    public static class GoalTarget {
        public BaseCodeableConcept measure;
        @JsonIgnore public Type detail;
        @JsonIgnore public Type due;
    }
    
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String lifecycleStatus;
    public BaseCodeableConcept achievementStatus;
    public List<BaseCodeableConcept> category;
    public BaseCodeableConcept priority;
    public BaseCodeableConcept description;
    public BaseReference subject;
    @JsonIgnore public Type start;
    public List<GoalTarget> target;
    public Date statusDate;
    public String statusReason;
    public BaseReference expressedBy;
    public List<BaseReference> addresses;
    public List<BaseAnnotation> note;
    public List<BaseCodeableConcept> outcomeCode;
    public List<BaseReference> outcomeReference;
}
