package vn.ehealth.hl7.fhir.term.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionComponent;
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
@Document(collection = "valueSetExpansion")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ValueSetExpansionEntity extends BaseResource {
    @Id
    public ObjectId id;
    public String identifier;
    public Date timestamp;
    public int total;
    public int offset;
    public List<ValueSetExpansionParameterEntity> parameter;
    public String valueSetId;
    public List<ValueSetContainEntity> contains;
    
    public static ValueSetExpansionEntity fromValueSetExpansionComponent(ValueSetExpansionComponent obj) {
        if(obj == null) return null;
        var ent = new ValueSetExpansionEntity();
        ent.identifier = obj.getIdentifier();
        ent.timestamp = obj.getTimestamp();
        ent.total = obj.getTotal();
        ent.offset = obj.getOffset();
        ent.parameter = transform(obj.getParameter(), ValueSetExpansionParameterEntity::fromValueSetExpansionParameterComponent);
        ent.contains = transform(obj.getContains(), ValueSetContainEntity::fromValueSetExpansionContainsComponent);
        return ent;        
    }
    
    public static ValueSetExpansionComponent toValueSetExpansionComponent(ValueSetExpansionEntity ent) {
        if(ent == null) return null;
        var obj = new ValueSetExpansionComponent();
        obj.setIdentifier(ent.identifier);
        obj.setTimestamp(ent.timestamp);
        obj.setTotal(ent.total);
        obj.setOffset(ent.offset);
        obj.setParameter(transform(ent.parameter, ValueSetExpansionParameterEntity::toValueSetExpansionParameterComponent));
        obj.setContains(transform(ent.contains, ValueSetContainEntity::toValueSetExpansionContainsComponent));
        return obj;
    }
     
}
