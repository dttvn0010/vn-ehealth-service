package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "episodeOfCare")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1, 'patient.reference':1, 'managingOrganization.reference':1, 'careManager.reference':1, 'team.reference':1}", name = "index_by_default")
public class EpisodeOfCareEntity extends BaseResource{
    
    public static class EpisodeOfCareStatusHistory {

        public String status;
        public BasePeriod period;        
    }
    
    public static class Diagnosis {
        public BaseReference condition;
        public BaseCodeableConcept role;
        public int rank;
    }
        
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public List<EpisodeOfCareStatusHistory> statusHistory;
    public List<BaseCodeableConcept> type;
    public List<Diagnosis> diagnosis;
    public BaseReference patient;
    public BaseReference managingOrganization;
    public BasePeriod period;
    public List<BaseReference> referralRequest;
    public BaseReference careManager;
    public List<BaseReference> team;
    public List<BaseReference> account;
    
}
