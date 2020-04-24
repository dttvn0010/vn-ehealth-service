package vn.ehealth.emr.dto.diagnostic;

import java.util.Date;

import org.hl7.fhir.r4.model.DiagnosticReport;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.dto.BaseDTO;
import vn.ehealth.emr.dto.CodeableConceptDTO;
import vn.ehealth.emr.dto.ReferenceDTO;

@JsonInclude(Include.NON_NULL)
public class DiagnosticReportDTO extends BaseDTO{

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date issued;
    public ReferenceDTO patient;
    public ReferenceDTO encounter;
    public CodeableConceptDTO category;
    public CodeableConceptDTO code;
    public ReferenceDTO performer;
    public ReferenceDTO resultsInterpreter;
    public ReferenceDTO specimen;
    public ReferenceDTO result;
    public String conclusion;
    
    public static DiagnosticReportDTO fromFhir(DiagnosticReport obj) {
        if(obj == null) return null;
        
        var dto = new DiagnosticReportDTO();
        dto.issued = obj.getIssued();
        dto.patient = ReferenceDTO.fromFhir(obj.getSubject());
        dto.encounter = ReferenceDTO.fromFhir(obj.getEncounter());
        dto.category = CodeableConceptDTO.fromFhir(obj.getCategoryFirstRep());
        dto.code = CodeableConceptDTO.fromFhir(obj.getCode());
        dto.performer = ReferenceDTO.fromFhir(obj.getPerformerFirstRep());
        dto.resultsInterpreter = ReferenceDTO.fromFhir(obj.getResultFirstRep());
        dto.specimen = ReferenceDTO.fromFhir(obj.getSpecimenFirstRep());
        dto.result = ReferenceDTO.fromFhir(obj.getResultFirstRep());
        dto.conclusion = obj.getConclusion();
        
        return dto;
    }
}
