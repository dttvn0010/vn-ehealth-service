package vn.ehealth.hl7.fhir.term.entity;

import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionParameterComponent;

public class ValueSetExpansionParameterEntity {

    
    public String name;
    public Type value;
    
    public static ValueSetExpansionParameterEntity fromValueSetExpansionParameterComponent(ValueSetExpansionParameterComponent obj) {
        if(obj == null) return null;
        var ent = new ValueSetExpansionParameterEntity();
        ent.name = obj.getName();
        ent.value = obj.getValue();
        return ent;
    }
    
    public static ValueSetExpansionParameterComponent toValueSetExpansionParameterComponent(ValueSetExpansionParameterEntity ent) {
        if(ent == null) return null;
        var obj = new ValueSetExpansionParameterComponent();
        obj.setName(ent.name);
        obj.setValue(ent.value);
        return obj;
    }
    
}
