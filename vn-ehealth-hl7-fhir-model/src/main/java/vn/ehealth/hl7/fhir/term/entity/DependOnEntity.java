package vn.ehealth.hl7.fhir.term.entity;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.ConceptMap.OtherElementComponent;
import org.springframework.data.annotation.Id;



/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
public class DependOnEntity {
    @Id
    public ObjectId id;
    public String targetElementEntityID;
    public String property;
    public String system;
    //public String code;
    public String display;
    
    public static DependOnEntity fromOtherElementComponent(OtherElementComponent obj) {
        if(obj == null) return null;
        
        var ent = new DependOnEntity();
        ent.property = obj.getProperty();
        ent.system = obj.getSystem();
        ent.display = obj.getDisplay();
        return ent;
    }
    
    public static OtherElementComponent toOtherElementComponent(DependOnEntity ent) {
        if(ent == null) return null;
        var obj = new OtherElementComponent();
        
        obj.setProperty(ent.property);
        obj.setSystem(ent.system);
        obj.setDisplay(ent.display);
        
        return obj;
    }
}
