package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;;

public class BaseCodeableConcept extends BaseType {
    public List<BaseCoding> coding;
    public String text;
}
