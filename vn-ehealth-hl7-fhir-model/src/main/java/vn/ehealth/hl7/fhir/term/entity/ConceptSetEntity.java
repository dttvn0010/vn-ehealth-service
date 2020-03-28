package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseResource;
/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
@Document(collection = "conceptSetEntity")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ConceptSetEntity extends BaseResource{
    
    public static class ConceptSetFilter {

        public String property;
        public String value;
        public String op;
    }

    
    @Id
    public ObjectId id;
    public  String system;
    public List<ConceptReferenceEntity> concept;
    public List<ConceptSetFilter> filter;
    public String valueSetComposeId;
    public String type;
}
