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
@Document(collection = "element")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ElementEntity extends BaseResource{
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public String groupElementID;
    public String code;
    public String display;
    public List<TargetElementEntity> target;//TargetElementComponent
    
}
