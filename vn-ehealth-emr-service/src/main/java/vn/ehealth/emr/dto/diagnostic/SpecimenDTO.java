package vn.ehealth.emr.dto.diagnostic;

import java.util.Date;

import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.Specimen.SpecimenCollectionComponent;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.dto.BaseDTO;
import vn.ehealth.emr.dto.ConceptDTO;
import vn.ehealth.emr.dto.ReferenceDTO;

@JsonInclude(Include.NON_NULL)
public class SpecimenDTO extends BaseDTO{
    
    @JsonInclude(Include.NON_NULL)
    public static class SpecimenCollectionDTO {
        public ReferenceDTO collector;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date collected;
        
        public ConceptDTO bodySite;
        
        public static SpecimenCollectionDTO fromFhir(SpecimenCollectionComponent obj) {
            if(obj == null) return null;
            
            var dto = new SpecimenCollectionDTO();
            dto.collector = ReferenceDTO.fromFhir(obj.getCollector());

            if(obj.hasCollectedDateTimeType()) {
                dto.collected = obj.getCollectedDateTimeType().getValue();
            }
            
            dto.bodySite = ConceptDTO.fromFhir(obj.getBodySite());
            
            return dto;
        }
    }
    
    public ConceptDTO type;
    public ReferenceDTO patient;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date receivedTime;
    
    public ReferenceDTO request;
    public SpecimenCollectionDTO collection;
    public String note;
    
    public static SpecimenDTO fromFhir(Specimen obj) {
        if(obj == null) return null;
        
        var dto = new SpecimenDTO();
        dto.id = obj.getId();
        dto.type = ConceptDTO.fromFhir(obj.getType());
        dto.patient = ReferenceDTO.fromFhir(obj.getSubject());
        
        if(obj.hasReceivedTime()) {
            dto.receivedTime = obj.getReceivedTime();
        }
        
        dto.request = ReferenceDTO.fromFhir(obj.getRequestFirstRep());
        dto.collection = SpecimenCollectionDTO.fromFhir(obj.getCollection());
        
        if(obj.hasNote()) {
            dto.note = obj.getNoteFirstRep().getText();
        }
        
        return dto;
        
    }
}
