package vn.ehealth.hl7.fhir.ehr.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.CareTeam;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.ehr.entity.CareTeamEntity;

@Component
public class CareTeamEntityToFHIRCareTeam implements Transformer<CareTeamEntity, CareTeam> {
    @Override
    public CareTeam transform(CareTeamEntity ent) {
        var obj = CareTeamEntity.toCareTeam(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "CareTeam-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;
    }
}
