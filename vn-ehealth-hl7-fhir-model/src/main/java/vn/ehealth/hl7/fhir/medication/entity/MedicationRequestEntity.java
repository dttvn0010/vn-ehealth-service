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
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "medicationRequest")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class MedicationRequestEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    //public List<BaseReference> definition;
    public BaseReference encounter;
    public List<BaseReference> basedOn;
    public BaseIdentifier groupIdentifier;
    public String status;
    public String intent;
    public List<BaseCodeableConcept> category;
    public String priority;
    @JsonIgnore public Type medication;
    public BaseReference subject;
    public List<BaseReference> supportingInformation;
    public Date authoredOn;
    public BaseReference requester;
    public BaseReference recorder;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public List<BaseAnnotation> note;
    public List<BaseDosage> dosageInstruction;
    public MedicationRequestDispenseRequestEntity dispenseRequest;//
    public MedicationRequestSubstitutionEntity substitution;//
    public BaseReference priorPrescription;
    public List<BaseReference> detectedIssue;
    public List<BaseReference> eventHistory;
}
