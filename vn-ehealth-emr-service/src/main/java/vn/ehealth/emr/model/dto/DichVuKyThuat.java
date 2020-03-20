package vn.ehealth.emr.model.dto;

import java.util.Map;

import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;

public abstract class DichVuKyThuat extends BaseModelDTO {

    public abstract Map<String, Object> toFhir();
    protected abstract void fromFhir(ServiceRequest serviceRequest);
    
    public DichVuKyThuat() {
        super();
    }
    
    public DichVuKyThuat(ServiceRequest serviceRequest) {
        super(serviceRequest);
        fromFhir(serviceRequest);
    }
    
    @Override
    public ResourceType getType() {
        return ResourceType.ServiceRequest;
    }
}
