package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;


import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "procedureRequest")
public class ProcedureRequestEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseReference> definition;
    public List<BaseReference> basedOn;
    public List<BaseReference> replaces;
    public BaseIdentifier requisition;
    public String status;
    public String intent;
    public String priority;
    public Boolean doNotPerform;
    public List<CodeableConcept> category;
    public CodeableConcept code;
    public BaseReference subject;
    public BaseReference context;
    public Type occurrence;
    public Type asNeeded;
    public Date authoredOn;
    public ProcedureRequestRequesterEntity requester;
    public CodeableConcept performerType;
    public BaseReference performer;
    public List<CodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public List<BaseReference> supportingInfo;
    public List<BaseReference> specimen;
    public List<CodeableConcept> bodySite;
    public List<BaseAnnotation> note;
    public List<BaseReference> relevantHistory;
}
