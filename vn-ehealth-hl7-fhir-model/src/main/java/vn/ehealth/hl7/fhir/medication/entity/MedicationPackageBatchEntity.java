package vn.ehealth.hl7.fhir.medication.entity;

import java.util.Date;



public class MedicationPackageBatchEntity {
    /**
     * The assigned lot number of a batch of the specified product.
     */
    public String lotNumber;

    /**
     * When this specific batch of product will expire.
     */
    public Date expirationDate;
}
