package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;
/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
@Document(collection = "groupElement")
public class GroupElementEntity extends BaseResource{
    @Id
    public ObjectId id;
    public String conceptMapID;
    public String source;
    public String sourceVersion;
    public String target;
    public String targetVersion;
    public List<ElementEntity> element;//SourceElementComponent
    public UnMappedEntity unmapped;//ConceptMapGroupUnmappedComponent    
    
    public static GroupElementEntity fromConceptMapGroupComponent(ConceptMapGroupComponent obj) {
        if(obj == null) return null;
        
        var ent = new GroupElementEntity();
        ent.source = obj.getSource();
        ent.sourceVersion = obj.getSourceVersion();
        ent.target = obj.getTarget();
        ent.targetVersion = obj.getTargetVersion();
        ent.element = transform(obj.getElement(), ElementEntity::fromSourceElementComponent);
        ent.unmapped = UnMappedEntity.fromConceptMapGroupUnmappedComponent(obj.getUnmapped());
        
        return ent;
    }
    
    public static ConceptMapGroupComponent toConceptMapGroupComponent(GroupElementEntity ent) {
        if(ent == null) return null;
        
        var obj = new ConceptMapGroupComponent();
        obj.setSource(ent.source);
        obj.setSourceVersion(ent.sourceVersion);
        obj.setTarget(ent.target);
        obj.setTargetVersion(ent.targetVersion);
        obj.setElement(transform(ent.element, ElementEntity::toSourceElementComponent));
        obj.setUnmapped(UnMappedEntity.toConceptMapGroupUnmappedComponent(ent.unmapped));
        return obj;
        
    }
}
