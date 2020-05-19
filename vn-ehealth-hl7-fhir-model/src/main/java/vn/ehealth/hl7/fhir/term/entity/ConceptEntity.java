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
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseMetadataResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;
/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */

@Document(collection = "concept")
//@CompoundIndex(def = "{'_fhirId':1,'codeSystemId':1,'_active':1,'_version':1}", name = "index_by_default")
public class ConceptEntity extends BaseMetadataResource {
    
	public static class ConceptDefinitionDesignation extends BaseBackboneElement{
        public String language;
        public BaseCoding use;
        public String value;
    }
    
    public static class ConceptProperty extends BaseBackboneElement{
        public String code;
        public BaseType value;
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    
    public String codeSystemId;
    public String code;
    public String display;
    public String definition;
    public List<ConceptDefinitionDesignation> designation;
    public List<ConceptProperty> property;
    
    @Transient
    public List<ConceptEntity> concept;
    
    public String parentConceptId;
    public int level;
}
