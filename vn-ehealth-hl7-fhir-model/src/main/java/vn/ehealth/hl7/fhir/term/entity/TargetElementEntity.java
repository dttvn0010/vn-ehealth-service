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
 */
@Document(collection = "target")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class TargetElementEntity extends BaseResource{
    @Id
    public ObjectId id;
    public String elementEntityID;
    public String code;
    public String display;
    public String equivalence;
    public String comment;
    public List<DependOnEntity> dependsOn;// OtherElementComponent
}
