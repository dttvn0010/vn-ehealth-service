package vn.ehealth.hl7.fhir.medication.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Medication;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.medication.entity.MedicationEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class MedicationEntityToFHIRMedication implements Transformer<MedicationEntity, Medication> {
    @Override
    public Medication transform(MedicationEntity ent) {
        var obj = MedicationEntity.toMedication(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Medication-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;
    }
}
