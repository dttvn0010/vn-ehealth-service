package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "detectedIssue")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class DetectedIssueEntity extends BaseResource {
    
    public static class DetectedIssueMitigation {
        public BaseCodeableConcept action;
        public Date date;
        public BaseReference author;
        
    }
    
    public static class DetectedIssueEvidence {
        public List<BaseCodeableConcept> code;
        public List<BaseReference> detail;
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public BaseCodeableConcept code;
    public String severity;
    public BaseReference patient;
    public BaseType identified;
    public BaseReference author;
    public List<BaseReference> implicated;
    public String detail;
    public String reference;
    public List<DetectedIssueMitigation> mitigation;
    public List<DetectedIssueEvidence> evidence;
 }
