package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.Date;


import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.ClinicalImpression;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.UriType;
import org.hl7.fhir.r4.model.ClinicalImpression.ClinicalImpressionStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "clinicalImpression")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ClinicalImpressionEntity extends BaseResource {

    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public BaseCodeableConcept code;
    public String description;
    public BaseReference subject;
    public BaseReference encounter;
    public Type effective;
    public Date date;
    public BaseReference assessor;
    public BaseReference previous;
    public List<BaseReference> problem;
    public List<ClinicalInvestigationEntity> investigation;
    public List<String> protocol;
    public String summary;
    public List<ClinicalFindingEntity> finding;
    public List<BaseCodeableConcept> prognosisCodeableConcept;
    public List<BaseReference> prognosisReference;
    public List<BaseAnnotation> note;
    
    public static ClinicalImpressionEntity fromClinicalImpression(ClinicalImpression obj) {
        if(obj == null) return null;
        
        var ent = new ClinicalImpressionEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.code = BaseCodeableConcept.fromCodeableConcept(obj.getCode());
        ent.description = obj.getDescription();
        ent.subject = BaseReference.fromReference(obj.getSubject());
        ent.encounter = BaseReference.fromReference(obj.getEncounter());
        ent.effective = obj.getEffective();
        ent.date = obj.getDate();
        ent.assessor = BaseReference.fromReference(obj.getAssessor());
        ent.previous = BaseReference.fromReference(obj.getPrevious());
        ent.problem = BaseReference.fromReferenceList(obj.getProblem());
        ent.investigation = transform(obj.getInvestigation(), ClinicalInvestigationEntity::fromClinicalImpressionInvestigationComponent);
        ent.protocol = transform(obj.getProtocol(), x -> x.getValue());
        ent.summary = obj.getSummary();
        ent.finding = transform(obj.getFinding(),ClinicalFindingEntity::fromClinicalImpressionFindingComponent);
        ent.prognosisCodeableConcept = BaseCodeableConcept.fromCodeableConcept(obj.getPrognosisCodeableConcept());
        ent.prognosisReference = BaseReference.fromReferenceList(obj.getPrognosisReference());
        ent.note = BaseAnnotation.fromAnnotationList(obj.getNote());
        
        return ent;
    }
    
    public static ClinicalImpression toClinicalImpression(ClinicalImpressionEntity ent) {
        if(ent == null) return null;
        
        var obj = new ClinicalImpression();
        
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setStatus(ClinicalImpressionStatus.fromCode(ent.status));
        obj.setCode(BaseCodeableConcept.toCodeableConcept(ent.code));
        obj.setDescription(ent.description);
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setEncounter(BaseReference.toReference(ent.encounter));
        obj.setEffective(ent.effective);
        obj.setDate(ent.date);
        obj.setAssessor(BaseReference.toReference(ent.assessor));
        obj.setPrevious(BaseReference.toReference(ent.previous));
        obj.setProblem(BaseReference.toReferenceList(ent.problem));
        obj.setInvestigation(transform(ent.investigation, ClinicalInvestigationEntity::toClinicalImpressionInvestigationComponent));
        obj.setProtocol(transform(ent.protocol, x -> new UriType(x)));
        obj.setSummary(ent.summary);
        obj.setFinding(transform(ent.finding, ClinicalFindingEntity::toClinicalImpressionFindingComponent));
        obj.setPrognosisCodeableConcept(BaseCodeableConcept.toCodeableConcept(ent.prognosisCodeableConcept));
        obj.setPrognosisReference(BaseReference.toReferenceList(ent.prognosisReference));
        obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
        
        return obj;
    }
}
