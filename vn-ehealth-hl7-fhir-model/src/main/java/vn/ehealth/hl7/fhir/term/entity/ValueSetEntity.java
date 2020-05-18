package vn.ehealth.hl7.fhir.term.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseBackboneElement;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseMetadataResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "valueSet")
//@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class ValueSetEntity extends BaseMetadataResource {
    
    public static class ConceptReferenceDesignation extends BaseBackboneElement {
        protected String language;
        protected BaseCoding use;
        protected String value;
    }
  
    public static class ConceptReference extends BaseBackboneElement {
        protected String code;
        protected String display;
        protected List<ConceptReferenceDesignation> designation;
    }
    
    public static class ConceptSet extends BaseBackboneElement {
        protected String system;
        protected String version;
        protected List<ConceptReference> concept;
        protected List<ConceptReference> filter;
        protected List<String> valueSet;
    }
    
    public static class ValueSetExpansionContains extends BaseBackboneElement {
        protected String system;
        protected Boolean abstract_;
        protected Boolean inactive;
        protected String version;
        protected String code;
        protected String display;
        protected List<ConceptReferenceDesignation> designation;
        protected List<ValueSetExpansionContains> contains;

    }
    
    public static class ValueSetCompose extends BaseBackboneElement {
        protected Date lockedDate;
        protected Boolean inactive;
        protected List<ConceptSet> include;
        protected List<ConceptSet> exclude;
    }
    
    public static class ValueSetExpansionParameter extends BaseBackboneElement {
        protected String name;
        @JsonIgnore protected BaseType value;
    }
    
    public static class ValueSetExpansion extends BaseBackboneElement {
        protected String identifier;
        protected Date timestamp;
        protected Integer total;
        protected Integer offset;
        protected List<ValueSetExpansionParameter> parameter;
        protected List<ValueSetExpansionContains> contains;  
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;

    protected List<BaseIdentifier> identifier;

    protected Boolean immutable;
    protected String purpose;
    protected String copyright;
    protected ValueSetCompose compose;
    protected ValueSetExpansion expansion;
}
