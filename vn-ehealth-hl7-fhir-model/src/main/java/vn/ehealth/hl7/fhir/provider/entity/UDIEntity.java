package vn.ehealth.hl7.fhir.provider.entity;

import org.hl7.fhir.r4.model.Base64BinaryType;




public class UDIEntity {
    public String deviceID;
    public String deviceIdentifier;
    public String name;
    public String jurisdiction;
    public String carrierHRF;
    public Base64BinaryType carrierAIDC;
    public String issuer;
    public String entryType;
}
