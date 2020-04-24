package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseBackboneElement;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseMetadataResource;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "codeSystem")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class CodeSystemEntity extends BaseMetadataResource {
    
    public static class CodeSystemFilter extends BaseBackboneElement{

        public String code;
        public String description;
        public List<String> operator;
        public String value;
    }

    public static class Property extends BaseBackboneElement{

        public String code;
        public String uri;
        public String description;
        public String type;
    }    
  
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String purpose;
    public String copyright;
    public Boolean caseSensitive;
    public String valueSet;
    public String hierarchyMeaning;
    public Boolean compositional;
    public Boolean versionNeeded;
    public String content;
    public String supplements;
    public Integer count;
    public List<CodeSystemFilter> filter;
    public List<Property> property;
    
    @Transient
    @JsonIgnore
    public List<ConceptEntity> concept;

}
