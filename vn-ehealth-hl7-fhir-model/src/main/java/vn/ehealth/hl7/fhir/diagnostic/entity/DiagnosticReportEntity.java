package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;


import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "diagnosticReport")
public class DiagnosticReportEntity extends BaseResource {

    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseReference> basedOn;
    public String status;
    public List<BaseCodeableConcept> category;
    public BaseCodeableConcept code;
    public BaseReference subject;
    //public BaseReference context;
    public Type effective;
    public Date issued;
    public List<BaseReference> performer;
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
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.basedOn = BaseReference.fromReferenceList(obj.getBasedOn());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.category = BaseCodeableConcept.fromCodeableConcept(obj.getCategory());
        ent.code = BaseCodeableConcept.fromCodeableConcept(obj.getCode());
        ent.subject = BaseReference.fromReference(obj.getSubject());
        ent.effective = obj.getEffective();
        ent.issued = obj.getIssued();
        ent.performer = BaseReference.fromReferenceList(obj.getPerformer());
        ent.specimen = BaseReference.fromReferenceList(obj.getSpecimen());
        ent.result = BaseReference.fromReferenceList(obj.getResult());
        ent.imagingStudy = BaseReference.fromReferenceList(obj.getImagingStudy());
        ent.media = transform(obj.getMedia(), DiagnosticReportMediaEntity::fromDiagnosticReportMediaComponent);                
        ent.conclusion = obj.getConclusion();
        ent.presentedForm = BaseAttachment.fromAttachmentList(obj.getPresentedForm());
         
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
        obj.setEffective(ent.effective);
        obj.setIssued(ent.issued);
        obj.setPerformer(BaseReference.toReferenceList(ent.performer));
        obj.setSpecimen(BaseReference.toReferenceList(ent.specimen));
        obj.setResult(BaseReference.toReferenceList(ent.result));
        obj.setImagingStudy(BaseReference.toReferenceList(ent.imagingStudy));
        obj.setMedia(transform(ent.media, DiagnosticReportMediaEntity::toDiagnosticReportMediaComponent));
        obj.setConclusion(ent.conclusion);
        obj.setPresentedForm(BaseAttachment.toAttachmentList(ent.presentedForm));
        
        return obj;
    }
}
