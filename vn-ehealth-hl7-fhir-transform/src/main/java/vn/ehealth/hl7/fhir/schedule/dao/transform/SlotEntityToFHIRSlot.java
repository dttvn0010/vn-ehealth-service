package vn.ehealth.hl7.fhir.schedule.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Slot;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.schedule.entity.SlotEntity;

@Component
public class SlotEntityToFHIRSlot implements Transformer<SlotEntity, Slot> {
    @Override
    public Slot transform(SlotEntity ent) {
        var obj = SlotEntity.toSlot(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Slot-v1.0"));
        obj.setExtension(DataConvertUtil.transform(ent.extension, vn.ehealth.hl7.fhir.core.entity.BaseExtension::toExtension));
        obj.setId(ent.fhir_id);
        return obj;
    }
}
