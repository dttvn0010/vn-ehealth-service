package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Patient.ContactComponent;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BaseContactPerson {
    public List<CodeableConcept> relationship;
    public BaseHumanName name;
    public List<BaseContactPoint> telecom;
    public List<BaseAddress> address;
    public String gender;
    public BaseReference organization;
    public BasePeriod period;
    
    public static BaseContactPerson fromContactComponent(ContactComponent object) {
        if(object == null) return null;
        
        var entity = new BaseContactPerson();
              
        entity.relationship = object.getRelationship();
        entity.name = BaseHumanName.fromHumanName(object.getName());
        entity.telecom = transform(object.getTelecom(), BaseContactPoint::fromContactPoint);
        entity.address = List.of(BaseAddress.fromAddress(object.getAddress()));
        entity.organization = BaseReference.fromReference(object.getOrganization());
        entity.period = BasePeriod.fromPeriod(object.getPeriod());
        
        return entity;
    }
    
    public static List<BaseContactPerson> fromContactComponentList(List<ContactComponent> lst) {
        return transform(lst, x -> fromContactComponent(x));        
    }
    
    public static ContactComponent toContactComponent(BaseContactPerson entity) {
        if(entity == null) return null;
        
        var object = new ContactComponent();
        object.setRelationship(entity.relationship);
        object.setName(BaseHumanName.toHumanName(entity.name));
        object.setTelecom(transform(entity.telecom, BaseContactPoint::toContactPoint));

        if (entity.address != null && entity.address.size() > 0) {
            object.setAddress(BaseAddress.toAddress(entity.address.get(0)));
        }
        
        object.setOrganization(BaseReference.toReference(entity.organization));
        object.setPeriod(BasePeriod.toPeriod(entity.period));
        
        return object;
    }
    
    public static List<ContactComponent> toContactComponentList(List<BaseContactPerson> entityList) {
        return transform(entityList, x -> toContactComponent(x));
    }
}
