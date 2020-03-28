package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;
import org.bson.types.ObjectId;
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

@Document(collection = "encounter")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1, 'serviceProvider.reference':1, 'partOf.reference':1, 'episodeOfCare.reference':1, 'subject.reference':1}", name = "index_by_default")
public class EncounterEntity extends BaseResource{
    public static class DiagnosisEntity {
        public BaseReference condition;
        public BaseCodeableConcept use;
        public Integer rank;
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
}
