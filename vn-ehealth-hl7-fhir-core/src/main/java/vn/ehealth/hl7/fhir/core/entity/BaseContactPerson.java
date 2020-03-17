package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Patient.ContactComponent;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BaseContactPerson {
    public List<BaseCodeableConcept> relationship;
    public BaseHumanName name;
    public List<BaseContactPoint> telecom;
    public List<BaseAddress> address;
    public String gender;
    public BaseReference organization;
    public BasePeriod period;
    public List<Extension> extension;
    
    public static BaseContactPerson fromContactComponent(ContactComponent obj) {        
        if(obj == null) return null;
        
        var ent = new BaseContactPerson();
              
        ent.relationship = obj.hasRelationship()? transform(obj.getRelationship(), BaseCodeableConcept::fromCodeableConcept) : null;
        ent.name = obj.hasName()? BaseHumanName.fromHumanName(obj.getName()) : null;
        ent.telecom = obj.hasTelecom()? transform(obj.getTelecom(), BaseContactPoint::fromContactPoint) : null;
        ent.address = obj.hasAddress()? List.of(BaseAddress.fromAddress(obj.getAddress())) : null;
        ent.organization = obj.hasOrganization()? BaseReference.fromReference(obj.getOrganization()) : null;
        ent.period = obj.hasPeriod()? BasePeriod.fromPeriod(obj.getPeriod()) : null;
        ent.extension = obj.hasExtension()? obj.getExtension() : null;
        return ent;
    }
    
    public static List<BaseContactPerson> fromContactComponentList(List<ContactComponent> lst) {
        return transform(lst, x -> fromContactComponent(x));        
    }
    
    public static ContactComponent toContactComponent(BaseContactPerson entity) {
        if(entity == null) return null;
        
        var object = new ContactComponent();
        object.setRelationship(transform(entity.relationship, BaseCodeableConcept::toCodeableConcept));
        object.setName(BaseHumanName.toHumanName(entity.name));
        object.setTelecom(transform(entity.telecom, BaseContactPoint::toContactPoint));

        if (entity.address != null && entity.address.size() > 0) {
            object.setAddress(BaseAddress.toAddress(entity.address.get(0)));
        }
        
        object.setOrganization(BaseReference.toReference(entity.organization));
        object.setPeriod(BasePeriod.toPeriod(entity.period));
        object.setExtension(entity.extension);
        return object;
    }
    
    public static List<ContactComponent> toContactComponentList(List<BaseContactPerson> entityList) {
        return transform(entityList, x -> toContactComponent(x));
    }
}
