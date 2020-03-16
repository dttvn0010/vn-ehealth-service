package vn.ehealth.hl7.fhir.medication.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.MedicationDispense;
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
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "medicationDispense")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class MedicationDispenseEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseReference> partOf;
    //Enumeration<MedicationDispenseStatus>
    public String status;
    public BaseCodeableConcept category;
    @JsonIgnore public Type medication;
    public BaseReference subject;
    //public BaseReference context;
    public List<BaseReference> supportingInformation;
    public List<MedicationDispensePerformerEntity> performer;
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
    public MedicationDispenseSubstitutionEntity substitution;
    public List<BaseReference> detectedIssue;
    //public Boolean notDone;
    //public Type notDoneReason;
    public List<BaseReference> eventHistory;
    
    public static MedicationDispenseEntity fromMedicationDispense(MedicationDispense obj) {
        if(obj == null) return null;
        
        var ent = new  MedicationDispenseEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.partOf = BaseReference.fromReferenceList(obj.getPartOf());
        ent.status = obj.getStatus();
        ent.category = BaseCodeableConcept.fromCodeableConcept(obj.getCategory());
        ent.medication = obj.getMedication();
        ent.subject = BaseReference.fromReference(obj.getSubject());
        ent.supportingInformation = BaseReference.fromReferenceList(obj.getSupportingInformation());
        ent.performer = transform(obj.getPerformer(),  MedicationDispensePerformerEntity::fromMedicationDispensePerformerComponent);
        ent.authorizingPrescription = BaseReference.fromReferenceList(obj.getAuthorizingPrescription());
        ent.type = BaseCodeableConcept.fromCodeableConcept(obj.getType());
        ent.quantity = BaseQuantity.fromQuantity(obj.getQuantity());
        ent.daysSupply = BaseQuantity.fromQuantity(obj.getDaysSupply());
        ent.whenPrepared = obj.getWhenPrepared();
        ent.whenHandedOver = obj.getWhenHandedOver();
        ent.destination = BaseReference.fromReference(obj.getDestination());
        ent.receiver = BaseReference.fromReferenceList(obj.getReceiver());
        ent.note = BaseAnnotation.fromAnnotationList(obj.getNote());
        ent.dosageInstruction = BaseDosage.fromDosageList(obj.getDosageInstruction());
        ent.substitution = MedicationDispenseSubstitutionEntity.fromMedicationDispenseSubstitutionComponent(obj.getSubstitution());
        ent.detectedIssue = BaseReference.fromReferenceList(obj.getDetectedIssue());
        ent.eventHistory = BaseReference.fromReferenceList(obj.getEventHistory());
        return ent;
    }
    
    
    public static MedicationDispense toMedicationDispense(MedicationDispenseEntity ent) {
        if(ent == null) return null;
        
        var obj = new MedicationDispense();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setPartOf(BaseReference.toReferenceList(ent.partOf));
        obj.setStatus(ent.status);
        obj.setCategory(BaseCodeableConcept.toCodeableConcept(ent.category));
        obj.setMedication(ent.medication);
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setSupportingInformation(BaseReference.toReferenceList(ent.supportingInformation));
        obj.setPerformer(transform(ent.performer, MedicationDispensePerformerEntity::toMedicationDispensePerformerComponent));
        obj.setAuthorizingPrescription(BaseReference.toReferenceList(ent.authorizingPrescription));
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setQuantity(BaseQuantity.toQuantity(ent.quantity));
        obj.setDaysSupply(BaseQuantity.toQuantity(ent.daysSupply));
        obj.setWhenPrepared(ent.whenPrepared);
        obj.setWhenHandedOver(ent.whenHandedOver);
        obj.setDestination(BaseReference.toReference(ent.destination));
        obj.setReceiver(BaseReference.toReferenceList(ent.receiver));
        obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
        obj.setDosageInstruction(BaseDosage.toDosageList(ent.dosageInstruction));
        obj.setSubstitution(MedicationDispenseSubstitutionEntity.toMedicationDispenseSubstitutionComponent(ent.substitution));
        obj.setDetectedIssue(BaseReference.toReferenceList(ent.detectedIssue));
        obj.setEventHistory(BaseReference.toReferenceList(ent.eventHistory));
        return obj;
        
    }
}
