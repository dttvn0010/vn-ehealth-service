package vn.ehealth.hl7.fhir.term.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseResource;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "valueSetExpansion")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ValueSetExpansionEntity extends BaseResource {
    public static class ValueSetExpansionParameter {
        public String name;
        @JsonIgnore public Type value;
        
    }
    
    @Id
    public ObjectId id;
    public String identifier;
    public Date timestamp;
    public int total;
    public int offset;
    public List<ValueSetExpansionParameter> parameter;
    public String valueSetId;
    public List<ValueSetContainEntity> contains;
     
}
