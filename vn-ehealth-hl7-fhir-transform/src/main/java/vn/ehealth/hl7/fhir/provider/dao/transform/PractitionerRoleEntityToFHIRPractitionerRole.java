package vn.ehealth.hl7.fhir.provider.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.provider.entity.PractitionerRoleEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class PractitionerRoleEntityToFHIRPractitionerRole
        implements Transformer<PractitionerRoleEntity, PractitionerRole> {
    @Override
    public PractitionerRole transform(PractitionerRoleEntity ent) {
        var obj = PractitionerRoleEntity.toPractitionerRole(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "PractitionerRole-v1.0"));
        obj.setExtension(ent.extension);
        obj.setId(ent.fhir_id);
        return obj;
    }
}
