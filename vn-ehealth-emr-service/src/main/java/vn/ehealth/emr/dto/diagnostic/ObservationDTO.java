package vn.ehealth.emr.dto.diagnostic;

import java.math.BigDecimal;
import java.util.Date;

import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.PrimitiveType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.dto.BaseDTO;
import vn.ehealth.emr.dto.CodeableConceptDTO;
import vn.ehealth.emr.dto.ReferenceDTO;

@JsonInclude(Include.NON_NULL)
public class ObservationDTO extends BaseDTO{

    public static class ObservationReferenceRangeDTO {
        BigDecimal low;
        BigDecimal high;
    }
    
    public ReferenceDTO patient;
    public ReferenceDTO encounter;
    public CodeableConceptDTO code;
    public CodeableConceptDTO interpretation;
    public ObservationReferenceRangeDTO referenceRange;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date issued;
    
    public ReferenceDTO performer;
    
    public String value;
    
    public static ObservationDTO fromFhir(Observation obj) {
        if(obj == null) return null;
        
        var dto = new ObservationDTO();
        dto.id = obj.getId();
        
        dto.patient = ReferenceDTO.fromFhir(obj.getSubject());
        dto.encounter = ReferenceDTO.fromFhir(obj.getEncounter());
        dto.code = CodeableConceptDTO.fromFhir(obj.getCode());
        dto.issued = obj.getIssued();
        dto.performer = ReferenceDTO.fromFhir(obj.getPerformerFirstRep());
        
        if(obj.getValue() instanceof PrimitiveType) {
            dto.value = ((PrimitiveType<?>) obj.getValue()).getValueAsString();
        }
        
        dto.interpretation = CodeableConceptDTO.fromFhir(obj.getInterpretationFirstRep());
        
        if(obj.hasReferenceRange()) {
            var range = obj.getReferenceRangeFirstRep();
            dto.referenceRange = new ObservationReferenceRangeDTO();
            
            if(range.hasLow()) {
                dto.referenceRange.low = range.getLow().getValue();                
            }
            
            if(range.hasHigh()) {
                dto.referenceRange.high = range.getHigh().getValue();
            }
        }
        
        return dto;
    }
}
