package vn.ehealth.hl7.fhir.medication.entity;

import java.util.Date;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.MedicationStatement.MedicationStatementStatus;
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

@Document(collection = "medicationStatement")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class MedicationStatementEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseReference> basedOn;
    public List<BaseReference> partOf;
    public BaseReference context;
    public String status;
    public BaseCodeableConcept category;
    @JsonIgnore public Type medication;
    @JsonIgnore public Type effective;
    public Date dateAsserted;
    public BaseReference informationSource;
    public BaseReference subject;
    public List<BaseReference> derivedFrom;
    //public String taken;
    //public List<CodeableConcept> reasonNotTaken;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public List<BaseAnnotation> note;
    public List<BaseDosage> dosage;
    /** dosage **/
    
    public static MedicationStatementEntity fromMedicationStatement(MedicationStatement obj) {
        if(obj == null) return null;
        var ent = new MedicationStatementEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.basedOn = BaseReference.fromReferenceList(obj.getBasedOn());
        ent.partOf = BaseReference.fromReferenceList(obj.getPartOf());
        ent.context = BaseReference.fromReference(obj.getContext());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.category = BaseCodeableConcept.fromCodeableConcept(obj.getCategory());
        ent.medication = obj.getMedication();
        ent.effective = obj.getEffective();
        ent.dateAsserted = obj.getDateAsserted();
        ent.informationSource = BaseReference.fromReference(obj.getInformationSource());
        ent.subject = BaseReference.fromReference(obj.getSubject());
        ent.derivedFrom = BaseReference.fromReferenceList(obj.getDerivedFrom());
        ent.reasonCode = BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode());
        ent.reasonReference = BaseReference.fromReferenceList(obj.getReasonReference());
        ent.note = BaseAnnotation.fromAnnotationList(obj.getNote());
        ent.dosage = BaseDosage.fromDosageList(obj.getDosage());
        return ent;
                
    }
    public static MedicationStatement toMedicationStatement(MedicationStatementEntity ent) {
        if(ent == null) return null;
        
        var obj = new MedicationStatement();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setBasedOn(BaseReference.toReferenceList(ent.basedOn));
        obj.setPartOf(BaseReference.toReferenceList(ent.partOf));
        obj.setContext(BaseReference.toReference(ent.context));        
        obj.setStatus(MedicationStatementStatus.fromCode(ent.status));
        obj.setCategory(BaseCodeableConcept.toCodeableConcept(ent.category));
        obj.setMedication(ent.medication);
        obj.setEffective(ent.effective);
        obj.setDateAsserted(ent.dateAsserted);
        obj.setInformationSource(BaseReference.toReference(ent.informationSource));
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setDerivedFrom(BaseReference.toReferenceList(ent.derivedFrom));
        obj.setReasonCode(BaseCodeableConcept.toCodeableConcept(ent.reasonCode));
        obj.setReasonReference(BaseReference.toReferenceList(ent.reasonReference));
        obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
        obj.setDosage(BaseDosage.toDosageList(ent.dosage));
        return obj;        
    }
}
