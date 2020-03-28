package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;;

public class BaseCodeableConcept {

    public List<BaseCoding> coding;
    public String text;
    public List<Extension> extension;
}
