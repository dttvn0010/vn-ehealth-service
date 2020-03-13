package vn.ehealth.hl7.fhir.medication.entity;

import org.hl7.fhir.r4.model.Medication.MedicationIngredientComponent;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hl7.fhir.r4.model.Type;

import vn.ehealth.hl7.fhir.core.entity.BaseRatio;

public class MedicationIngredientEntity{
    @JsonIgnore public Type item;
    public boolean isActive;
    public BaseRatio strength;
    
    public static MedicationIngredientEntity fromMedicationIngredientComponent(MedicationIngredientComponent obj) {
        if(obj == null) return null;
        var ent = new MedicationIngredientEntity();
        ent.item = obj.getItem();
        ent.isActive = obj.getIsActive();
        ent.strength = BaseRatio.fromRation(obj.getStrength());
        return ent;
    }
    
    public static MedicationIngredientComponent toMedicationIngredientComponent(MedicationIngredientEntity ent) {
        if(ent == null) return null;
        
        var obj = new MedicationIngredientComponent();
        obj.setItem(ent.item);
        obj.setIsActive(ent.isActive);
        obj.setStrength(BaseRatio.toRatio(ent.strength));
        return obj;
    }
}
