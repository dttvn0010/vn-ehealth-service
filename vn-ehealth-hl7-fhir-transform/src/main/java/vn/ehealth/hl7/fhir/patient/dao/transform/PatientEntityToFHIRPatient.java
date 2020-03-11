package vn.ehealth.hl7.fhir.patient.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Component
public class PatientEntityToFHIRPatient implements Transformer<PatientEntity, Patient> {
    @Override
    public Patient transform(PatientEntity ent) {
        var obj = PatientEntity.toPatient(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Patient-v1.0"));
        obj.setExtension(ent.extension);
        obj.setId(ent.fhir_id);
        return obj;        
    }
}
