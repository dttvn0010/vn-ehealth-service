package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;
/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
@Document(collection = "element")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class ElementEntity extends BaseResource{
    @Id
    public ObjectId id;
    public String groupElementID;
    public String code;
    public String display;
    public List<TargetElementEntity> target;//TargetElementComponent
    
    public static ElementEntity fromSourceElementComponent(SourceElementComponent obj) {
        if(obj == null) return null;
        var ent = new ElementEntity();
        ent.code = obj.getCode();
        ent.display = obj.getDisplay();
        ent.target = transform(obj.getTarget(), TargetElementEntity::fromTargetElementComponent);
        return ent;
    }
    
    public static SourceElementComponent toSourceElementComponent(ElementEntity ent) {
        if(ent == null) return null;
        
        var obj = new SourceElementComponent();
        obj.setCode(ent.code);
        obj.setDisplay(ent.display);
        obj.setTarget(transform(ent.target, TargetElementEntity::toTargetElementComponent));
        
        return obj;
    }
}
