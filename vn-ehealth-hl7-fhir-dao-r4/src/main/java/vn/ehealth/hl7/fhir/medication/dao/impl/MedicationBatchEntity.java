package vn.ehealth.hl7.fhir.medication.dao.impl;

import java.util.Date;

import org.hl7.fhir.r4.model.Medication.MedicationBatchComponent;

public class MedicationBatchEntity {

    public Date expirationDate;
    public String lotNumber;
    
    public static MedicationBatchEntity fromMedicationBatchComponent(MedicationBatchComponent obj) {
        if(obj == null) return null;
        var ent = new MedicationBatchEntity();
        ent.expirationDate = obj.getExpirationDate();
        ent.lotNumber = obj.getLotNumber();
        return ent;
    }
    
    public static MedicationBatchComponent toMedicationBatchComponent(MedicationBatchEntity ent) {
        if(ent == null) return null;
        var obj = new MedicationBatchComponent();
        obj.setExpirationDate(ent.expirationDate);
        obj.setLotNumber(ent.lotNumber);
        return obj;
    }
}
