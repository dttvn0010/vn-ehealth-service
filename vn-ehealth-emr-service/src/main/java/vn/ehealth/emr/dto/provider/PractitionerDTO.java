package vn.ehealth.emr.dto.provider;

import org.hl7.fhir.r4.model.Practitioner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.dto.BaseDTO;
import vn.ehealth.emr.dto.HumanNameDTO;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;

@JsonInclude(Include.NON_NULL)
public class PractitionerDTO extends BaseDTO {

    public HumanNameDTO name;
    public String qualityId;
    
    public static PractitionerDTO fromFhir(Practitioner obj) {
        
        if(obj == null) return null;
        
        var dto = new PractitionerDTO();
        dto.name = HumanNameDTO.fromFhir(obj.getNameFirstRep());
        
        var qualityId = FPUtil.findFirst(obj.getIdentifier(), 
                                x -> IdentifierSystem.CHUNG_CHI_HANH_NGHE.equals(x.getSystem()));

        if(qualityId != null) {
            dto.qualityId = qualityId.getValue();
        }
        
        return dto;
    }
}
