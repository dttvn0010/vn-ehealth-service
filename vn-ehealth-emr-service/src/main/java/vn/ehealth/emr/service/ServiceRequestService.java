package vn.ehealth.emr.service;

import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.stereotype.Service;

import vn.ehealth.hl7.fhir.diagnostic.entity.ServiceRequestEntity;

@Service
public class ServiceRequestService extends ResourceService<ServiceRequestEntity, ServiceRequest> {

    @Override
    ServiceRequestEntity fromFhir(ServiceRequest obj) {
        return ServiceRequestEntity.fromServiceRequest(obj);
    }

    @Override
    ServiceRequest toFhir(ServiceRequestEntity ent) {
        return ServiceRequestEntity.toServiceRequest(ent);
    }

}
