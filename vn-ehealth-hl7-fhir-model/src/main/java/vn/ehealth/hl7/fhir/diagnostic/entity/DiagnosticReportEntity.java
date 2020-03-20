package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;


import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "diagnosticReport")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1, 'basedOn.reference':1, 'subject.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class DiagnosticReportEntity extends BaseResource {

    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseReference> basedOn;
    public String status;
    public List<BaseCodeableConcept> category;
    public BaseCodeableConcept code;
    public BaseReference subject;
    public BaseReference encounter;
    @JsonIgnore public Type effective;
    public Date issued;
    public List<BaseReference> performer;    
    public List<BaseReference> resultsInterpreter;
    public List<BaseReference> specimen;
    public List<BaseReference> result;
    public List<BaseReference> imagingStudy;
    public List<DiagnosticReportMediaEntity> media;
    public String conclusion;
    //public List<CodeableConcept> codedDiagnosis;
    public List<BaseAttachment> presentedForm;
    
    public static DiagnosticReportEntity fromDiagnosticReport(DiagnosticReport obj) {
        if(obj == null) return null;
        
        var ent = new DiagnosticReportEntity();
        ent.identifier = obj.hasIdentifier()? BaseIdentifier.fromIdentifierList(obj.getIdentifier()) : null;
        ent.basedOn = obj.hasBasedOn()? BaseReference.fromReferenceList(obj.getBasedOn()) : null;
        ent.status = obj.hasStatus()? Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null) : null;
        ent.category = obj.hasCategory()? BaseCodeableConcept.fromCodeableConcept(obj.getCategory()) : null;
        ent.code = obj.hasCode()? BaseCodeableConcept.fromCodeableConcept(obj.getCode()) : null;
        ent.subject = obj.hasSubject()? BaseReference.fromReference(obj.getSubject()) : null;
        ent.encounter = obj.hasEncounter()? BaseReference.fromReference(obj.getEncounter()) : null;
        ent.effective = obj.hasEffective()? obj.getEffective() : null;
        ent.issued = obj.hasIssued()? obj.getIssued() : null;
        ent.performer = obj.hasPerformer()?  BaseReference.fromReferenceList(obj.getPerformer()) : null;
        ent.resultsInterpreter = obj.hasResultsInterpreter()? BaseReference.fromReferenceList(obj.getResultsInterpreter()) : null;
        ent.specimen = obj.hasSpecimen()? BaseReference.fromReferenceList(obj.getSpecimen()) : null;
        ent.result = obj.hasResult()? BaseReference.fromReferenceList(obj.getResult()) : null;
        ent.imagingStudy = obj.hasImagingStudy()? BaseReference.fromReferenceList(obj.getImagingStudy()) : null;
        ent.media = obj.hasMedia()? transform(obj.getMedia(), DiagnosticReportMediaEntity::fromDiagnosticReportMediaComponent) : null;                
        ent.conclusion = obj.hasConclusion()? obj.getConclusion() : null;
        ent.presentedForm = obj.hasPresentedForm()? BaseAttachment.fromAttachmentList(obj.getPresentedForm()) : null;
         
        return ent;
    }
    
    public static DiagnosticReport toDiagnosticReport(DiagnosticReportEntity ent) {
        if(ent == null) return null;
        
        var obj = new DiagnosticReport();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setBasedOn(BaseReference.toReferenceList(ent.basedOn));
        obj.setStatus(DiagnosticReportStatus.fromCode(ent.status));
        obj.setCategory(BaseCodeableConcept.toCodeableConcept(ent.category));
        obj.setCode(BaseCodeableConcept.toCodeableConcept(ent.code));
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setEncounter(BaseReference.toReference(ent.encounter));
        obj.setEffective(ent.effective);
        obj.setIssued(ent.issued);
        obj.setPerformer(BaseReference.toReferenceList(ent.performer));
        obj.setResultsInterpreter(BaseReference.toReferenceList(ent.resultsInterpreter));
        obj.setSpecimen(BaseReference.toReferenceList(ent.specimen));
        obj.setResult(BaseReference.toReferenceList(ent.result));
        obj.setImagingStudy(BaseReference.toReferenceList(ent.imagingStudy));
        obj.setMedia(transform(ent.media, DiagnosticReportMediaEntity::toDiagnosticReportMediaComponent));
        obj.setConclusion(ent.conclusion);
        obj.setPresentedForm(BaseAttachment.toAttachmentList(ent.presentedForm));
        
        return obj;
    }
    
    public BaseReference getFirstResultsInterpreter() {
        if(resultsInterpreter != null && resultsInterpreter.size() > 0) {
            return resultsInterpreter.get(0);
        }
        return null;
    }
    
    public BaseReference getFirstPeformer() {
        if(performer != null && performer.size() > 0) {
            return performer.get(0);
        }
        return null;
    }
}
