package vn.ehealth.hl7.fhir.medication.entity;

import java.util.Date;

import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class ImmunizationReactionEntity {

    public Date date;
    public BaseReference detail;
    public boolean reported;
    
}
