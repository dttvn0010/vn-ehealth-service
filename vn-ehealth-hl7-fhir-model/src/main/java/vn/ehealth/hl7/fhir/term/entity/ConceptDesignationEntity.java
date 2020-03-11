package vn.ehealth.hl7.fhir.term.entity;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseResource;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public class ConceptDesignationEntity extends BaseResource {

    public String language;
    public Coding use;
    public String value;
    
    public static ConceptDesignationEntity fromConceptDefinitionDesignationComponent(ConceptDefinitionDesignationComponent obj) {
        if(obj == null) return null;
        var ent = new ConceptDesignationEntity();
        ent.language = obj.getLanguage();
        ent.use = obj.getUse();
        ent.value = obj.getValue();
        return ent;
    }
    
    public static ConceptDefinitionDesignationComponent toConceptDefinitionDesignationComponent(ConceptDesignationEntity ent) {
        if(ent == null) return null;
        var obj = new ConceptDefinitionDesignationComponent();
        obj.setLanguage(ent.language);
        obj.setUse(ent.use);
        obj.setValue(ent.value);
        return obj;
    }
}
