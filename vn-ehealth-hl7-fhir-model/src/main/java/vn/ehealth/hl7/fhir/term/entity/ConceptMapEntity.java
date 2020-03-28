package vn.ehealth.hl7.fhir.term.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseUsageContext;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "conceptMap")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ConceptMapEntity extends BaseResource {
    @Id
    public ObjectId id;
    public String url;
    public BaseIdentifier identifier;
    public String name;
    public String title;
    public String status;
    public boolean experimental;
    public Date date;
    public String publisher;
    public List<ContactDetailEntity> contact;
    public String description;
    public List<BaseUsageContext> useContext;
    public List<BaseCodeableConcept> jurisdiction;
    public String purpose;
    public String copyright;
    @JsonIgnore public Type source;
    @JsonIgnore public Type target;
    public List<GroupElementEntity> group;// ConceptMapGroupComponent
}
