package vn.ehealth.hl7.fhir.schedule.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "participant")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ParticipantEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseCodeableConcept> type;
    public BaseReference actor;
    public String required;
    public String status;
    //public List<BasePeriod> requestedPeriod;
    BasePeriod period;
    public String appointmentEntityID;
}
