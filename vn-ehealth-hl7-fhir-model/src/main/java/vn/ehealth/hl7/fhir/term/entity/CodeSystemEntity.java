package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "codeSystem")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class CodeSystemEntity extends BaseResource {
    
    public static class CodeSystemFilter{

        public String code;
        public String description;
        public List<String> operator;
        public String value;
    }

    public static class Property{

        public String code;
        public String uri;
        public String description;
        public String type;
    }
    
    public static class ConceptDefinitionDesignation {
        public String language;
        public BaseCoding use;
        public String value;
    }
    
    public static class ConceptProperty {
        public String code;
        public BaseType value;
    }
    
    public static class ConceptDefinition {
        public String code;
        public String display;
        public String definition;
        public List<ConceptDefinitionDesignation> designation;
        public List<ConceptProperty> property;
        public List<ConceptDefinition> concept;
    }
    
    @Id
    public ObjectId id;
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
    public List<ConceptDefinition> concept;
    
    /*
    public String name;
    public String title;
    public String status;
    public boolean experimental;
    public Date date;
    public String publisher;
    public List<ContactDetail> contact;
    public String description;
    public List<BaseUsageContext> useContext;
    public List<BaseCodeableConcept> jurisdiction;
    */
}
