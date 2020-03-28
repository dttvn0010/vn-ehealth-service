package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;
import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseHumanName;

public class OrganizationContactEntity {

    public BaseCodeableConcept purpose;
    public BaseHumanName name;
    public List<BaseContactPoint> telecom;
    public BaseAddress address;    
}
