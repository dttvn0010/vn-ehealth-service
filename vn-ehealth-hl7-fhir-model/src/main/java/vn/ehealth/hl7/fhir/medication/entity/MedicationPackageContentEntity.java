package vn.ehealth.hl7.fhir.medication.entity;


import org.hl7.fhir.r4.model.Type;

import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;

public class MedicationPackageContentEntity {
    public Type item;
    public BaseQuantity amount;
}
