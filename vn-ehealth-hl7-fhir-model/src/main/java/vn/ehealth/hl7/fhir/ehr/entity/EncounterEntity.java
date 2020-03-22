package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

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
import vn.ehealth.hl7.fhir.core.util.FPUtil;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "encounter")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1, 'serviceProvider.reference':1, 'partOf.reference':1, 'episodeOfCare.reference':1, 'subject.reference':1}", name = "index_by_default")
public class EncounterEntity extends BaseResource{
    public static class DiagnosisEntity {
        public BaseReference condition;
        public BaseCodeableConcept use;
        public Integer rank;
        
        public static DiagnosisEntity fromDiagnosisComponent(DiagnosisComponent obj) {
            if(obj == null) return null;
            
            var ent = new DiagnosisEntity();
                    
            ent.condition = obj.hasCondition()? BaseReference.fromReference(obj.getCondition()) : null;
            ent.use = obj.hasUse()? BaseCodeableConcept.fromCodeableConcept(obj.getUse()) : null;
            ent.rank = obj.hasRank()? obj.getRank() : null;
                    
            return ent;
        }
        
        public static DiagnosisComponent toDiagnosisComponent(DiagnosisEntity ent) {
            if(ent == null) return null;
            
            var obj = new DiagnosisComponent();
            
            obj.setCondition(BaseReference.toReference(ent.condition));
            obj.setUse(BaseCodeableConcept.toCodeableConcept(ent.use));
            if(ent.rank != null) obj.setRank(ent.rank);
            
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
        ent.identifier = obj.hasIdentifier()? BaseIdentifier.fromIdentifierList(obj.getIdentifier()) : null;
        ent.status = obj.hasStatus()? Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null) : null;
        ent.statusHistory = obj.hasStatusHistory()? transform(obj.getStatusHistory(), EncounterStatusHistoryEntity::fromStatusHistoryComponent) : null;
        ent.class_ = obj.hasClass_()? BaseCoding.fromCoding(obj.getClass_())  : null;
        ent.classHistory = obj.hasClassHistory()? transform(obj.getClassHistory(), EncounterClassHistoryEntity::fromClassHistoryComponent) : null;        
        ent.type = obj.hasType()? BaseCodeableConcept.fromCodeableConcept(obj.getType()) : null;
        ent.priority = obj.hasPriority()? BaseCodeableConcept.fromCodeableConcept(obj.getPriority()) : null;
        ent.subject = obj.hasSubject()? BaseReference.fromReference(obj.getSubject()) : null;
        ent.episodeOfCare = obj.hasEpisodeOfCare()? BaseReference.fromReferenceList(obj.getEpisodeOfCare()) : null;
        ent.reasonReference = obj.hasReasonReference()? BaseReference.fromReferenceList(obj.getReasonReference()) : null;
        ent.participant = obj.hasParticipant()? transform(obj.getParticipant(), EncounterParticipantEntity::fromEncounterParticipantComponent) : null;
        ent.appointment = obj.hasAppointment()? BaseReference.fromReferenceList(obj.getAppointment()) : null;
        ent.period = obj.hasPeriod()? BasePeriod.fromPeriod(obj.getPeriod()) : null;
        ent.length = obj.hasLength()? BaseDuration.fromDuration(obj.getLength()) : null;
        ent.reasonCode = obj.hasReasonCode()? BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode()) : null;
        ent.diagnosis = obj.hasDiagnosis()? transform(obj.getDiagnosis(), DiagnosisEntity::fromDiagnosisComponent) : null;
        ent.hospitalization = obj.hasHospitalization()? HospitalizationEntity.fromEncounterHospitalizationComponent(obj.getHospitalization()) : null;
        ent.location = obj.hasLocation()? transform(obj.getLocation(), EncounterLocationEntity::fromEncounterLocationComponent) : null;
        ent.serviceProvider = obj.hasServiceProvider()? BaseReference.fromReference(obj.getServiceProvider()) : null;
        ent.partOf = obj.hasPartOf()? BaseReference.fromReference(obj.getPartOf()) : null;
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
        obj.setSubject(BaseReference.toReference(ent.subject));
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
    
    public String getIdentifier() {
        String value = "";
        if(identifier != null && identifier.size() > 0) {
            value = identifier.get(0).value;
        }
        return value != null? value : "";
    }
    
    public Date getStart() {
        return period != null? period.start : null;
    }
    
    public Date getEnd() {
        return period != null? period.end : null;
    }
    
    public BaseCodeableConcept getTypeBySystem(@Nonnull String system) {
        if(type != null) {
            return type.stream()
                        .filter(x -> FPUtil.anyMatch(x.coding, y -> system.equals(y.system)))
                        .findFirst()
                        .orElse(null);
        }
        return null;
    }
}
