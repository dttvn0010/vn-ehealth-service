package vn.ehealth.hl7.fhir.provider.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.HealthcareService;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.provider.entity.HealthcareServiceEntity;

@Component
public class HealthcareServiceEntityToFHIRHealthcareService
        implements Transformer<HealthcareServiceEntity, HealthcareService> {
    /**
     * @author sonvt Convert entity to healthcareServiceect
     */
    @Override
    public HealthcareService transform(HealthcareServiceEntity ent) {
        var obj = HealthcareServiceEntity.toHealthcareService(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "HealthcareService-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhir_id);
        return obj;
    }
}
