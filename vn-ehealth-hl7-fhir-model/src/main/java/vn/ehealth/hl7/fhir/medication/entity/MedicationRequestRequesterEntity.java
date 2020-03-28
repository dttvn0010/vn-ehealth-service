package vn.ehealth.hl7.fhir.medication.entity;

import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class MedicationRequestRequesterEntity{
    public BaseReference agent;
    public BaseReference onBehalfOf;
}
