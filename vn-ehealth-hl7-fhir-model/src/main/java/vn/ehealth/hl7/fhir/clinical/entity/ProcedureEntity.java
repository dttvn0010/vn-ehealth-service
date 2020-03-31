package vn.ehealth.hl7.fhir.clinical.entity;

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
import vn.ehealth.hl7.fhir.core.entity.BasePrimitiveType;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;
import vn.ehealth.hl7.fhir.core.view.DTOView;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

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
        
        @JsonView(DTOView.class)
        public Map<String, Object> getDto() {
            return mapOf(
                    "function", BaseCodeableConcept.toDto(function),
                    "actor", BaseReference.toDto(actor),
                    "onBehalfOf", BaseReference.toDto(onBehalfOf)
                );
        }
        
        public static Map<String, Object>  toDto(ProcedurePerformer performer) {
            if(performer == null) return null;
            return performer.getDto();
        }
    }

    @Id
    @JsonIgnore public ObjectId id;
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
    
    
    @JsonView(DTOView.class)
    public Map<String, Object> getDto() {
        
        var dto = mapOf(                    
                    "serviceRequest", BaseReference.toDto(getFirst(basedOn)),
                    "category", BaseCodeableConcept.toDto(category),
                    "code", BaseCodeableConcept.toDto(code),
                    "patient", BaseReference.toDto(subject),
                    "encounter", BaseReference.toDto(encounter),
                    "recorder", BaseReference.toDto(recorder),
                    "asserter", BaseReference.toDto(asserter),
                    "performer", transform(performer, ProcedurePerformer::toDto),
                    "bodySite", transform(bodySite, BaseCodeableConcept::toDto),
                    "outcome", BaseCodeableConcept.toDto(outcome),
                    "diagnosticReport", BaseReference.toDto(getFirst(report))
                );
        
        if(performed instanceof BasePrimitiveType) {
            var value = ((BasePrimitiveType) performed).value;
            if(value instanceof Date) {
                dto.put("performedDate", value);
            }
        }
        
        return dto;
    }
    
    public static Map<String, Object>  toDto(ProcedureEntity ent) {
        if(ent == null) return null;
        return ent.getDto();
    }
}
