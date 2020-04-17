package vn.ehealth.hl7.fhir.medication.entity;

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
import vn.ehealth.hl7.fhir.core.entity.BaseDosage;
import vn.ehealth.hl7.fhir.core.entity.BaseDuration;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "medicationRequest")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class MedicationRequestEntity extends BaseResource {
    
    public static class MedicationRequestDispenseRequestInitialFill {
        public BaseQuantity quantity;
        public BaseDuration duration;
    }
    
    public static class MedicationRequestDispenseRequest{
        public MedicationRequestDispenseRequestInitialFill initialFill;
        public BaseDuration dispenseInterval;
        public BasePeriod validityPeriod;
        public int numberOfRepeatsAllowed;
        public BaseQuantity quantity;
        public BaseDuration expectedSupplyDuration;
        public BaseReference performer;
    }

    public static class MedicationRequestSubstitution{
        public BaseType allowed;
        public BaseCodeableConcept reason;
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public List<BaseIdentifier> identifier;
    //public List<BaseReference> definition;
    public BaseReference encounter;
    public List<BaseReference> basedOn;
    public BaseIdentifier groupIdentifier;
    public BaseCodeableConcept courseOfTherapyType;
    public List<BaseReference> insurance;
    public String status;
    public BaseCodeableConcept statusReason;
    public String intent;
    public List<BaseCodeableConcept> category;
    public String priority;
    public Boolean doNotPerform;
    public BaseType reported;
    public BaseType medication;
    public BaseReference subject;
    public List<BaseReference> supportingInformation;
    public Date authoredOn;
    public BaseReference requester;
    public BaseReference performer;
    public BaseCodeableConcept performerType;
    
    public BaseReference recorder;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public List<String> instantiatesCanonical;
    public List<String> instantiatesUri;
    public List<BaseAnnotation> note;
    public List<BaseDosage> dosageInstruction;
    public MedicationRequestDispenseRequest dispenseRequest;//
    public MedicationRequestSubstitution substitution;//
    public BaseReference priorPrescription;
    public List<BaseReference> detectedIssue;
    public List<BaseReference> eventHistory;
}
