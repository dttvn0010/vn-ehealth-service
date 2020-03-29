package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

public class BaseHumanName extends BaseType {
    public String use;
    public String text;
    public String family;
    public List<String> given;
    public List<String> prefix;
    public List<String> suffix;
    public BasePeriod period;    
}
