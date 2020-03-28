package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class EncounterParticipantEntity {
    public List<BaseCodeableConcept> type;
    public BasePeriod period;
    public BaseReference individual;
    
}
