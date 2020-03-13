package vn.ehealth.hl7.fhir.medication.entity;


import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;

public class MedicationPackageContentEntity {
    @JsonIgnore public Type item;
    public BaseQuantity amount;
}
