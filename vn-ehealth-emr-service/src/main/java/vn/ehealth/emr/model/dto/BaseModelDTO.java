package vn.ehealth.emr.model.dto;

import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

public class BaseModelDTO {

    public String fhirId;
    
    public BaseModelDTO() {
        
    }
    
    public BaseModelDTO(BaseResource ent) {
        if(ent != null) {
            this.fhirId = ent.fhirId;
        }
    }
    
    public static BaseReference toReference(BaseModelDTO dto) {
        if(dto == null) return null;
        var ref = new BaseReference();
        ref.reference = dto.fhirId;
        return ref;
    }
}
