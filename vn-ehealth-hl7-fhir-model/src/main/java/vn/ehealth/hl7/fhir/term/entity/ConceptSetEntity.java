package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.ValueSet.ConceptSetComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;
/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
@Document(collection = "conceptSetEntity")
public class ConceptSetEntity extends BaseResource{
    @Id
    public ObjectId id;
    public  String system;
    public List<ConceptReferenceEntity> concept;
    public List<ConceptSetFilterEntity> filter;
    public String valueSetComposeId;
    public String type;
    
    public static ConceptSetEntity fromConceptSetComponent(ConceptSetComponent obj) {
        if(obj == null) return null;
        var ent = new ConceptSetEntity();
        ent.system = obj.getSystem();
        ent.concept = transform(obj.getConcept(), ConceptReferenceEntity::fromConceptReferenceComponent);
        ent.filter = transform(obj.getFilter(), ConceptSetFilterEntity::fromConceptSetFilterComponent);
        return ent;
    }
    
    public static ConceptSetComponent toConceptSetComponent(ConceptSetEntity ent) {
        if(ent == null) return null;
        var obj = new ConceptSetComponent();
        obj.setSystem(ent.system);
        obj.setConcept(transform(ent.concept, ConceptReferenceEntity::toConceptReferenceComponent));
        obj.setFilter(transform(ent.filter, ConceptSetFilterEntity::toConceptSetFilterComponent));
        return obj;
    }
}
