package vn.ehealth.hl7.fhir.ehr.entity;

import org.hl7.fhir.r4.model.Coding;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;

public class EncounterClassHistoryEntity{
    public Coding class_;
    public BasePeriod period;
    
}
