package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;

public class BaseCoding {

    public String system;
    public String version;
    public String code;
    public String display;
    public Boolean userSelected;
    public List<Extension> extension;
}
