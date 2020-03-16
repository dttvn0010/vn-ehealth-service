package vn.ehealth.hl7.fhir.medication.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.medication.entity.MedicationDispenseEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class MedicationDispenseEntityToFHIRMedicationDispense
        implements Transformer<MedicationDispenseEntity, MedicationDispense> {
    @Override
    public MedicationDispense transform(MedicationDispenseEntity ent) {
        var obj = MedicationDispenseEntity.toMedicationDispense(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "MedicationDispense-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhir_id);
        return obj;
    }

}
