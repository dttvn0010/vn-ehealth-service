package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;
import vn.ehealth.hl7.fhir.core.view.DTOView;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;


@Document(collection = "serviceRequest")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1, 'basedOn.reference':1, 'subject.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class ServiceRequestEntity extends BaseResource {
    @Id
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
    
    
    @JsonView(DTOView.class)
    public Map<String, Object> getDto() {
        return mapOf(
                    "category", transform(category, BaseCodeableConcept::toDto),
                    "code", BaseCodeableConcept.toDto(code),
                    "orderDetail", BaseCodeableConcept.toDto(getFirst(orderDetail)),
                    "patient", BaseReference.toDto(subject),
                    "encounter", BaseReference.toDto(encounter),
                    "authoredOn", authoredOn,
                    "requester", BaseReference.toDto(requester)                    
                );
    }
    
    public static Map<String, Object> toDto(DiagnosticReportEntity ent) {
        if(ent == null) return null;
        return ent.getDto();
    }
}
