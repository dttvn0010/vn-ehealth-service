package vn.ehealth.hl7.fhir.ehr.entity;

import vn.ehealth.hl7.fhir.core.entity.BasePeriod;


import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class EncounterLocationEntity {
    public BaseReference location;
    public String status;
    public BasePeriod period;
    
}
