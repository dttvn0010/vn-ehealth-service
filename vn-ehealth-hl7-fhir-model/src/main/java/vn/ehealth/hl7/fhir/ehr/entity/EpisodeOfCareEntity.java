package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.EpisodeOfCare;
import org.hl7.fhir.r4.model.EpisodeOfCare.DiagnosisComponent;
import org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "episodeOfCare")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1, 'patient.reference':1, 'managingOrganization.reference':1, 'careManager.reference':1, 'team.reference':1}", name = "index_by_default")
public class EpisodeOfCareEntity extends BaseResource{
    
    public static class DiagnosisEntity {
        public BaseReference condition;
        public BaseCodeableConcept role;
        public int rank;
        
        public static DiagnosisEntity fromDiagnosisComponent(DiagnosisComponent obj) {
            if(obj == null) return null;
            
            var ent = new DiagnosisEntity();
                    
            ent.condition = BaseReference.fromReference(obj.getCondition());
            ent.role = BaseCodeableConcept.fromCodeableConcept(obj.getRole());
            ent.rank = obj.getRank();
                    
            return ent;
        }
        
        public static DiagnosisComponent toDiagnosisComponent(DiagnosisEntity ent) {
            if(ent == null) return null;
            
            var obj = new DiagnosisComponent();
            
            obj.setCondition(BaseReference.toReference(ent.condition));
            obj.setRole(BaseCodeableConcept.toCodeableConcept(ent.role));
            obj.setRank(ent.rank);
            
            return obj;
        }
    }
    
    
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public List<EOCStatusHistoryEntity> statusHistory;
    public List<BaseCodeableConcept> type;
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
        
        ent.identifier = obj.hasIdentifier()? BaseIdentifier.fromIdentifierList(obj.getIdentifier()) : null;
        ent.status = obj.hasStatus()? Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null) : null;
        
        ent.statusHistory = obj.hasStatusHistory()? transform(obj.getStatusHistory(), 
                                EOCStatusHistoryEntity::fromEpisodeOfCareStatusHistoryComponent) : null;
                
        ent.type = obj.hasType()? BaseCodeableConcept.fromCodeableConcept(obj.getType()) : null;
        
        ent.diagnosis = obj.hasDiagnosis()? transform(obj.getDiagnosis(), DiagnosisEntity::fromDiagnosisComponent) : null;
        
        ent.patient = obj.hasPatient()? BaseReference.fromReference(obj.getPatient()) : null;
        
        ent.managingOrganization = obj.hasManagingOrganization()? 
                                    BaseReference.fromReference(obj.getManagingOrganization()) : null;
                                    
        ent.period = obj.hasPeriod()? BasePeriod.fromPeriod(obj.getPeriod()) : null;
                                    
        ent.referralRequest = obj.hasReferralRequest()? BaseReference.fromReferenceList(obj.getReferralRequest()) : null;
        
        ent.careManager = obj.hasCareManager()? BaseReference.fromReference(obj.getCareManager()) : null;
        
        ent.team = obj.hasTeam()? BaseReference.fromReferenceList(obj.getTeam()) : null;
        
        return ent;
    }
    
    public static EpisodeOfCare toEpisodeOfCare(EpisodeOfCareEntity ent) {
        if(ent == null) return null;
        
        var obj = new EpisodeOfCare();
        
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setStatus(EpisodeOfCareStatus.fromCode(ent.status));
        
        obj.setStatusHistory(transform(ent.statusHistory, 
                                EOCStatusHistoryEntity::toEpisodeOfCareStatusHistoryComponent));

        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setDiagnosis(transform(ent.diagnosis, DiagnosisEntity::toDiagnosisComponent));
        obj.setPatient(BaseReference.toReference(ent.patient));
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        obj.setManagingOrganization(BaseReference.toReference(ent.managingOrganization));
        obj.setReferralRequest(BaseReference.toReferenceList(ent.referralRequest));
        obj.setCareManager(BaseReference.toReference(ent.careManager));
        obj.setTeam(BaseReference.toReferenceList(ent.team));
        
        return obj;
    }
    
}
