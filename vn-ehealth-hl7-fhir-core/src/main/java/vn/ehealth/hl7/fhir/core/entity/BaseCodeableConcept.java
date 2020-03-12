package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;;

public class BaseCodeableConcept {

    public List<BaseCoding> coding;
    public String text;
    
    public static BaseCodeableConcept fromCodeableConcept(CodeableConcept obj) {
                
        if(obj == null) return null;
        
        var ent = new BaseCodeableConcept();
        ent.coding = obj.hasCoding()? transform(obj.getCoding(), BaseCoding::fromCoding) : null;
        ent.text = obj.hasText()? obj.getText() : null;
        return ent;
    }
    
    public static List<BaseCodeableConcept> fromCodeableConcept(List<CodeableConcept> lst) {
        return transform(lst, x -> fromCodeableConcept(x));
    }
    
    public static CodeableConcept toCodeableConcept(BaseCodeableConcept ent) {
        if(ent == null) return null;
        var obj = new CodeableConcept();
        obj.setCoding(transform(ent.coding, BaseCoding::toCoding));
        obj.setText(ent.text);
        return obj;
    }
    
    public static List<CodeableConcept> toCodeableConcept(List<BaseCodeableConcept> entLst) {
        return transform(entLst, x -> toCodeableConcept(x));
    }
}
