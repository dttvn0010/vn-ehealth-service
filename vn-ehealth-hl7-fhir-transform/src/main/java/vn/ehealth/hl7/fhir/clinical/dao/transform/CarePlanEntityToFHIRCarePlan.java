package vn.ehealth.hl7.fhir.clinical.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.CarePlan;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.clinical.entity.CarePlanEntity;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class CarePlanEntityToFHIRCarePlan implements Transformer<CarePlanEntity, CarePlan> {
    @Override
    public CarePlan transform(CarePlanEntity ent) {
        var obj = CarePlanEntity.toCarePlan(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "CarePlan-v1.0"));
        obj.setExtension(DataConvertUtil.transform(ent.extension, vn.ehealth.hl7.fhir.core.entity.BaseExtension::toExtension));
        obj.setId(ent.fhir_id);
        return obj;
    }
}
