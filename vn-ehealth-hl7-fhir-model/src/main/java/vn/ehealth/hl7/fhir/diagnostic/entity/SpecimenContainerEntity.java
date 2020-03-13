package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.List;
import org.hl7.fhir.r4.model.Specimen.SpecimenContainerComponent;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hl7.fhir.r4.model.Type;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;

public class SpecimenContainerEntity {
    public List<BaseIdentifier> identifier;
    public String description;
    public BaseCodeableConcept type;
    public BaseQuantity capacity;
    public BaseQuantity specimenQuantity;
    @JsonIgnore public Type additive;
    
    public static SpecimenContainerEntity fromSpecimenContainerComponent(SpecimenContainerComponent obj) {
        if(obj == null) return null;
        
        var ent = new SpecimenContainerEntity();
        
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.description = obj.getDescription();
        ent.type = BaseCodeableConcept.fromCodeableConcept(obj.getType());
        ent.capacity = BaseQuantity.fromQuantity(obj.getCapacity());
        ent.specimenQuantity = BaseQuantity.fromQuantity(obj.getSpecimenQuantity());
        ent.additive = obj.getAdditive();
        
        return ent;
    }
    
    public static SpecimenContainerComponent toSpecimenContainerComponent(SpecimenContainerEntity ent) {
        if(ent == null) return null;
        
        var obj = new SpecimenContainerComponent();
        
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setDescription(ent.description);
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setCapacity(BaseQuantity.toQuantity(ent.capacity));
        obj.setSpecimenQuantity(BaseQuantity.toQuantity(ent.specimenQuantity));
        obj.setAdditive(ent.additive);
        
        return obj;
    }
    
}
