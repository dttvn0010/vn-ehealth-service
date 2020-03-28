package vn.ehealth.hl7.fhir.term.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "valueSet")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ValueSetEntity extends BaseResource {
    
    public static class ConceptReferenceDesignation {
        protected String language;
        protected BaseCoding use;
        protected String value;
    }
  
    public static class ConceptReference {
        protected String code;
        protected String display;
        protected List<ConceptReferenceDesignation> designation;
    }
    
    public static class ConceptSet  {
        protected String system;
        protected String version;
        protected List<ConceptReference> concept;
        protected List<ConceptReference> filter;
        protected List<String> valueSet;
    }
    
    public static class ValueSetExpansionContains {
        protected String system;
        protected Boolean abstract_;
        protected Boolean inactive;
        protected String version;
        protected String code;
        protected String display;
        protected List<ConceptReferenceDesignation> designation;
        protected List<ValueSetExpansionContains> contains;

    }
    
    public static class ValueSetCompose {
        protected Date lockedDate;
        protected Boolean inactive;
        protected List<ConceptSet> include;
        protected List<ConceptSet> exclude;
    }
    
    public static class ValueSetExpansionParameter {
        protected String name;
        @JsonIgnore protected Type value;
    }
    
    public static class ValueSetExpansion {
        protected String identifier;
        protected Date timestamp;
        protected Integer total;
        protected Integer offset;
        protected List<ValueSetExpansionParameter> parameter;
        protected List<ValueSetExpansionContains> contains;  
    }
    
    @Id
    public ObjectId id;

    protected List<BaseIdentifier> identifier;

    protected Boolean immutable;
    protected String purpose;
    protected String copyright;
    protected ValueSetCompose compose;
    protected ValueSetExpansion expansion;
}
