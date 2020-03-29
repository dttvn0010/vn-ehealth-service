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
    
    public static class EncounterLocation {
        public BaseReference location;
        public String status;
        public BaseCodeableConcept physicalType;
        public BasePeriod period;
        
    }
    
    public static class StatusHistory{
        public String status;
        public BasePeriod period;
    }
    
    public static class ClassHistory{
        public BaseCoding class_;
        public BasePeriod period;
        
    }
    
    public static class EncounterParticipant {
        public List<BaseCodeableConcept> type;
        public BasePeriod period;
        public BaseReference individual;
        
    }
    
    public static class Diagnosis {
        public BaseReference condition;
        public BaseCodeableConcept use;
        public Integer rank;
    }
    
    public class Hospitalization{
        public BaseIdentifier preAdmissionIdentifier;
        public BaseReference origin;
        public BaseCodeableConcept admitSource;
        public BaseCodeableConcept reAdmission;
        public List<BaseCodeableConcept> dietPreference;
        public List<BaseCodeableConcept> specialCourtesy;
        public List<BaseCodeableConcept> specialArrangement;
        public BaseReference destination;
        public BaseCodeableConcept dischargeDisposition;
    }
    
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public List<StatusHistory> statusHistory;
    public BaseCoding class_;
    public List<ClassHistory> classHistory;
    public List<BaseCodeableConcept> type;
    public BaseCodeableConcept serviceType;
    public BaseCodeableConcept priority;
    public BaseReference subject;
    public List<BaseReference> episodeOfCare;
    public BaseReference basedOn;
    public List<BaseReference> reasonReference;
    public List<EncounterParticipant> participant;
    public List<BaseReference> appointment;
    public BasePeriod period;
    public BaseDuration length;
    public List<BaseCodeableConcept> reasonCode;
    public List<Diagnosis> diagnosis;
    public List<BaseReference> account;
    public Hospitalization hospitalization;
    public List<EncounterLocation> location;
    public BaseReference serviceProvider;
    public BaseReference partOf;
}
