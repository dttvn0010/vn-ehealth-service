package vn.ehealth.hl7.fhir.user.entity;

import org.hl7.fhir.r4.model.Person.IdentityAssuranceLevel;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class PersonLinkEntity{
    public BaseReference target;
    public IdentityAssuranceLevel assurance;    
    
}
