package vn.ehealth.hl7.fhir.schedule.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseBackboneElement;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "appointment")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class AppointmentEntity extends BaseResource {

    public static class AppointmentParticipant extends BaseBackboneElement {
        public List<BaseCodeableConcept> type;
        public BaseReference actor;
        public String required;
        public String status;
        BasePeriod period;
        public String appointmentEntityID;
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public BaseCodeableConcept cancelationReason;
    public List<BaseCodeableConcept> serviceCategory;
    public List<BaseCodeableConcept> serviceType;
    public List<BaseCodeableConcept> specialty;
    public BaseCodeableConcept appointmentType;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public int priority;
    public String description;
    public List<BaseReference> supportingInformation;
    public Date start;
    public Date end;
    public int minutesDuration;
    public List<BaseReference> slot;
    public Date created;
    public String comment;
    public String patientInstruction;
    public List<BaseReference> basedOn;
    public List<AppointmentParticipant> participant;
    public List<BasePeriod> requestedPeriod;
}
