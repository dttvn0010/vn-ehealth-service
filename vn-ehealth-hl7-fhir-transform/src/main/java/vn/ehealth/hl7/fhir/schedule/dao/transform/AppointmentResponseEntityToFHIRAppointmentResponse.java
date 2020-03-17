package vn.ehealth.hl7.fhir.schedule.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.AppointmentResponse;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.schedule.entity.AppointmentResponseEntity;

@Component
public class AppointmentResponseEntityToFHIRAppointmentResponse
        implements Transformer<AppointmentResponseEntity, AppointmentResponse> {
    @Override
    public AppointmentResponse transform(AppointmentResponseEntity ent) {
        var obj = AppointmentResponseEntity.toAppointmentResponse(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "AppointmentResponse-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;
    }

}
