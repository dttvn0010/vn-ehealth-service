package vn.ehealth.hl7.fhir.clinical.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.diagnostic.entity.ServiceRequestEntity;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

@Component
public class ServiceRequestEntityToFHIRServiceRequest implements Transformer<ServiceRequestEntity, ServiceRequest> {
	@Override
    public ServiceRequest transform(ServiceRequestEntity ent) {
        var obj = ServiceRequestEntity.toServiceRequest(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "ServiceRequest-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhir_id);
        return obj;
    }
}
