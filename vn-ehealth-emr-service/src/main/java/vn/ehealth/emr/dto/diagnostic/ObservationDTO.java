package vn.ehealth.emr.dto.diagnostic;

import java.util.Date;

import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.PrimitiveType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.dto.BaseDTO;
import vn.ehealth.emr.dto.ConceptDTO;
import vn.ehealth.emr.dto.ReferenceDTO;

@JsonInclude(Include.NON_NULL)
public class ObservationDTO extends BaseDTO{

    public ReferenceDTO patient;
    public ReferenceDTO encounter;
    public ConceptDTO code;
    
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
        dto.code = ConceptDTO.fromFhir(obj.getCode());
        dto.issued = obj.getIssued();
        dto.performer = ReferenceDTO.fromFhir(obj.getPerformerFirstRep());
        
        if(obj.getValue() instanceof PrimitiveType) {
            dto.value = ((PrimitiveType<?>) obj.getValue()).getValueAsString();
        }
        
        return dto;
    }
}
