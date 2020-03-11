package vn.ehealth.hl7.fhir.medication.entity;

import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;



public class ImmunizationExplanationEntity {

    public List<CodeableConcept> reason;
    public List<CodeableConcept> reasonNotGiven;
}
