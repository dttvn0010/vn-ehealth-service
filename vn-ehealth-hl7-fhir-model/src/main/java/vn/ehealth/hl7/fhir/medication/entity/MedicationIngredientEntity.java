package vn.ehealth.hl7.fhir.medication.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hl7.fhir.r4.model.Type;

import vn.ehealth.hl7.fhir.core.entity.BaseRatio;

public class MedicationIngredientEntity{
    @JsonIgnore public Type item;
    public boolean isActive;
    public BaseRatio strength;
}
