package vn.ehealth.emr.model.dto;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BaseModelDTO {

    public String id;
    
    @JsonIgnore
    public IdType getIdPart() {
        return new IdType(id);
    }
    
    public BaseModelDTO() {
        
    }
    
    public BaseModelDTO(Resource obj) {
        if(obj != null) {
            this.id = obj.getId();
        }
    }
    
    public abstract ResourceType getType();
}
