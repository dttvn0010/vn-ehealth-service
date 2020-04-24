package vn.ehealth.emr.dto.ehr;

import java.util.Date;

import org.hl7.fhir.r4.model.Encounter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.emr.dto.BaseDTO;
import vn.ehealth.emr.dto.CodeableConceptDTO;
import vn.ehealth.emr.dto.ReferenceDTO;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;

@JsonInclude(Include.NON_NULL)
public class EncounterDTO extends BaseDTO{
    
    public CodeableConceptDTO type;
    public String mohNumber;
    public ReferenceDTO patient;
    public ReferenceDTO serviceProvider;
    public ReferenceDTO partOf;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date start;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date end;
    
    
    public static EncounterDTO fromFhir(Encounter obj) {
        
        if(obj == null) return null;
        
        var dto = new EncounterDTO();
        dto.id = obj.getId();
        
        if(obj.hasPartOf() && obj.getPartOf().hasReference()) {
            var type = FhirUtil.findConceptBySystem(obj.getType(), CodeSystemValue.LOAI_KHAM_BENH);
            dto.type = CodeableConceptDTO.fromFhir(type);
        }else {
            var type = FhirUtil.findConceptBySystem(obj.getType(), CodeSystemValue.KHOA_DIEU_TRI);
            dto.type = CodeableConceptDTO.fromFhir(type);
        }
        
        var mohId = FPUtil.findFirst(obj.getIdentifier(), 
                x -> IdentifierSystem.MA_HO_SO.equals(x.getSystem()));

        if(mohId != null && mohId.hasValue()) {
            dto.mohNumber = mohId.getValue();
        }
        
        dto.patient = ReferenceDTO.fromFhir(obj.getSubject());
        dto.serviceProvider = ReferenceDTO.fromFhir(obj.getServiceProvider());
        dto.partOf = ReferenceDTO.fromFhir(obj.getPartOf());
        
        if(obj.hasPeriod()) {
            dto.start = obj.getPeriod().getStart();
            dto.end = obj.getPeriod().getEnd();
        }
        
        return dto;
    }    
    
}
