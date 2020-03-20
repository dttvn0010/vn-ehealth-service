package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Observation.ObservationReferenceRangeComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseRange;

public class ObservationReferenceRangeEntity {

    public BaseQuantity low;
    public BaseQuantity high;
    public BaseCodeableConcept type;
    public List<BaseCodeableConcept> appliesTo;
    public BaseRange age;
    public String text;
    
    public static ObservationReferenceRangeEntity fromObservationReferenceRangeComponent(ObservationReferenceRangeComponent obj) {
        if(obj == null) return null;
        
        var ent = new ObservationReferenceRangeEntity();
        ent.low = obj.hasLow()? BaseQuantity.fromQuantity(obj.getLow()) : null;
        ent.high = obj.hasHigh()? BaseQuantity.fromQuantity(obj.getHigh()) : null;
        ent.type = obj.hasType()? BaseCodeableConcept.fromCodeableConcept(obj.getType()) : null;
        ent.appliesTo = obj.hasAppliesTo()? BaseCodeableConcept.fromCodeableConcept(obj.getAppliesTo()) : null;
        ent.age = obj.hasAge()? BaseRange.fromRange(obj.getAge()) : null;
        ent.text = obj.hasText()? obj.getText() : null;
        
        return ent;
    }
    
    public static ObservationReferenceRangeComponent toObservationReferenceRangeComponent(ObservationReferenceRangeEntity ent) {
        if(ent == null) return null;
        var obj = new ObservationReferenceRangeComponent();
        obj.setLow(BaseQuantity.toQuantity(ent.low));
        obj.setHigh(BaseQuantity.toQuantity(ent.high));
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setAppliesTo(BaseCodeableConcept.toCodeableConcept(ent.appliesTo));
        obj.setAge(BaseRange.toRange(ent.age));
        obj.setText(ent.text);
        return obj;        
    }
}
