package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;

public class BaseContactPerson {
    public List<BaseCodeableConcept> relationship;
    public BaseHumanName name;
    public List<BaseContactPoint> telecom;
    public List<BaseAddress> address;
    public String gender;
    public BaseReference organization;
    public BasePeriod period;
    public List<Extension> extension;
}
