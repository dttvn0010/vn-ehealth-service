package vn.ehealth.hl7.fhir.diagnostic.entity;

import org.hl7.fhir.r4.model.CodeableConcept;

import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.Specimen.SpecimenCollectionComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class SpecimenCollectionEntity {
    public BaseReference collector;
    public Type collected;
    public BaseQuantity quantity;
    public CodeableConcept method;
    public CodeableConcept bodySite;
    
    public static SpecimenCollectionEntity fromSpecimenCollectionComponent(SpecimenCollectionComponent obj) {
        if(obj == null) return null;
        
        var ent = new SpecimenCollectionEntity();
        ent.collector = BaseReference.fromReference(obj.getCollector());
        ent.collected = obj.getCollected();
        ent.quantity = BaseQuantity.fromQuantity(obj.getQuantity());
        ent.method = obj.getMethod();
        ent.bodySite = obj.getBodySite();
        
        return ent;
    }
    
    public static SpecimenCollectionComponent toSpecimenCollectionComponent(SpecimenCollectionEntity ent) {
        if(ent == null) return null;
        
        var obj = new SpecimenCollectionComponent();
        obj.setCollector(BaseReference.toReference(ent.collector));
        obj.setCollected(ent.collected);
        obj.setQuantity(BaseQuantity.toQuantity(ent.quantity));
        obj.setMethod(ent.method);
        obj.setBodySite(ent.bodySite);
        
        return obj;
    }
}
