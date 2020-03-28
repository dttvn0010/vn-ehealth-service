package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "practitionerRole")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class PractitionerRoleEntity extends BaseResource {
    
    public static class PractitionerRoleAvailableTime{
        public List<String> daysOfWeek;
        public Boolean allDay;
        public String availableStartTime;
        public String availableEndTime;
    }
    
    public static class PractitionerRoleNotAvailable{
        protected String description;
        protected BasePeriod during;
    }
    
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public BasePeriod period;
    public BaseReference practitioner;
    public BaseReference organization;
    public List<BaseCodeableConcept> code;
    public List<BaseCodeableConcept> specialty;
    public List<BaseReference> location;
    public List<BaseReference> healthcareService;
    public List<BaseContactPoint> telecom;
    public List<PractitionerRoleAvailableTime> availableTime;
    public List<PractitionerRoleNotAvailable> notAvailable;
    public String availabilityExceptions;
    public List<BaseReference> endpoint;
}