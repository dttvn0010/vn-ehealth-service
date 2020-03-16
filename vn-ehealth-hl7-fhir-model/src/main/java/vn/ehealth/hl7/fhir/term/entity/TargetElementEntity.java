package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent;
import org.hl7.fhir.r4.model.Enumerations.ConceptMapEquivalence;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "target")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class TargetElementEntity extends BaseResource{
    @Id
    public ObjectId id;
    public String elementEntityID;
    public String code;
    public String display;
    public String equivalence;
    public String comment;
    public List<DependOnEntity> dependsOn;// OtherElementComponent
    
    public static TargetElementEntity fromTargetElementComponent(TargetElementComponent obj) {
        if(obj == null) return null;
        var ent = new TargetElementEntity();
        
        ent.code = obj.getCode();
        ent.display = obj.getDisplay();
        ent.equivalence = Optional.ofNullable(obj.getEquivalence()).map(x -> x.toCode()).orElse(null);
        ent.comment = obj.getComment();
        ent.dependsOn = transform(obj.getDependsOn(), DependOnEntity::fromOtherElementComponent);
        
        return ent;
    }
    
    public static TargetElementComponent toTargetElementComponent(TargetElementEntity ent) {
        if(ent == null) return null;
        var obj = new TargetElementComponent();
        
        obj.setCode(ent.code);
        obj.setDisplay(ent.display);
        obj.setEquivalence(ConceptMapEquivalence.fromCode(ent.equivalence));
        obj.setComment(ent.comment);
        obj.setDependsOn(transform(ent.dependsOn, DependOnEntity::toOtherElementComponent));
        
        return obj;
                
    }
}
