package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.List;



import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "bodySite")
public class BodySiteEntity extends BaseResource {

    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public CodeableConcept code;
    public List<CodeableConcept> qualifier;
    public String description;
    public List<BaseAttachment> image;
    public BaseReference patient;
}
