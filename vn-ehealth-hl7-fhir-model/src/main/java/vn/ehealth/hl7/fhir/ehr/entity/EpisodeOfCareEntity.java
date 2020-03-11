package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.EpisodeOfCare;
import org.hl7.fhir.r4.model.EpisodeOfCare.DiagnosisComponent;
import org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "episodeOfCare")
public class EpisodeOfCareEntity extends BaseResource{
    
    public static class DiagnosisEntity {
        public BaseReference condition;
        public CodeableConcept role;
        public int rank;
        
        public static DiagnosisEntity fromDiagnosisComponent(DiagnosisComponent obj) {
            if(obj == null) return null;
            
            var ent = new DiagnosisEntity();
                    
            ent.condition = BaseReference.fromReference(obj.getCondition());
            ent.role = obj.getRole();
            ent.rank = obj.getRank();
                    
            return ent;
        }
        
        public static DiagnosisComponent toDiagnosisComponent(DiagnosisEntity ent) {
            if(ent == null) return null;
            
            var obj = new DiagnosisComponent();
            
            obj.setCondition(BaseReference.toReference(ent.condition));
            obj.setRole(ent.role);
            obj.setRank(ent.rank);
            
            return obj;
        }
    }
    
    
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public List<EOCStatusHistoryEntity> statusHistory;
    public List<CodeableConcept> type;
    public List<DiagnosisEntity> diagnosis;
    public BaseReference patient;
    public BaseReference managingOrganization;
    public BasePeriod period;
    public List<BaseReference> referralRequest;
    public BaseReference careManager;
    public List<BaseReference> team;
    
    public static EpisodeOfCareEntity fromEpisodeOfCare(EpisodeOfCare obj) {
        if(obj == null) return null;
        
        var ent = new EpisodeOfCareEntity();
        
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        
        ent.statusHistory = transform(obj.getStatusHistory(), 
                EOCStatusHistoryEntity::fromEpisodeOfCareStatusHistoryComponent);
        
        
        ent.type = obj.getType();
        
        ent.diagnosis = transform(obj.getDiagnosis(), DiagnosisEntity::fromDiagnosisComponent);
        
        ent.patient = BaseReference.fromReference(obj.getPatient());
        ent.managingOrganization = BaseReference.fromReference(obj.getManagingOrganization());
        ent.referralRequest = BaseReference.fromReferenceList(obj.getReferralRequest());
        ent.careManager = BaseReference.fromReference(obj.getCareManager());
        ent.team = BaseReference.fromReferenceList(obj.getTeam());
        
        return ent;
    }
    
    public static EpisodeOfCare toEpisodeOfCare(EpisodeOfCareEntity ent) {
        if(ent == null) return null;
        
        var obj = new EpisodeOfCare();
        
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setStatus(EpisodeOfCareStatus.fromCode(ent.status));
        
        obj.setStatusHistory(transform(ent.statusHistory, 
                                EOCStatusHistoryEntity::toEpisodeOfCareStatusHistoryComponent));

        obj.setType(ent.type);
        obj.setDiagnosis(transform(ent.diagnosis, DiagnosisEntity::toDiagnosisComponent));
        obj.setPatient(BaseReference.toReference(ent.patient));
        obj.setManagingOrganization(BaseReference.toReference(ent.managingOrganization));
        obj.setReferralRequest(BaseReference.toReferenceList(ent.referralRequest));
        obj.setCareManager(BaseReference.toReference(ent.careManager));
        obj.setTeam(BaseReference.toReferenceList(ent.team));
        
        return obj;
    }
    
}
