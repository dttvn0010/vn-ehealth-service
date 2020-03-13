package vn.ehealth.hl7.fhir.medication.entity;

import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestComponent;

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
    
    public static MedicationRequestDispenseRequestEntity fromMedicationRequestDispenseRequestComponent(MedicationRequestDispenseRequestComponent obj) {
        if(obj == null) return null;
        var ent = new MedicationRequestDispenseRequestEntity();
        ent.validityPeriod = BasePeriod.fromPeriod(obj.getValidityPeriod());
        ent.numberOfRepeatsAllowed = obj.getNumberOfRepeatsAllowed();
        ent.quantity = BaseQuantity.fromQuantity(obj.getQuantity());
        ent.expectedSupplyDuration = BaseDuration.fromDuration(obj.getExpectedSupplyDuration());
        ent.performer = BaseReference.fromReference(obj.getPerformer());
        return ent;
        
    }
    public static MedicationRequestDispenseRequestComponent toMedicationRequestDispenseRequestComponent(MedicationRequestDispenseRequestEntity ent) {
        if(ent == null) return null;
        var obj = new MedicationRequestDispenseRequestComponent();
        obj.setValidityPeriod(BasePeriod.toPeriod(ent.validityPeriod));
        obj.setNumberOfRepeatsAllowed(ent.numberOfRepeatsAllowed);
        obj.setQuantity(BaseQuantity.toQuantity(ent.quantity));
        obj.setExpectedSupplyDuration(BaseDuration.toDuration(ent.expectedSupplyDuration));
        obj.setPerformer(BaseReference.toReference(ent.performer));
        return obj;
    }
}
