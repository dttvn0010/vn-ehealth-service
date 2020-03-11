package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;
/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */

@Document(collection = "concept")
public class ConceptEntity extends BaseResource {
    @Id
    public ObjectId id;
    public String code;
    public String display;
    public String definition;
    public List<ConceptDesignationEntity> designation;
    public List<ConceptPropertyEntity> property;
    public String parentConceptId;
    public String codeSystemId;
    public List<ConceptEntity> concept;
    public Integer level;
    public PropertyType type;
    
    public static ConceptEntity fromConceptDefinitionComponent(ConceptDefinitionComponent obj) {
        if(obj == null) return null;
        var ent = new ConceptEntity();
        ent.code = obj.getCode();
        ent.display = obj.getDisplay();
        ent.definition =  obj.getDefinition();
        ent.designation = transform(obj.getDesignation(), ConceptDesignationEntity::fromConceptDefinitionDesignationComponent);
        ent.property = transform(obj.getProperty(), ConceptPropertyEntity::fromConceptPropertyComponent);
        ent.concept = transform(obj.getConcept(), ConceptEntity::fromConceptDefinitionComponent);
        return ent;
    }
    
    public static ConceptDefinitionComponent toConceptDefinitionComponent(ConceptEntity ent) {
        if(ent == null) return null;
        var obj = new ConceptDefinitionComponent();
        
        obj.setCode(ent.code);
        obj.setDisplay(ent.display);
        obj.setDefinition(ent.definition);
        obj.setDesignation(transform(ent.designation, ConceptDesignationEntity::toConceptDefinitionDesignationComponent));
        obj.setProperty(transform(ent.property, ConceptPropertyEntity::toConceptPropertyComponent));
        obj.setConcept(transform(ent.concept, ConceptEntity::toConceptDefinitionComponent));
        return obj;
    }
}
