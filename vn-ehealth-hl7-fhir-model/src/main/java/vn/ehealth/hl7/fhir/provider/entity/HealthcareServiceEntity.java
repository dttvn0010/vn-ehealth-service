package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseBackboneElement;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "healthcareService")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class HealthcareServiceEntity extends BaseResource {
    
    public static class HealthcareServiceNotAvailable extends BaseBackboneElement {
        public String description;
        public BasePeriod during;
    }
    
    public static class HealthcareServiceAvailableTime extends BaseBackboneElement {
        public List<String> daysOfWeek;
        public Boolean allDay;
        public String availableStartTime;
        public String availableEndTime;
    }
    
    public static class HealthcareServiceEligibility extends BaseBackboneElement{
        public BaseCodeableConcept code;
        public String comment;
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public Boolean active;
    public List<BaseIdentifier> identifier;
    public BaseReference providedBy;
    public List<BaseCodeableConcept> category;
    public List<BaseCodeableConcept> type;
    public List<BaseCodeableConcept> specialty;
    public List<BaseReference> location;
    public String name;
    public String comment;
    public String extraDetails;
    public BaseAttachment photo;
    public List<BaseContactPoint> telecom;
    public List<BaseReference> coverageArea;
    public List<BaseCodeableConcept> serviceProvisionCode;
    public List<HealthcareServiceEligibility> eligibility;
    public List<BaseCodeableConcept> program;
    public List<BaseCodeableConcept> characteristic;
    public List<BaseCodeableConcept> communication;
    public List<BaseCodeableConcept> referralMethod;
    public boolean appointmentRequired;
    public List<HealthcareServiceAvailableTime> availableTime;
    public List<HealthcareServiceNotAvailable> notAvailable;
    public String availabilityExceptions;
    public List<BaseReference> endpoint;
    
}
