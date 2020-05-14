package vn.ehealth.emr.dto.clinical;

import org.hl7.fhir.r4.model.ServiceRequest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.diagnostic.entity.ServiceRequestEntity;

@JsonInclude(Include.NON_NULL)
public class ServiceRequestDTO extends ServiceRequestEntity{    
        
    public static ServiceRequestDTO fromFhir(ServiceRequest obj) {
        return DataConvertUtil.fhirToEntity(obj, ServiceRequestDTO.class);
    }
}
