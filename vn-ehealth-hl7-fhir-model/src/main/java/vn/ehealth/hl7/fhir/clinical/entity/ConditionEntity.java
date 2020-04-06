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
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "condition")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ConditionEntity extends BaseResource {

    public static class ConditionStage {        
        public BaseCodeableConcept summary;
        public List<BaseReference> assessment;
        public BaseCodeableConcept type;        
    }

    public static class ConditionEvidence {

        public List<BaseCodeableConcept> code;
        public List<BaseReference> detail;
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public List<BaseIdentifier> identifier;
    public BaseCodeableConcept clinicalStatus;
    public BaseCodeableConcept verificationStatus;
    public List<BaseCodeableConcept> category;
    public BaseCodeableConcept severity;
    public BaseCodeableConcept code;
    public List<BaseCodeableConcept> bodySite;
    public BaseReference subject;
    public BaseReference encounter;
    public BaseType onset;
    public BaseType abatement;
    public Date recordedDate;
    public BaseReference recorder;
    public BaseReference asserter;
    public List<ConditionStage> stage;
    public List<ConditionEvidence> evidence;
    public List<BaseAnnotation> note;
}
