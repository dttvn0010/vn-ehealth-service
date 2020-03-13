package vn.ehealth.hl7.fhir.term.entity;

import org.hl7.fhir.r4.model.ValueSet.ConceptReferenceDesignationComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseCoding;

public class ConceptReferenceDesignationEntity {

    public String language;
    public BaseCoding use;
    public String value;
    
    public static ConceptReferenceDesignationEntity fromConceptReferenceDesignationComponent(ConceptReferenceDesignationComponent obj) {
        if(obj == null) return null;
        var ent = new ConceptReferenceDesignationEntity();
        ent.language = obj.getLanguage();
        ent.use = BaseCoding.fromCoding(obj.getUse());
        ent.value = obj.getValue();
        return ent;
    }
    
    public static ConceptReferenceDesignationComponent toConceptReferenceDesignationComponent(ConceptReferenceDesignationEntity ent) {
        if(ent == null) return null;
        var obj = new ConceptReferenceDesignationComponent();
        obj.setLanguage(ent.language);
        obj.setUse(BaseCoding.toCoding(ent.use));
        obj.setValue(ent.value);
        return obj;
    }
    
}
