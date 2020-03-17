package vn.ehealth.hl7.fhir.ehr.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.EpisodeOfCare;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.ehr.entity.EpisodeOfCareEntity;

@Component
public class EpisodeOfCareEntityToFHIREpisodeOfCare implements Transformer<EpisodeOfCareEntity, EpisodeOfCare> {
    @Override
    public EpisodeOfCare transform(EpisodeOfCareEntity ent) {
        var obj = EpisodeOfCareEntity.toEpisodeOfCare(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "EpisodeOfCare-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;
        
    }
}
