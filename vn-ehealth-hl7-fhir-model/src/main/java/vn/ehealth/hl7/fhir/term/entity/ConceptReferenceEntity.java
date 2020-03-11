package vn.ehealth.hl7.fhir.term.entity;

import org.hl7.fhir.r4.model.ValueSet.ConceptReferenceComponent;

public class ConceptReferenceEntity {

    public String code;
    public String display;
    
    public ConceptReferenceEntity() {
        
    }
    
    public ConceptReferenceEntity(String code, String display) {
        this.code = code;
        this.display = display;
    }
    
    public static ConceptReferenceEntity fromConceptReferenceComponent(ConceptReferenceComponent obj) {
        if(obj == null) return null;
        var ent = new ConceptReferenceEntity();
        ent.code = obj.getCode();
        ent.display = obj.getDisplay();
        return ent;
    }
    
    public static ConceptReferenceComponent toConceptReferenceComponent(ConceptReferenceEntity ent) {
        if(ent == null) return null;
        var obj = new ConceptReferenceComponent();
        obj.setCode(ent.code);
        obj.setDisplay(ent.display);
        return obj;
    }
}
