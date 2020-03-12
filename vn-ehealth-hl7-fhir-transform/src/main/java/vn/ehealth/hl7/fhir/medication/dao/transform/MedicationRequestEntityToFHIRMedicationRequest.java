package vn.ehealth.hl7.fhir.medication.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.medication.entity.MedicationRequestEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class MedicationRequestEntityToFHIRMedicationRequest implements Transformer<MedicationRequestEntity, MedicationRequest> {
    @Override
    public MedicationRequest transform(MedicationRequestEntity ent) {
        var obj = MedicationRequestEntity.toMedicationRequest(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "MedicationRequest-v1.0"));
        obj.setExtension(DataConvertUtil.transform(ent.extension, vn.ehealth.hl7.fhir.core.entity.BaseExtension::toExtension));
        obj.setId(ent.fhir_id);
        return obj;        
    }

}
