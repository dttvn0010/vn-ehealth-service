package vn.ehealth.hl7.fhir.term.entity;

import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseResource;
/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
public class ConceptPropertyEntity extends BaseResource {
    public String code;
    @JsonIgnore public Type value;
    
    public static ConceptPropertyEntity fromConceptPropertyComponent(ConceptPropertyComponent obj) {
        if(obj == null) return null;
        var ent = new ConceptPropertyEntity();
        ent.code = obj.getCode();
        ent.value = obj.getValue();
        return ent;
    }
    
    public static ConceptPropertyComponent toConceptPropertyComponent(ConceptPropertyEntity ent) {
        if(ent == null) return null;
        var obj = new ConceptPropertyComponent();
        obj.setCode(ent.code);
        obj.setValue(ent.value);
        return obj;
    }
}
