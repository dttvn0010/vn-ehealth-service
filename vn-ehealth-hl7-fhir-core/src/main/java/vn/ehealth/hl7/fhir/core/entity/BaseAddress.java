package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public class BaseAddress {
    public String addressLine1;
    public String addressLine2;
    public String addressLine3;
    public String ward;
    public String district;
    public String city;
    public String state;
    public String country;
    public String addressUse;
    public String addressType;
    public String postalCode;
    public String text;
    
    public List<Extension> extension;
    public BasePeriod period;
}
