package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.CareTeam.CareTeamStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "careTeam")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class CareTeamEntity extends BaseResource{
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public List<BaseCodeableConcept> category;
    public String name;
    public BaseReference subject;
    //public BaseReference context;
    public BasePeriod period;
    public List<CareTeamParticipantEntity> participant;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public List<BaseReference> managingOrganization;
    public List<BaseAnnotation> note;
    
    public static CareTeamEntity fromCareTeam(CareTeam obj) {
        if(obj == null) return null;
        
        var ent = new CareTeamEntity();
        
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.category = BaseCodeableConcept.fromCodeableConcept(obj.getCategory());
        ent.name = obj.getName();
        ent.subject = BaseReference.fromReference(obj.getSubject());
        ent.period = BasePeriod.fromPeriod(obj.getPeriod());
        
        ent.participant = transform(obj.getParticipant(),
                                        CareTeamParticipantEntity::fromCareTeamParticipantComponent);
        
        ent.reasonCode = BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode());
        ent.reasonReference = BaseReference.fromReferenceList(obj.getReasonReference());
        ent.managingOrganization = BaseReference.fromReferenceList(obj.getManagingOrganization());
        ent.note = BaseAnnotation.fromAnnotationList(obj.getNote());
                
        return ent;
    }
    
    public static CareTeam toCareTeam(CareTeamEntity ent) {
        if(ent == null) return null;
        
        var obj = new CareTeam();
        
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setStatus(CareTeamStatus.fromCode(ent.status));
        obj.setCategory(BaseCodeableConcept.toCodeableConcept(ent.category));
        obj.setName(ent.name);
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        
        obj.setParticipant(transform(ent.participant,
                                    CareTeamParticipantEntity::toCareTeamParticipantComponent));
        
        obj.setReasonCode(BaseCodeableConcept.toCodeableConcept(ent.reasonCode));
        obj.setReasonReference(BaseReference.toReferenceList(ent.reasonReference));
        obj.setManagingOrganization(BaseReference.toReferenceList(ent.managingOrganization));
        obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
        return obj;        
    }
}
