package vn.ehealth.hl7.fhir.provider.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.provider.entity.PractitionerEntity;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class PractitionerEntityToFHIRPractitioner implements Transformer<PractitionerEntity, Practitioner> {
    @Override
    public Practitioner transform(PractitionerEntity ent) {
        var obj = PractitionerEntity.toPractitioner(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Practitioner-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;
        
    }
}
