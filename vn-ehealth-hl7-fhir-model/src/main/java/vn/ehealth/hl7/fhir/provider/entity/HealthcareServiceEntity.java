package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceEligibilityComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAvailableTime;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseNotAvailableTime;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "healthcareService")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class HealthcareServiceEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public BaseReference providedBy;
    public List<BaseCodeableConcept> category;
    public List<BaseCodeableConcept> type;
    public List<BaseCodeableConcept> specialty;
    public List<BaseReference> location;
    public String name;
    public String comment;
    public String extraDetails;
    /** public photo **/
    public List<BaseContactPoint> telecom;
    public List<BaseReference> coverageArea;
    public List<BaseCodeableConcept> serviceProvisionCode;
    public List<HealthcareServiceEligibilityComponent> eligibility;
    //public String eligibilityNote;
    public List<BaseCodeableConcept> program;
    public List<BaseCodeableConcept> characteristic;
    public List<BaseCodeableConcept> referralMethod;
    public boolean appointmentRequired;
    public List<BaseAvailableTime> availableTime;
    public List<BaseNotAvailableTime> notAvailable;
    public String availabilityExceptions;
    
}
