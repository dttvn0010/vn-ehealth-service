package vn.ehealth.hl7.fhir.user.entity;



import org.hl7.fhir.r4.model.Person.IdentityAssuranceLevel;
import org.hl7.fhir.r4.model.Person.PersonLinkComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class PersonLinkEntity{
    public BaseReference target;
    public IdentityAssuranceLevel assurance;    
    
    public static PersonLinkEntity fromPersonLinkComponent(PersonLinkComponent obj) {
        if(obj == null) return null;
        var ent = new PersonLinkEntity();
        ent.target = BaseReference.fromReference(obj.getTarget());
        ent.assurance = obj.getAssurance();
        return ent;
    }
    
    public static PersonLinkComponent toPersonLinkComponent(PersonLinkEntity ent) {
        if(ent == null) return null;
        var obj = new PersonLinkComponent();
        obj.setTarget(BaseReference.toReference(ent.target));
        obj.setAssurance(ent.assurance);
        return obj;
    }
}
