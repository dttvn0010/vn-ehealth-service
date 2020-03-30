package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;

import vn.ehealth.hl7.fhir.core.view.DTOView;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@JsonInclude(Include.NON_NULL)
public class BaseCodeableConcept  extends BaseSimpleType  {

    public List<BaseCoding> coding;
    public String text;
    
    public static Map<String, ?> toDto(BaseCodeableConcept ent) {
        if(ent == null) return null;
        String display = ent.text, code = "";
        if(ent.coding != null && ent.coding.size() > 0) {
            code = ent.coding.get(0).code;
            display = ent.coding.get(0).display;
        }
        return mapOf("display", display, "code", code);
    }
    
    @JsonView(DTOView.class)
    public Map<String, ?> getDto() {
        return toDto(this);
    }
}
