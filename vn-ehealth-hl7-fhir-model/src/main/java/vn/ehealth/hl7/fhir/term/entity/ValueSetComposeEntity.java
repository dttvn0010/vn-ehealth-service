package vn.ehealth.hl7.fhir.term.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.ValueSet.ValueSetComposeComponent;
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
@Document(collection = "valueSetCompose")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class ValueSetComposeEntity extends BaseResource {
    @Id
    public ObjectId id;
    public Date lockedDate;
    public Boolean inactive;
    public String valueSetId;
    public List<ConceptSetEntity> include;
    
    public static ValueSetComposeEntity fromValueSetComposeComponent(ValueSetComposeComponent obj) {
        if(obj == null) return null;
        
        var ent = new ValueSetComposeEntity();
        ent.lockedDate = obj.getLockedDate();
        ent.inactive = obj.getInactive();
        ent.include = transform(obj.getInclude(), ConceptSetEntity::fromConceptSetComponent);
        
        return ent;
    }
    
    public static ValueSetComposeComponent toValueSetComposeComponent(ValueSetComposeEntity ent) {
        if(ent == null) return null;
        
        var obj = new ValueSetComposeComponent();
        obj.setLockedDate(ent.lockedDate);
        obj.setInactive(ent.inactive);
        obj.setInclude(transform(ent.include, ConceptSetEntity::toConceptSetComponent));
        
        return obj;
    }
    
}
