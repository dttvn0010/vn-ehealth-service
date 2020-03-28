package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.Extension;

public class BasePeriod {
    public Date start;
    public Date end;
    public List<Extension> extension;
    
}
