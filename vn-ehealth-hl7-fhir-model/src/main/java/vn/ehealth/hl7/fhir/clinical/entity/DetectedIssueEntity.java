package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.DetectedIssue;
import org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueSeverity;
import org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "detectedIssue")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class DetectedIssueEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    //public CodeableConcept category;
    public String severity;
    public BaseReference patient;
    //public Date date;
    public BaseReference author;
    public List<BaseReference> implicated;
    public String detail;
    public String reference;
    
    
    public static DetectedIssueEntity fromDetectedIssue(DetectedIssue obj) {
        if(obj == null) return null;
        
        var ent = new DetectedIssueEntity();
        
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.severity = Optional.ofNullable(obj.getSeverity()).map(x -> x.toCode()).orElse(null);
        ent.patient = BaseReference.fromReference(obj.getPatient());
        ent.author = BaseReference.fromReference(obj.getAuthor());
        ent.implicated = BaseReference.fromReferenceList(obj.getImplicated());
        ent.detail = obj.getDetail();
        ent.reference = obj.getReference();
        
        return ent;
    }
    
    public static DetectedIssue toDetectedIssue(DetectedIssueEntity ent) {
        if(ent == null) return null;
        
        var obj = new DetectedIssue();
        
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setStatus(DetectedIssueStatus.fromCode(ent.status));
        obj.setSeverity(DetectedIssueSeverity.fromCode(ent.severity));
        obj.setPatient(BaseReference.toReference(ent.patient));
        obj.setAuthor(BaseReference.toReference(ent.author));
        obj.setImplicated(BaseReference.toReferenceList(ent.implicated));
        obj.setDetail(ent.detail);
        obj.setReference(ent.reference);
        
        return obj;
    }

}
