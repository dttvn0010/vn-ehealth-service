package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;

import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;

public class ContactDetailEntity {
    public String name;
    public List<BaseContactPoint> telecom;
}
