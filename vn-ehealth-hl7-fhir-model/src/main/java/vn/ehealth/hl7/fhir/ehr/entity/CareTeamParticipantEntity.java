package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class CareTeamParticipantEntity {
    public List<BaseCodeableConcept> role;
    public BaseReference member;
    public BaseReference onBehalfOf;
    public BasePeriod period;
}
