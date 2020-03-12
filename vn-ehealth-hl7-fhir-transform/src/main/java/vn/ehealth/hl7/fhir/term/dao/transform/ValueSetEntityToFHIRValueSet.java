package vn.ehealth.hl7.fhir.term.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.ValueSet;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.term.entity.ValueSetEntity;

/**
 * @author sonvt
 * @since 2019
 * @version 1.0
 */
@Component
public class ValueSetEntityToFHIRValueSet implements Transformer<ValueSetEntity, ValueSet> {
    @Override
    public ValueSet transform(ValueSetEntity ent) {
        var obj = ValueSetEntity.toValueSet(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "CarePlan-v1.0"));
        obj.setExtension(DataConvertUtil.transform(ent.extension, vn.ehealth.hl7.fhir.core.entity.BaseExtension::toExtension));
        obj.setId(ent.fhir_id);
        return obj;
    }

}
