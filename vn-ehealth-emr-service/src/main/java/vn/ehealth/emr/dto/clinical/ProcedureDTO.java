package vn.ehealth.emr.dto.clinical;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Procedure.ProcedurePerformerComponent;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.dto.BaseDTO;
import vn.ehealth.emr.dto.ConceptDTO;
import vn.ehealth.emr.dto.ReferenceDTO;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@JsonInclude(Include.NON_NULL)
public class ProcedureDTO extends BaseDTO{
    
    public static class ProcedurePerformerDTO {
        public ConceptDTO function;
        public ReferenceDTO actor;
        public ReferenceDTO onBehalfOf;
        
        public static ProcedurePerformerDTO fromFhir(ProcedurePerformerComponent obj) {
            if(obj == null) return null;
            
            var dto = new ProcedurePerformerDTO();
            dto.function = ConceptDTO.fromFhir(obj.getFunction());
            dto.actor = ReferenceDTO.fromFhir(obj.getActor());
            dto.onBehalfOf = ReferenceDTO.fromFhir(obj.getOnBehalfOf());
            
            return dto;
        }
    }
    
    public ReferenceDTO serviceRequest;
    public ConceptDTO category;
    public ConceptDTO code;
    public ReferenceDTO patient;
    public ReferenceDTO encounter;
    public ReferenceDTO recorder;
    public ReferenceDTO asserter;
    public List<ProcedurePerformerDTO> performer;
    public List<ConceptDTO> bodySite;
    public ConceptDTO outcome;
    public ReferenceDTO diagnosticReport;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date performedDate;
    
    public static ProcedureDTO fromFhir(Procedure obj) {
        if(obj == null) return null;
        
        var dto = new ProcedureDTO();
        
        dto.id = obj.getId();
        dto.serviceRequest = ReferenceDTO.fromFhir(obj.getBasedOnFirstRep());
        dto.category = ConceptDTO.fromFhir(obj.getCategory());
        dto.code = ConceptDTO.fromFhir(obj.getCode());
        dto.patient = ReferenceDTO.fromFhir(obj.getSubject());
        dto.encounter = ReferenceDTO.fromFhir(obj.getEncounter());
        dto.recorder = ReferenceDTO.fromFhir(obj.getRecorder());
        dto.asserter = ReferenceDTO.fromFhir(obj.getAsserter());
        dto.performer = transform(obj.getPerformer(), ProcedurePerformerDTO::fromFhir);
        dto.bodySite = transform(obj.getBodySite(), ConceptDTO::fromFhir);
        dto.outcome = ConceptDTO.fromFhir(obj.getOutcome());
        dto.diagnosticReport = ReferenceDTO.fromFhir(obj.getReportFirstRep());
        
        if(obj.hasPerformedDateTimeType()) {
            dto.performedDate = obj.getPerformedDateTimeType().getValue();
        }
        
        return dto;
    }
}