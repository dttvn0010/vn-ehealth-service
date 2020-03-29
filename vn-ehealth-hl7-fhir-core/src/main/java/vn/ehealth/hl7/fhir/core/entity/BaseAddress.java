package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;


/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public class BaseAddress extends BaseComplexType {
    public String use;
    public String type;
    public String text;
    public List<String> line;
    public String city;
    public String district;    
    public String state;
    public String postalCode;
    public String country;
    public BasePeriod period;
    
}
