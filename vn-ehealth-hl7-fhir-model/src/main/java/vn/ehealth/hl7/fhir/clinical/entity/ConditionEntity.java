package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "condition")
public class ConditionEntity extends BaseResource {

    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public CodeableConcept clinicalStatus;
    public CodeableConcept verificationStatus;
    public List<CodeableConcept> category;
    public CodeableConcept severity;
    public CodeableConcept code;
    public List<CodeableConcept> bodySite;
    public BaseReference subject;
    //public BaseReference context;
    public Type onset;
    public Type abatement;
    //public Date assertedDate;
    public BaseReference asserter;
    public List<ConditionStageEntity> stage;
    public List<ConditionEvidenceEntity> evidence;
    public List<BaseAnnotation> note;
    
    public static ConditionEntity fromCondition(Condition obj) {
        if(obj == null) return null;
        
        var ent = new ConditionEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.clinicalStatus = obj.getClinicalStatus();
        ent.verificationStatus = obj.getVerificationStatus();
        ent.category = obj.getCategory();
        ent.severity = obj.getSeverity();
        ent.code = obj.getCode();
        ent.bodySite = obj.getBodySite();
        ent.subject = BaseReference.fromReference(obj.getSubject());
        ent.onset = obj.getOnset();
        ent.abatement = obj.getAbatement();
        ent.asserter = BaseReference.fromReference(obj.getAsserter());
        ent.stage = transform(obj.getStage(), ConditionStageEntity::fromConditionStageComponent);
        ent.evidence = transform(obj.getEvidence(),ConditionEvidenceEntity::fromConditionEvidenceComponent);
        ent.note = BaseAnnotation.fromAnnotationList(obj.getNote());
        
        return ent;
    }
    
    
    public static Condition toCondition(ConditionEntity ent) {
        if(ent == null) return null;
        
        var obj = new Condition();
        
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setClinicalStatus(ent.clinicalStatus);
        obj.setVerificationStatus(ent.verificationStatus);
        obj.setCategory(ent.category);
        obj.setSeverity(ent.severity);
        obj.setCode(ent.code);
        obj.setBodySite(ent.bodySite);
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setOnset(ent.onset);
        obj.setAbatement(ent.abatement);
        obj.setAsserter(BaseReference.toReference(ent.asserter));
        obj.setStage(transform(ent.stage, ConditionStageEntity::toConditionStageComponent));
        obj.setEvidence(transform(ent.evidence, ConditionEvidenceEntity::toConditionEvidenceComponent));
        obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
        
        return obj;
    }
}
