package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Encounter.DiagnosisComponent;
import org.hl7.fhir.r4.model.Encounter.EncounterStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseDuration;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "encounter")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class EncounterEntity extends BaseResource{
    public static class DiagnosisEntity {
        public BaseReference condition;
        public BaseCodeableConcept use;
        public int rank;
        
        public static DiagnosisEntity fromDiagnosisComponent(DiagnosisComponent obj) {
            if(obj == null) return null;
            
            var ent = new DiagnosisEntity();
                    
            ent.condition = BaseReference.fromReference(obj.getCondition());
            ent.use = BaseCodeableConcept.fromCodeableConcept(obj.getUse());
            ent.rank = obj.getRank();
                    
            return ent;
        }
        
        public static DiagnosisComponent toDiagnosisComponent(DiagnosisEntity ent) {
            if(ent == null) return null;
            
            var obj = new DiagnosisComponent();
            
            obj.setCondition(BaseReference.toReference(ent.condition));
            obj.setUse(BaseCodeableConcept.toCodeableConcept(ent.use));
            obj.setRank(ent.rank);
            
            return obj;
        }
    }
    
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public List<EncounterStatusHistoryEntity> statusHistory;
    public BaseCoding class_;
    public List<EncounterClassHistoryEntity> classHistory;
    public List<BaseCodeableConcept> type;
    public BaseCodeableConcept priority;
    public BaseReference subject;
    public List<BaseReference> episodeOfCare;
    public List<BaseReference> reasonReference;
    public List<EncounterParticipantEntity> participant;
    public List<BaseReference> appointment;
    public BasePeriod period;
    public BaseDuration length;
    public List<BaseCodeableConcept> reasonCode;
    public List<DiagnosisEntity> diagnosis;
    public HospitalizationEntity hospitalization;
    public List<EncounterLocationEntity> location;
    public BaseReference serviceProvider;
    public BaseReference partOf;
    
    public static EncounterEntity fromEncounter(Encounter obj) {
        if(obj == null) return null;
        
        var ent = new EncounterEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.statusHistory = transform(obj.getStatusHistory(), EncounterStatusHistoryEntity::fromStatusHistoryComponent);
        ent.class_ = BaseCoding.fromCoding(obj.getClass_());
        ent.classHistory = transform(obj.getClassHistory(), EncounterClassHistoryEntity::fromClassHistoryComponent);        
        ent.type = BaseCodeableConcept.fromCodeableConcept(obj.getType());
        ent.priority = BaseCodeableConcept.fromCodeableConcept(obj.getPriority());
        ent.episodeOfCare = BaseReference.fromReferenceList(obj.getEpisodeOfCare());
        ent.reasonReference = BaseReference.fromReferenceList(obj.getReasonReference());
        ent.participant = transform(obj.getParticipant(), EncounterParticipantEntity::fromEncounterParticipantComponent);
        ent.appointment = BaseReference.fromReferenceList(obj.getAppointment());
        ent.period = BasePeriod.fromPeriod(obj.getPeriod());
        ent.length = BaseDuration.fromDuration(obj.getLength());
        ent.reasonCode = BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode());
        ent.diagnosis = transform(obj.getDiagnosis(), DiagnosisEntity::fromDiagnosisComponent);
        ent.hospitalization = HospitalizationEntity.fromEncounterHospitalizationComponent(obj.getHospitalization());
        ent.location = transform(obj.getLocation(), EncounterLocationEntity::fromEncounterLocationComponent);
        ent.serviceProvider = BaseReference.fromReference(obj.getServiceProvider());
        ent.partOf = BaseReference.fromReference(obj.getPartOf());
        return ent;        
    }
    
    public static Encounter toEncounter(EncounterEntity ent) {
        if(ent == null) return null;
        
        var obj = new Encounter();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setStatus(EncounterStatus.fromCode(ent.status));
        obj.setStatusHistory(transform(ent.statusHistory, EncounterStatusHistoryEntity::toStatusHistoryComponent));
        obj.setClass_(BaseCoding.toCoding(ent.class_));
        obj.setClassHistory(transform(ent.classHistory, EncounterClassHistoryEntity::toClassHistoryComponent));
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setPriority(BaseCodeableConcept.toCodeableConcept(ent.priority));
        obj.setEpisodeOfCare(BaseReference.toReferenceList(ent.episodeOfCare));
        obj.setReasonReference(BaseReference.toReferenceList(ent.reasonReference));
        obj.setParticipant(transform(ent.participant, EncounterParticipantEntity::toEncounterParticipantComponent));
        obj.setAppointment(BaseReference.toReferenceList(ent.appointment));
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        obj.setLength(BaseDuration.toDuration(ent.length));
        obj.setReasonCode(BaseCodeableConcept.toCodeableConcept(ent.reasonCode));
        obj.setDiagnosis(transform(ent.diagnosis, DiagnosisEntity::toDiagnosisComponent));
        obj.setHospitalization(HospitalizationEntity.toEncounterHospitalizationComponent(ent.hospitalization));
        obj.setLocation(transform(ent.location, EncounterLocationEntity::toEncounterLocationComponent));
        obj.setServiceProvider(BaseReference.toReference(ent.serviceProvider));
        obj.setPartOf(BaseReference.toReference(ent.partOf));
        return obj;
        
    }
}
