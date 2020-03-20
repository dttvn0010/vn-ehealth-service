package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Procedure.ProcedureStatus;
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
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "procedure")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1, 'basedOn.reference':1, 'subject.reference':1, 'encounter.reference':1}", name = "index_by_default")
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
    public BaseReference encounter;
    @JsonIgnore public Type performed;
    public Date performedDate;
    public BaseReference recorder;
    public BaseReference asserter;
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
        
        ent.identifier = obj.hasIdentifier()? BaseIdentifier.fromIdentifierList(obj.getIdentifier()) : null;
        ent.basedOn = obj.hasBasedOn()? BaseReference.fromReferenceList(obj.getBasedOn()) : null;
        ent.partOf = obj.hasPartOf()? BaseReference.fromReferenceList(obj.getPartOf()) : null;
        ent.status = obj.hasStatus()? Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null) : null;
        ent.category = obj.hasCategory()? BaseCodeableConcept.fromCodeableConcept(obj.getCategory()) : null;
        ent.code = obj.hasCode()? BaseCodeableConcept.fromCodeableConcept(obj.getCode()) : null;
        ent.subject = obj.hasSubject()? BaseReference.fromReference(obj.getSubject()) : null;
        ent.encounter = obj.hasEncounter()? BaseReference.fromReference(obj.getEncounter()) : null;
        ent.performed = obj.hasPerformed()? obj.getPerformed() : null;
        ent.performedDate = obj.hasPerformedDateTimeType()? obj.getPerformedDateTimeType().getValue() : null;
        ent.recorder = obj.hasRecorder()? BaseReference.fromReference(obj.getRecorder()) : null;
        ent.asserter = obj.hasAsserter()? BaseReference.fromReference(obj.getAsserter()) : null;
        ent.performer = obj.hasPerformer()? transform(obj.getPerformer(), ProcedurePerformerEntity::fromProcedurePerformerComponent) : null;
        ent.location = obj.hasLocation()? BaseReference.fromReference(obj.getLocation()) : null;
        ent.reasonCode = obj.hasReasonCode()? BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode()) : null;
        ent.bodySite = obj.hasBodySite()? BaseCodeableConcept.fromCodeableConcept(obj.getBodySite()) : null;
        ent.outcome = obj.hasOutcome()? BaseCodeableConcept.fromCodeableConcept(obj.getOutcome()) : null;
        ent.report = obj.hasReport()? BaseReference.fromReferenceList(obj.getReport()) : null;
        ent.complication = obj.hasComplication()? BaseCodeableConcept.fromCodeableConcept(obj.getComplication()) : null;
        ent.complicationDetail = obj.hasComplicationDetail()? BaseReference.fromReferenceList(obj.getComplicationDetail()) : null;
        ent.followUp = obj.hasFollowUp()? BaseCodeableConcept.fromCodeableConcept(obj.getFollowUp()) : null;
        ent.note = obj.hasNote()? BaseAnnotation.fromAnnotationList(obj.getNote()) : null;
        ent.focalDevice = obj.hasFocalDevice()? transform(obj.getFocalDevice(), ProcedureFocalDeviceEntity::fromProcedureFocalDeviceComponent) : null;
        ent.usedReference = obj.hasUsedReference()? BaseReference.fromReferenceList(obj.getUsedReference()) : null;
        ent.usedCode = obj.hasUsedCode()? BaseCodeableConcept.fromCodeableConcept(obj.getUsedCode()) : null;
        
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
        obj.setEncounter(BaseReference.toReference(ent.encounter));
        obj.setPerformed(ent.performed);
        if(ent.performedDate != null) obj.setPerformed(new DateTimeType(ent.performedDate));
        obj.setRecorder(BaseReference.toReference(ent.recorder));
        obj.setAsserter(BaseReference.toReference(ent.asserter));
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
    
    public String getOutcome() {
        String text = "";
        if(outcome != null) text = outcome.text;
        return text != null? text : "";
    }
    
    public String getFollowUp() {
        String text = "";
        if(followUp != null && followUp.size() > 0) {
            text = followUp.get(0).text;
        }
        return text != null? text : "";
    }
}
