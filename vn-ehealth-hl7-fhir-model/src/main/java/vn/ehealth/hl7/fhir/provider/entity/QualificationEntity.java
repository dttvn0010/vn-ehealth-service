package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class QualificationEntity {

    public List<BaseIdentifier> identifier;
    public BaseCodeableConcept code;
    public BasePeriod period;
    public BaseReference issuer;
    
}
