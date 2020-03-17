package vn.ehealth.hl7.fhir.medication.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.medication.entity.MedicationStatementEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class MedicationStatementEntityToFHIRMedicationStatement
        implements Transformer<MedicationStatementEntity, MedicationStatement> {
    @Override
    public MedicationStatement transform(MedicationStatementEntity ent) {
        var obj = MedicationStatementEntity.toMedicationStatement(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "MedicationStatement-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;        
    }
}
