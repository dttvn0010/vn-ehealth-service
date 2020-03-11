package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "valueSetContain")
public class ValueSetContainEntity extends BaseResource {
    @Id
    public ObjectId id;
    public String system;
    public boolean _abstract;
    public boolean inactive;
    public String code;
    public String display;
    public List<ConceptReferenceDesignationEntity> designation;
    public List<ValueSetContainEntity> contains;
    public String parentContainId;
    public String valueSetExpansionId;
    
    public static ValueSetContainEntity fromValueSetExpansionContainsComponent(ValueSetExpansionContainsComponent obj) {
        if(obj == null) return null;
        var ent = new ValueSetContainEntity();
        ent.system = obj.getSystem();
        ent._abstract = obj.getAbstract();
        ent.inactive = obj.getInactive();
        ent.code = obj.getCode();
        ent.display = obj.getDisplay();
        ent.designation = transform(obj.getDesignation(), ConceptReferenceDesignationEntity::fromConceptReferenceDesignationComponent);
        ent.contains = transform(obj.getContains(), ValueSetContainEntity::fromValueSetExpansionContainsComponent);
        
        return ent;
    }
    
    public static ValueSetExpansionContainsComponent toValueSetExpansionContainsComponent(ValueSetContainEntity ent) {
        if(ent == null) return null;
        var obj = new ValueSetExpansionContainsComponent();
        obj.setSystem(ent.system);
        obj.setAbstract(ent._abstract);
        obj.setInactive(ent.inactive);
        obj.setCode(ent.code);
        obj.setDisplay(ent.display);
        obj.setDesignation(transform(ent.designation, ConceptReferenceDesignationEntity::toConceptReferenceDesignationComponent));
        obj.setContains(transform(ent.contains, ValueSetContainEntity::toValueSetExpansionContainsComponent));
        
        return obj;
    }
}
