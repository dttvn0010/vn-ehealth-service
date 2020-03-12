package vn.ehealth.hl7.fhir.schedule.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Appointment;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.schedule.entity.AppointmentEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class AppointmentEntityToFHIRAppointment implements Transformer<AppointmentEntity, Appointment> {
    @Override
    public Appointment transform(AppointmentEntity ent) {
        var obj = AppointmentEntity.toAppointment(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Appointment-v1.0"));
        obj.setExtension(DataConvertUtil.transform(ent.extension, vn.ehealth.hl7.fhir.core.entity.BaseExtension::toExtension));
        obj.setId(ent.fhir_id);
        return obj;
        
        
    }
}
