package vn.ehealth.hl7.fhir.medication.entity;

import vn.ehealth.hl7.fhir.core.entity.BaseDuration;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class MedicationRequestDispenseRequestEntity{
    public BasePeriod validityPeriod;
    public int numberOfRepeatsAllowed;
    public BaseQuantity quantity;
    public BaseDuration expectedSupplyDuration;
    public BaseReference performer;
}
