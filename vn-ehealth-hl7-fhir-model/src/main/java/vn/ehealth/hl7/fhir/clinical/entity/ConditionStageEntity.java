package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.List;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class ConditionStageEntity {
    
    public BaseCodeableConcept summary;
    public List<BaseReference> assessment;
}
