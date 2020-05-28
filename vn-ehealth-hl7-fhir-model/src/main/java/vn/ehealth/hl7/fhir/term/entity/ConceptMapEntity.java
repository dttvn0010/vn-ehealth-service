package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseBackboneElement;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseMetadataResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "conceptMap")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class ConceptMapEntity extends BaseMetadataResource {

    public static class OtherElement extends BaseBackboneElement {
        protected String property;
        protected String system;
        protected String value;
        protected String display;
    }
    
    public static class TargetElement extends BaseBackboneElement {        
        protected String code;
        protected String display;
        protected String equivalence;
        protected String comment;
        protected List<OtherElement> dependsOn;
        protected List<OtherElement> product;
    }
    
    public static class SourceElement extends BaseBackboneElement {
        protected String code;
        protected String display;
        protected List<TargetElement> target;
    }
    
    public static class ConceptMapGroupUnmapped extends BaseBackboneElement {
        
        protected String mode;
        protected String code;
        protected String display;
        protected String url;
    }
    
    public static class ConceptMapGroup extends BaseBackboneElement {
        protected String source;
        protected String sourceVersion;
        protected String target;
        protected String targetVersion;
        protected List<SourceElement> element;
        protected ConceptMapGroupUnmapped unmapped;
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    
    public BaseIdentifier identifier;
    public String purpose;
    public String copyright;
    public BaseType source;
    public BaseType target;
    public List<ConceptMapGroup> group;

}
