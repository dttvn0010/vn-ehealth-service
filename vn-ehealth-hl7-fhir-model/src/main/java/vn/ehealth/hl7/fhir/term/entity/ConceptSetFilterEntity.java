package vn.ehealth.hl7.fhir.term.entity;

import java.util.Optional;

import org.hl7.fhir.r4.model.ValueSet.ConceptSetFilterComponent;
import org.hl7.fhir.r4.model.ValueSet.FilterOperator;

public class ConceptSetFilterEntity {

    public String property;
    public String value;
    public String op;
    
    public static ConceptSetFilterEntity fromConceptSetFilterComponent(ConceptSetFilterComponent obj) {
        if(obj == null) return null;
        var ent = new ConceptSetFilterEntity();
        ent.property = obj.getProperty();
        ent.value = obj.getValue();
        ent.op = Optional.ofNullable(obj.getOp()).map(x -> x.toCode()).orElse(null);
        return ent;
    }
    
    public static ConceptSetFilterComponent toConceptSetFilterComponent(ConceptSetFilterEntity ent) {
        if(ent == null) return null;
        var obj = new ConceptSetFilterComponent();
        obj.setProperty(ent.property);
        obj.setValue(ent.value);
        obj.setOp(FilterOperator.fromCode(ent.op));
        return obj;
    }
}
