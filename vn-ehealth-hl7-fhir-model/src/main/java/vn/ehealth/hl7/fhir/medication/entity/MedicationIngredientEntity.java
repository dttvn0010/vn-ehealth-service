package vn.ehealth.hl7.fhir.medication.entity;

import org.hl7.fhir.r4.model.Medication.MedicationIngredientComponent;
import org.hl7.fhir.r4.model.Ratio;


import org.hl7.fhir.r4.model.Type;

public class MedicationIngredientEntity{
    public Type item;
    public boolean isActive;
    public Ratio strength;
    
    public static MedicationIngredientEntity fromMedicationIngredientComponent(MedicationIngredientComponent obj) {
        if(obj == null) return null;
        var ent = new MedicationIngredientEntity();
        ent.item = obj.getItem();
        ent.isActive = obj.getIsActive();
        ent.strength = obj.getStrength();
        return ent;
    }
    
    public static MedicationIngredientComponent toMedicationIngredientComponent(MedicationIngredientEntity ent) {
        if(ent == null) return null;
        
        var obj = new MedicationIngredientComponent();
        obj.setItem(ent.item);
        obj.setIsActive(ent.isActive);
        obj.setStrength(ent.strength);
        return obj;
    }
}
