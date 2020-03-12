package vn.ehealth.hl7.fhir.medication.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Immunization;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.medication.entity.ImmunizationEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class ImmunizationEntityToFHIRImmunization implements Transformer<ImmunizationEntity, Immunization> {
    @Override
    public Immunization transform(ImmunizationEntity ent) {
        var obj = ImmunizationEntity.toImmunization(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Immunization-v1.0"));
        obj.setExtension(DataConvertUtil.transform(ent.extension, vn.ehealth.hl7.fhir.core.entity.BaseExtension::toExtension));
        obj.setId(ent.fhir_id);
        return obj;
        
    }
}
