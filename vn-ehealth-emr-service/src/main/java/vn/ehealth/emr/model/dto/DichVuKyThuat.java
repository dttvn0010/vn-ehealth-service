package vn.ehealth.emr.model.dto;

import java.util.Map;

import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;

import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

public abstract class DichVuKyThuat extends BaseModelDTO {
    public String patientId;
    public String encounterId;
    
    public abstract Map<String, Object> toFhir();
    protected abstract void fromFhir(ServiceRequest serviceRequest);
    
    public DichVuKyThuat() {
        super();
    }
    
    public DichVuKyThuat(ServiceRequest serviceRequest) {
        super(serviceRequest);
        if(serviceRequest != null) {
            this.patientId = idFromRef(serviceRequest.getSubject());
            this.encounterId = idFromRef(serviceRequest.getEncounter());
            fromFhir(serviceRequest);
        }
    }
    
    @Override
    public ResourceType getType() {
        return ResourceType.ServiceRequest;
    }
}
