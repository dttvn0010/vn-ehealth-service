package vn.ehealth.hl7.fhir.schedule.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Schedule;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.schedule.entity.ScheduleEntity;

@Component
public class ScheduleEntityToFHIRSchedule implements Transformer<ScheduleEntity, Schedule> {
    @Override
    public Schedule transform(ScheduleEntity ent) {
        var obj = ScheduleEntity.toSchedule(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Schedule-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhir_id);
        return obj;
        
    }
}
