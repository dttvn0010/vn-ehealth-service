package vn.ehealth.hl7.fhir.term.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.CodeSystem;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.term.entity.CodeSystemEntity;

/**
 * @author sonvt
 * @since 2019
 * @version 1.0
 */
@Component
public class CodeSystemEntityToFHIRCodeSystem implements Transformer<CodeSystemEntity, CodeSystem> {

    @Override
    public CodeSystem transform(CodeSystemEntity ent) {
        var obj = CodeSystemEntity.toCodeSystem(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "CodeSystem-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhir_id);
        return obj;
        
    }
}
