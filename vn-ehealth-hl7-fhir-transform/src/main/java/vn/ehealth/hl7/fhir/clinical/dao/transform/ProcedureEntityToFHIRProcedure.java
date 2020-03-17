package vn.ehealth.hl7.fhir.clinical.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Procedure;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.clinical.entity.ProcedureEntity;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class ProcedureEntityToFHIRProcedure implements Transformer<ProcedureEntity, Procedure> {
    @Override
    public Procedure transform(ProcedureEntity ent) {
        var obj = ProcedureEntity.toProcedure(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Procedure-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;
    }
}
