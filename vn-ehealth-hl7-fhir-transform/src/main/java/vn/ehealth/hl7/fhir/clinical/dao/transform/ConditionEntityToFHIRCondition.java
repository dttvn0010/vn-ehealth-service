package vn.ehealth.hl7.fhir.clinical.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Condition;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.clinical.entity.ConditionEntity;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class ConditionEntityToFHIRCondition implements Transformer<ConditionEntity, Condition> {
    @Override
    public Condition transform(ConditionEntity ent) {
        var obj = ConditionEntity.toCondition(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Condition-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;
    }

}
