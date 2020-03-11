package vn.ehealth.hl7.fhir.clinical.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Goal;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.clinical.entity.GoalEntity;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class GoalEntityToFHIRGoal implements Transformer<GoalEntity, Goal> {
    @Override
    public Goal transform(GoalEntity ent) {
        var obj = GoalEntity.toGoal(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Goal-v1.0"));
        obj.setExtension(ent.extension);
        obj.setId(ent.fhir_id);
        return obj;
    
    }
}
