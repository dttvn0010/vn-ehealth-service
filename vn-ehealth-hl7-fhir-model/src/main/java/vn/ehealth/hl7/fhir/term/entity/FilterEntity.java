package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;

import org.hl7.fhir.r4.model.CodeSystem.CodeSystemFilterComponent;
import org.hl7.fhir.r4.model.CodeSystem.FilterOperator;

import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public class FilterEntity extends BaseResource {

    public String code;
    public String description;
    public List<String> operator;
    public String value;
    
    public static CodeSystemFilterComponent toCodeSystemFilterComponent(FilterEntity ent) {
        if(ent == null) return null;
        var obj = new CodeSystemFilterComponent();
        obj.setCode(ent.code);
        obj.setDescription(ent.description);
        if(ent.operator != null) {
            for(var op : ent.operator) {
                obj.addOperator(FilterOperator.fromCode(op));
            }
        }
        obj.setValue(ent.value);
        return obj;
    }
    
    
    public static FilterEntity fromCodeSystemFilterComponent(CodeSystemFilterComponent obj) {
        if(obj == null) return null;
        var ent = new FilterEntity();
        ent.code = obj.getCode();
        ent.description = obj.getDescription();
        ent.operator = transform(obj.getOperator(), x -> x.getValue().toCode());
        ent.value = obj.getValue();
        return ent;
    }
}
