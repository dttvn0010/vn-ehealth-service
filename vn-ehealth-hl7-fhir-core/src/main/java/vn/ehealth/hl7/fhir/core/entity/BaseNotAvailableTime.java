package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;

public class BaseNotAvailableTime{
    public String description; 
    public BasePeriod during;
    public List<Extension> extension;
}
