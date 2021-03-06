package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseResource;
/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
@Document(collection = "conceptSetEntity")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class ConceptSetEntity extends BaseResource{
    
    public static class ConceptSetFilter {

        public String property;
        public String value;
        public String op;
    }

    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public  String system;
    public List<ConceptReferenceEntity> concept;
    public List<ConceptSetFilter> filter;
    public String valueSetComposeId;
    public String type;
}
