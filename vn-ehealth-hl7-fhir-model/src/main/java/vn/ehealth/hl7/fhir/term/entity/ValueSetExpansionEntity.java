package vn.ehealth.hl7.fhir.term.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "valueSetExpansion")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class ValueSetExpansionEntity extends BaseResource {
    public static class ValueSetExpansionParameter {
        public String name;
        public BaseType value;
        
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public String identifier;
    public Date timestamp;
    public int total;
    public int offset;
    public List<ValueSetExpansionParameter> parameter;
    public String valueSetId;
    public List<ValueSetContainEntity> contains;
     
}
