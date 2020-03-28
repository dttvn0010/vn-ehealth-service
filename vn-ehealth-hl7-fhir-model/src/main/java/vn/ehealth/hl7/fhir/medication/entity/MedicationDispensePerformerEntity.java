package vn.ehealth.hl7.fhir.medication.entity;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;


public class MedicationDispensePerformerEntity {
    public BaseReference actor;
    public BaseCodeableConcept function;
}
