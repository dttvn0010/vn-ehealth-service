package vn.ehealth.hl7.fhir.medication.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.medication.entity.MedicationAdministrationEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class MedicationAdministrationEntityToFHIRMedicationAdministration
        implements Transformer<MedicationAdministrationEntity, MedicationAdministration> {
    @Override
    public MedicationAdministration transform(MedicationAdministrationEntity ent) {
        var obj = MedicationAdministrationEntity.toMedicationAdministration(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "MedicationAdministration-v1.0"));
        obj.setExtension(ent.extension);
        obj.setId(ent.fhir_id);
        return obj;
    }
}
