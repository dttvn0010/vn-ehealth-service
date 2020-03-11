package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

public class BaseContact{
    public BaseHumanName name;
    public List<BaseContactPoint> telecom;
    public List<BaseAddress> address;
    public String purpose;
    
}
