package vn.ehealth.hl7.fhir.medication.entity;

import java.util.Date;


import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestIntent;
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestPriority;
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestStatus;
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
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
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
    
    public static MedicationRequestEntity fromMedicationRequest(MedicationRequest obj) {
        if(obj == null) return null;
        var ent = new MedicationRequestEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.encounter = BaseReference.fromReference(obj.getEncounter());
        ent.basedOn = BaseReference.fromReferenceList(obj.getBasedOn());
        ent.groupIdentifier = BaseIdentifier.fromIdentifier(obj.getGroupIdentifier());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.intent = Optional.ofNullable(obj.getIntent()).map(x -> x.toCode()).orElse(null);
        ent.category = BaseCodeableConcept.fromCodeableConcept(obj.getCategory());
        ent.priority = Optional.ofNullable(obj.getPriority()).map(x -> x.toCode()).orElse(null);
        ent.medication = obj.getMedication();
        ent.subject = BaseReference.fromReference(obj.getSubject());
        ent.supportingInformation = BaseReference.fromReferenceList(obj.getSupportingInformation());
        ent.authoredOn = obj.getAuthoredOn();
        ent.requester = BaseReference.fromReference(obj.getRequester());
        ent.recorder = BaseReference.fromReference(obj.getRecorder());
        ent.reasonCode = BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode());
        ent.reasonReference = BaseReference.fromReferenceList(obj.getReasonReference()); 
        ent.note = BaseAnnotation.fromAnnotationList(obj.getNote());
        ent.dosageInstruction = BaseDosage.fromDosageList(obj.getDosageInstruction());
        ent.dispenseRequest = MedicationRequestDispenseRequestEntity.fromMedicationRequestDispenseRequestComponent(obj.getDispenseRequest());
        ent.substitution = MedicationRequestSubstitutionEntity.fromMedicationRequestSubstitutionComponent(obj.getSubstitution());
        ent.priorPrescription = BaseReference.fromReference(obj.getPriorPrescription());
        ent.detectedIssue = BaseReference.fromReferenceList(obj.getDetectedIssue());
        ent.eventHistory = BaseReference.fromReferenceList(obj.getEventHistory());
        return ent;
      
    }
    
    public static MedicationRequest toMedicationRequest(MedicationRequestEntity ent) {
        if(ent == null) return null;
        
        var obj = new MedicationRequest();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setEncounter(BaseReference.toReference(ent.encounter));
        obj.setBasedOn(BaseReference.toReferenceList(ent.basedOn));
        obj.setGroupIdentifier(BaseIdentifier.toIdentifier(ent.groupIdentifier));
        obj.setStatus(MedicationRequestStatus.fromCode(ent.status));
        obj.setIntent(MedicationRequestIntent.fromCode(ent.intent));
        obj.setCategory(BaseCodeableConcept.toCodeableConcept(ent.category));
        obj.setPriority(MedicationRequestPriority.fromCode(ent.priority));
        obj.setMedication(ent.medication);
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setSupportingInformation(BaseReference.toReferenceList(ent.supportingInformation));
        obj.setAuthoredOn(ent.authoredOn);
        obj.setRequester(BaseReference.toReference(ent.requester));
        obj.setRecorder(BaseReference.toReference(ent.recorder));
        obj.setReasonCode(BaseCodeableConcept.toCodeableConcept(ent.reasonCode));
        obj.setReasonReference(BaseReference.toReferenceList(ent.reasonReference));
        obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
        obj.setDosageInstruction(BaseDosage.toDosageList(ent.dosageInstruction));
        obj.setDispenseRequest(MedicationRequestDispenseRequestEntity.toMedicationRequestDispenseRequestComponent(ent.dispenseRequest));
        obj.setSubstitution(MedicationRequestSubstitutionEntity.toMedicationRequestSubstitutionComponent(ent.substitution));
        obj.setPriorPrescription(BaseReference.toReference(ent.priorPrescription));        
        obj.setDetectedIssue(BaseReference.toReferenceList(ent.detectedIssue));
        obj.setEventHistory(BaseReference.toReferenceList(ent.eventHistory));
        return obj;
    }
}
