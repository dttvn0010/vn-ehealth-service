package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceEligibilityComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseAvailableTime;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseNotAvailableTime;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "healthcareService")
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
    
    public static HealthcareServiceEntity fromHealthcareService(HealthcareService obj) {
        if(obj == null) return null;
        var ent = new HealthcareServiceEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.providedBy = BaseReference.fromReference(obj.getProvidedBy());
        ent.category = BaseCodeableConcept.fromCodeableConcept(obj.getCategory());
        ent.type = BaseCodeableConcept.fromCodeableConcept(obj.getType());
        ent.specialty = BaseCodeableConcept.fromCodeableConcept(obj.getSpecialty());
        ent.location = BaseReference.fromReferenceList(obj.getLocation());
        ent.name = obj.getName();
        ent.comment = obj.getComment();
        ent.extraDetails = obj.getExtraDetails();
        ent.telecom = BaseContactPoint.fromContactPointList(obj.getTelecom());
        ent.coverageArea = BaseReference.fromReferenceList(obj.getCoverageArea());
        ent.serviceProvisionCode = BaseCodeableConcept.fromCodeableConcept(obj.getServiceProvisionCode());
        ent.eligibility = obj.getEligibility();
        ent.program = BaseCodeableConcept.fromCodeableConcept(obj.getProgram());
        ent.characteristic = BaseCodeableConcept.fromCodeableConcept(obj.getCharacteristic());
        ent.referralMethod = BaseCodeableConcept.fromCodeableConcept(obj.getReferralMethod());
        ent.appointmentRequired = obj.getAppointmentRequired();
        ent.availableTime = BaseAvailableTime.fromHealthcareServiceAvailableTimeComponent(obj.getAvailableTime());
        ent.notAvailable = BaseNotAvailableTime.fromHealthcareServiceNotAvailableComponent(obj.getNotAvailable());
        ent.availabilityExceptions = obj.getAvailabilityExceptions();
        return ent;
    }
    
    public static HealthcareService toHealthcareService(HealthcareServiceEntity ent) {
        if(ent == null) return null;
        var obj = new HealthcareService();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setProvidedBy(BaseReference.toReference(ent.providedBy));
        obj.setCategory(BaseCodeableConcept.toCodeableConcept(ent.category));
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setSpecialty(BaseCodeableConcept.toCodeableConcept(ent.specialty));
        obj.setLocation(BaseReference.toReferenceList(ent.location));
        obj.setName(ent.name);
        obj.setComment(ent.comment);
        obj.setExtraDetails(ent.extraDetails);
        obj.setTelecom(BaseContactPoint.toContactPointList(ent.telecom));
        obj.setCoverageArea(BaseReference.toReferenceList(ent.coverageArea));
        obj.setServiceProvisionCode(BaseCodeableConcept.toCodeableConcept(ent.serviceProvisionCode));
        obj.setEligibility(ent.eligibility);
        obj.setProgram(BaseCodeableConcept.toCodeableConcept(ent.program));
        obj.setCharacteristic(BaseCodeableConcept.toCodeableConcept(ent.characteristic));
        obj.setReferralMethod(BaseCodeableConcept.toCodeableConcept(ent.referralMethod));
        obj.setAppointmentRequired(ent.appointmentRequired);
        obj.setAvailableTime(BaseAvailableTime.toHealthcareServiceAvailableTimeComponent(ent.availableTime));
        obj.setNotAvailable(BaseNotAvailableTime.toHealthcareServiceNotAvailableComponent(ent.notAvailable));
        obj.setAvailabilityExceptions(ent.availabilityExceptions);
        return obj;
    }
}
