package vn.ehealth.hl7.fhir.term.entity;



import java.util.Optional;
import org.hl7.fhir.r4.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyType;

import vn.ehealth.hl7.fhir.core.entity.BaseResource;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public class CodeSystemPropertyEntity extends BaseResource {

    public String code;
    public String uri;
    public String description;
    public String type;
    
    public static CodeSystemPropertyEntity fromPropertyComponent(PropertyComponent obj) {
        if(obj == null) return null;
        var ent = new CodeSystemPropertyEntity();
        ent.code = obj.getCode();
        ent.uri = obj.getUri();
        ent.description = obj.getDescription();
        ent.type = Optional.ofNullable(obj.getType()).map(x -> x.toCode()).orElse(null);
        return ent;
    }
    
    public static PropertyComponent toPropertyComponent(CodeSystemPropertyEntity ent) {
        if(ent == null) return null;
        var obj = new PropertyComponent();
        obj.setCode(ent.code);
        obj.setUri(ent.uri);
        obj.setDescription(ent.description);
        obj.setType(PropertyType.fromCode(ent.type));
        return obj;
    }    
}
