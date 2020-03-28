package vn.ehealth.hl7.fhir.medication.entity;

import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;



public class MedicationRequestSubstitutionEntity{
    @JsonIgnore public Type allowed;
    public BaseCodeableConcept reason;
}
