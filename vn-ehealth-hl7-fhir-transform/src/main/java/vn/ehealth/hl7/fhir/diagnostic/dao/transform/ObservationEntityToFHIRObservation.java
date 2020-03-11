package vn.ehealth.hl7.fhir.diagnostic.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.diagnostic.entity.ObservationEntity;

@Component
public class ObservationEntityToFHIRObservation implements Transformer<ObservationEntity, Observation> {
    @Override
    public Observation transform(ObservationEntity ent) {
        var obj = ObservationEntity.toObservation(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Observation-v1.0"));
        obj.setExtension(ent.extension);
        obj.setId(ent.fhir_id);
        return obj;
        
    }
}
