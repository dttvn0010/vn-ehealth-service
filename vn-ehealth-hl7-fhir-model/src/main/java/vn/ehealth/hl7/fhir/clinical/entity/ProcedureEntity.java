package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "procedure")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1, 'basedOn.reference':1, 'subject.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class ProcedureEntity extends BaseResource {

    public static class ProcedureFocalDevice {

        public BaseCodeableConcept action;
        public BaseReference manipulated;
    }
    
    public static class ProcedurePerformer {

        public BaseCodeableConcept function;
        public BaseReference actor;
        public BaseReference onBehalfOf;
    }

    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<String> instantiatesCanonical;
    public List<String> instantiatesUri;
    public List<BaseReference> basedOn;
    public List<BaseReference> partOf;
    public String status;
    public BaseCodeableConcept statusReason;
    public BaseCodeableConcept category;
    public BaseCodeableConcept code;
    public BaseReference subject;
    public BaseReference encounter;
    public BaseType performed;
    public Date performedDate;
    public BaseReference recorder;
    public BaseReference asserter;
    public List<ProcedurePerformer> performer;
    public BaseReference location;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public List<BaseCodeableConcept> bodySite;
    public BaseCodeableConcept outcome;
    public List<BaseReference> report;
    public List<BaseCodeableConcept> complication;
    public List<BaseReference> complicationDetail;
    public List<BaseCodeableConcept> followUp;
    public List<BaseAnnotation> note;
    public List<ProcedureFocalDevice> focalDevice;
    public List<BaseReference> usedReference;
    public List<BaseCodeableConcept> usedCode;
}
