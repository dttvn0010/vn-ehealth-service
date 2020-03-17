package vn.ehealth.emr.model.dto;

import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

public class BaseModelDTO {

    public String id;
    
    public BaseModelDTO() {
        
    }
    
    public BaseModelDTO(Resource ent) {
        if(ent != null) {
            this.id = ent.getId();
        }
    }
    
    public static Reference toReference(BaseModelDTO dto) {
        if(dto == null) return null;
        var ref = new Reference(dto.id);
        return ref;
    }
}
