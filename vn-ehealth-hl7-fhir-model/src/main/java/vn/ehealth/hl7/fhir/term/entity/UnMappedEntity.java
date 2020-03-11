package vn.ehealth.hl7.fhir.term.entity;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedComponent;
import org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedMode;
import org.springframework.data.annotation.Id;


/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public class UnMappedEntity {
    @Id
    public ObjectId id;
    public String groupElementEntityID;
    public String mode;
    public String code;
    public String display;
    public String url;
    
    public static UnMappedEntity fromConceptMapGroupUnmappedComponent(ConceptMapGroupUnmappedComponent obj) {
        if(obj == null) return null;
        var ent = new UnMappedEntity();
        
        ent.mode = Optional.ofNullable(obj.getMode()).map(x -> x.toCode()).orElse(null);
        ent.code = obj.getCode();
        ent.display = obj.getDisplay();
        ent.url = obj.getUrl();
        return ent;
    }
    
    public static ConceptMapGroupUnmappedComponent toConceptMapGroupUnmappedComponent(UnMappedEntity ent) {
        if(ent == null) return null;
        
        var obj = new ConceptMapGroupUnmappedComponent();
        
        obj.setMode(ConceptMapGroupUnmappedMode.fromCode(ent.mode));
        obj.setCode(ent.code);
        obj.setDisplay(ent.display);
        obj.setUrl(ent.url);
        
        return obj;
    }
}
