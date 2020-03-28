package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "conceptMap")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ConceptMapEntity extends BaseResource {

    public static class OtherElement {
        protected String property;
        protected String system;
        protected String value;
        protected String display;
    }
    
    public static class TargetElement {        
        protected String code;
        protected String display;
        protected String equivalence;
        protected String comment;
        protected List<OtherElement> dependsOn;
        protected List<OtherElement> product;
    }
    
    public static class SourceElement {
        protected String code;
        protected String display;
        protected List<TargetElement> target;
    }
    
    public static class ConceptMapGroupUnmapped {
        
        protected String mode;
        protected String code;
        protected String display;
        protected String url;
    }
    
    public static class ConceptMapGroup {
        protected String source;
        protected String sourceVersion;
        protected String target;
        protected String targetVersion;
        protected List<SourceElement> element;
        protected ConceptMapGroupUnmapped unmapped;
    }
    
    @Id
    public ObjectId id;
    
    public BaseIdentifier identifier;
    public String purpose;
    public String copyright;
    @JsonIgnore public Type source;
    @JsonIgnore  public Type target;
    public List<ConceptMapGroup> group;

}
