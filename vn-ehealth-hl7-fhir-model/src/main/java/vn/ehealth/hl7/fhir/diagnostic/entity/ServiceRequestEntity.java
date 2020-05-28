package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "serviceRequest")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1, 'basedOn.reference':1, 'subject.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class ServiceRequestEntity extends BaseResource {
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<String> instantiatesCanonical;
    public List<String> instantiatesUri;
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
    public BaseType quantity;
    public BaseReference subject;    
    public BaseReference encounter;
    public BaseType occurrence;
    public BaseType asNeeded;
    public Date authoredOn;
    public BaseReference requester;
    public BaseCodeableConcept performerType;
    public List<BaseReference> performer;
    public List<BaseCodeableConcept> locationCode;
    public List<BaseReference> locationReference;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public List<BaseReference> insurance;
    public List<BaseReference> supportingInfo;
    public List<BaseReference> specimen;
    public List<BaseCodeableConcept> bodySite;
    public List<BaseAnnotation> note;
    public String patientInstruction;
    public List<BaseReference> relevantHistory;
}
