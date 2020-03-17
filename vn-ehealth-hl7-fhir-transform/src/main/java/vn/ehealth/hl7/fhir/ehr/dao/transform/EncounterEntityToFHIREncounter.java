package vn.ehealth.hl7.fhir.ehr.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Encounter;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.ehr.entity.EncounterEntity;

@Component
public class EncounterEntityToFHIREncounter implements Transformer<EncounterEntity, Encounter> {
    @Override
    public Encounter transform(EncounterEntity ent) {
        var obj = EncounterEntity.toEncounter(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Encounter-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;
        
    }
}
