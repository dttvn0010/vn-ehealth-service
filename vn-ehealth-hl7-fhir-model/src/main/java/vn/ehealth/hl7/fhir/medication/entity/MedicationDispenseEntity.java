package vn.ehealth.hl7.fhir.medication.entity;

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
import vn.ehealth.hl7.fhir.core.entity.BaseDosage;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "medicationDispense")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class MedicationDispenseEntity extends BaseResource {
    
    public static class MedicationDispensePerformer {
        public BaseReference actor;
        public BaseCodeableConcept function;
    }
    
    public static class MedicationDispenseSubstitution {
        public boolean wasSubstituted;
        public BaseCodeableConcept type;
        public List<BaseCodeableConcept> reason;
        public List<BaseReference> responsibleParty;
    }

    
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseReference> partOf;
    public String status;
    @JsonIgnore public Type statusReason;
    public BaseCodeableConcept category;
    @JsonIgnore public Type medication;
    public BaseReference subject;
    public BaseReference context;
    public List<BaseReference> supportingInformation;
    public List<MedicationDispensePerformer> performer;
    public BaseReference location;
    public List<BaseReference> authorizingPrescription;
    public BaseCodeableConcept type;
    public BaseQuantity quantity;
    public BaseQuantity daysSupply;
    public Date whenPrepared;
    public Date whenHandedOver;
    public BaseReference destination;
    public List<BaseReference> receiver;
    public List<BaseAnnotation> note;
    public List<BaseDosage> dosageInstruction;
    public MedicationDispenseSubstitution substitution;
    public List<BaseReference> detectedIssue;
    public List<BaseReference> eventHistory;
}
