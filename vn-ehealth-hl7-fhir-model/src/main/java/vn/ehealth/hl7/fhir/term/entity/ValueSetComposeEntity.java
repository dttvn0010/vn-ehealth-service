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

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "valueSetCompose")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class ValueSetComposeEntity extends BaseResource {
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public Date lockedDate;
    public Boolean inactive;
    public String valueSetId;
    public List<ConceptSetEntity> include;
    
}
