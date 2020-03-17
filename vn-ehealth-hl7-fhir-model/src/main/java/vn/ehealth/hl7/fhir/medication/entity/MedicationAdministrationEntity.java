package vn.ehealth.hl7.fhir.medication.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.MedicationAdministration;
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

@Document(collection = "medicationAdministration")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class MedicationAdministrationEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    //    public List<BaseReference> definition;
    public List<BaseReference> partOf;
    public String status;
    public BaseCodeableConcept category;
    public Type medication;
    public BaseReference request;
    public BaseReference subject;
    public BaseReference context;
    public List<BaseReference> supportingInformation;
    @JsonIgnore public Type effective;
    /** performer **/
    //public Boolean notGiven;
    //public List<CodeableConcept> reasonNotGiven;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    //public BaseReference prescription;
    public List<BaseReference> device;
    public List<BaseAnnotation> note;
    /** dosage **/
    public List<BaseReference> eventHistory;
    
    public static MedicationAdministrationEntity fromMedicationAdministration(MedicationAdministration obj) {
        if(obj == null) return null;
        
        var ent = new MedicationAdministrationEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.partOf = BaseReference.fromReferenceList(obj.getPartOf());
        ent.status = obj.getStatus();
        ent.category = BaseCodeableConcept.fromCodeableConcept(obj.getCategory());
        ent.medication = obj.getMedication();
        ent.request = BaseReference.fromReference(obj.getRequest());
        ent.subject = BaseReference.fromReference(obj.getSubject());
        ent.context = BaseReference.fromReference(obj.getContext());
        ent.supportingInformation = BaseReference.fromReferenceList(obj.getSupportingInformation());
        ent.effective = obj.getEffective();
        ent.reasonCode = BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode());
        ent.reasonReference = BaseReference.fromReferenceList(obj.getReasonReference());
        ent.device = BaseReference.fromReferenceList(obj.getDevice());
        ent.note = BaseAnnotation.fromAnnotationList(obj.getNote());
        ent.eventHistory = BaseReference.fromReferenceList(obj.getEventHistory());
        return ent;
    }
    
    public static MedicationAdministration toMedicationAdministration(MedicationAdministrationEntity ent) {
        if(ent == null) return null;
        
        var obj = new MedicationAdministration();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setPartOf(BaseReference.toReferenceList(ent.partOf));
        obj.setStatus(ent.status);
        obj.setCategory(BaseCodeableConcept.toCodeableConcept(ent.category));
        obj.setMedication(ent.medication);
        obj.setRequest(BaseReference.toReference(ent.request));
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setContext(BaseReference.toReference(ent.context));
        obj.setSupportingInformation(BaseReference.toReferenceList(ent.supportingInformation));
        obj.setEffective(ent.effective);
        obj.setReasonCode(BaseCodeableConcept.toCodeableConcept(ent.reasonCode));
        obj.setReasonReference(BaseReference.toReferenceList(ent.reasonReference));
        obj.setDevice(BaseReference.toReferenceList(ent.device));
        obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
        obj.setEventHistory(BaseReference.toReferenceList(ent.eventHistory));
        return obj;
    }

}
