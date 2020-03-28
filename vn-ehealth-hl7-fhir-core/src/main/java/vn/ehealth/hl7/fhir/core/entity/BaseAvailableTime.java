package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;

public class BaseAvailableTime {
    public List<String> daysOfWeek;
    public Boolean allDay;
    public String availableStartTime;
    public String availableEndTime;
    public List<Extension> extension;
}
