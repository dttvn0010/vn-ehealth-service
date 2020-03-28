package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "serviceRequest")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1, 'basedOn.reference':1, 'subject.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class ServiceRequestEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    //List<BaseReference> definition;
    public List<BaseReference> basedOn;
    public List<BaseReference> replaces;
    public BaseIdentifier requisition;
    public String status;
    public String intent;
    public String priority;
    public Boolean doNotPerform;
    public List<BaseCodeableConcept> category;
    public BaseCodeableConcept code;
    public List<BaseCodeableConcept> orderDetail;
    @JsonIgnore public Type quantity;
    public BaseReference subject;    
    public BaseReference encounter;
    public Type occurrence;
    public Type asNeeded;
    public Date authoredOn;
    public BaseReference requester;
    public BaseCodeableConcept performerType;
    public List<BaseReference> performer;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public List<BaseReference> supportingInfo;
    public List<BaseReference> specimen;
    public List<BaseCodeableConcept> bodySite;
    public List<BaseAnnotation> note;
    public List<BaseReference> relevantHistory;
}
