package vn.ehealth.hl7.fhir.provider.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Device;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.provider.entity.DeviceEntity;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Component
public class DeviceEntityToFHIRDevice implements Transformer<DeviceEntity, Device> {
    @Override
    public Device transform(DeviceEntity ent) {
        var obj = DeviceEntity.toDevice(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Device-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhir_id);
        return obj;
        
    }
}
