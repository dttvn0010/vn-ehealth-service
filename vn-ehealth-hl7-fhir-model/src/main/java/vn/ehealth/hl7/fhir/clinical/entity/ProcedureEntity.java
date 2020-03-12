package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Procedure.ProcedureStatus;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "procedure")
public class ProcedureEntity extends BaseResource {

    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    //public List<BaseReference> definition;
    public List<BaseReference> basedOn;
    public List<BaseReference> partOf;
    public String status;
    //public Boolean notDone;
    //public CodeableConcept notDoneReason;
    public BaseCodeableConcept category;
    public BaseCodeableConcept code;
    public BaseReference subject;
    //public BaseReference context;
    public Type performed;
    public List<ProcedurePerformerEntity> performer;
    public BaseReference location;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public List<BaseCodeableConcept> bodySite;
    public BaseCodeableConcept outcome;
    public List<BaseReference> report;
    public List<BaseCodeableConcept> complication;
    public List<BaseReference> complicationDetail;
    public List<BaseCodeableConcept> followUp;
    public List<BaseAnnotation> note;
    public List<ProcedureFocalDeviceEntity> focalDevice;
    public List<BaseReference> usedReference;
    public List<BaseCodeableConcept> usedCode;
    
    public static ProcedureEntity fromProcedure(Procedure obj) {
        if(obj == null) return null;
        
        var ent = new ProcedureEntity();
        
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.basedOn = BaseReference.fromReferenceList(obj.getBasedOn());
        ent.partOf = BaseReference.fromReferenceList(obj.getPartOf());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.category = BaseCodeableConcept.fromCodeableConcept(obj.getCategory());
        ent.code = BaseCodeableConcept.fromCodeableConcept(obj.getCode());
        ent.subject = BaseReference.fromReference(obj.getSubject());
        ent.performed = obj.getPerformed();
        ent.performer = transform(obj.getPerformer(), ProcedurePerformerEntity::fromProcedurePerformerComponent);
        ent.location = BaseReference.fromReference(obj.getLocation());
        ent.reasonCode = BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode());
        ent.bodySite = BaseCodeableConcept.fromCodeableConcept(obj.getBodySite());
        ent.outcome = BaseCodeableConcept.fromCodeableConcept(obj.getOutcome());
        ent.report = BaseReference.fromReferenceList(obj.getReport());
        ent.complication = BaseCodeableConcept.fromCodeableConcept(obj.getComplication());
        ent.complicationDetail = BaseReference.fromReferenceList(obj.getComplicationDetail());
        ent.followUp = BaseCodeableConcept.fromCodeableConcept(obj.getFollowUp());
        ent.note = BaseAnnotation.fromAnnotationList(obj.getNote());
        ent.focalDevice = transform(obj.getFocalDevice(), ProcedureFocalDeviceEntity::fromProcedureFocalDeviceComponent);
        ent.usedReference = BaseReference.fromReferenceList(obj.getUsedReference());
        ent.usedCode = BaseCodeableConcept.fromCodeableConcept(obj.getUsedCode());
        
        return ent;
    }
    
    public static Procedure toProcedure(ProcedureEntity ent) {
        if(ent == null) return null;
        
        var obj = new Procedure();
        
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setBasedOn(BaseReference.toReferenceList(ent.basedOn));
        obj.setPartOf(BaseReference.toReferenceList(ent.partOf));
        obj.setStatus(ProcedureStatus.fromCode(ent.status));
        obj.setCategory(BaseCodeableConcept.toCodeableConcept(ent.category));
        obj.setCode(BaseCodeableConcept.toCodeableConcept(ent.code));
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setPerformed(ent.performed);
        obj.setPerformer(transform(ent.performer, ProcedurePerformerEntity::toProcedurePerformerComponent));
        obj.setLocation(BaseReference.toReference(ent.location));
        obj.setReasonCode(BaseCodeableConcept.toCodeableConcept(ent.reasonCode));
        obj.setBodySite(BaseCodeableConcept.toCodeableConcept(ent.bodySite));
        obj.setOutcome(BaseCodeableConcept.toCodeableConcept(ent.outcome));
        obj.setReport(BaseReference.toReferenceList(ent.report));
        obj.setComplication(BaseCodeableConcept.toCodeableConcept(ent.complication));
        obj.setComplicationDetail(BaseReference.toReferenceList(ent.complicationDetail));
        obj.setFollowUp(BaseCodeableConcept.toCodeableConcept(ent.followUp));
        obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
        obj.setFocalDevice(transform(ent.focalDevice, ProcedureFocalDeviceEntity::toProcedureFocalDeviceComponent));
        obj.setUsedReference(BaseReference.toReferenceList(ent.usedReference));
        obj.setUsedCode(BaseCodeableConcept.toCodeableConcept(ent.usedCode));
        
        return obj;
    }
}
