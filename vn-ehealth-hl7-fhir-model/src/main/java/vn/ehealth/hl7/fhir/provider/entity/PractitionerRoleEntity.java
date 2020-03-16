package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseAvailableTime;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseNotAvailableTime;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "practitionerRole")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class PractitionerRoleEntity extends BaseResource {
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
    public List<BaseAvailableTime> availableTime;
    public List<BaseNotAvailableTime> notAvailable;
    public String availabilityExceptions;
    
    public static PractitionerRoleEntity fromPractitionerRole(PractitionerRole obj) {
        if(obj == null) return null;
        var ent = new PractitionerRoleEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.period = BasePeriod.fromPeriod(obj.getPeriod());
        ent.practitioner = BaseReference.fromReference(obj.getPractitioner());
        ent.organization = BaseReference.fromReference(obj.getOrganization());
        ent.code = BaseCodeableConcept.fromCodeableConcept(obj.getCode());
        ent.specialty = BaseCodeableConcept.fromCodeableConcept(obj.getSpecialty());
        ent.location = BaseReference.fromReferenceList(obj.getLocation());
        ent.healthcareService = BaseReference.fromReferenceList(obj.getHealthcareService());
        ent.telecom = BaseContactPoint.fromContactPointList(obj.getTelecom());
        //ent.availableTime = BaseAvailableTime.fromHealthcareServiceAvailableTimeComponent(obj.getAvailableTime());
        //ent.notAvailable = BaseNotAvailableTime.fromHealthcareServiceNotAvailableComponent(obj.getNotAvailable());
        ent.availabilityExceptions = obj.getAvailabilityExceptions();
        return ent;
    }
    
    public static PractitionerRole toPractitionerRole(PractitionerRoleEntity ent) {
        if(ent == null)return  null;
        var obj = new PractitionerRole();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        obj.setPractitioner(BaseReference.toReference(ent.practitioner));
        obj.setOrganization(BaseReference.toReference(ent.organization));
        obj.setCode(BaseCodeableConcept.toCodeableConcept(ent.code));
        obj.setSpecialty(BaseCodeableConcept.toCodeableConcept(ent.specialty));
        obj.setLocation(BaseReference.toReferenceList(ent.location));
        obj.setHealthcareService(BaseReference.toReferenceList(ent.healthcareService));
        obj.setTelecom(BaseContactPoint.toContactPointList(ent.telecom));
        obj.setAvailabilityExceptions(ent.availabilityExceptions);
        return obj;        
    }
}