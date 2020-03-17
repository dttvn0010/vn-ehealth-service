package vn.ehealth.hl7.fhir.patient.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.patient.entity.RelatedPersonEntity;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Component
public class RelatedPersonEntityToFHIRRelatedPerson implements Transformer<RelatedPersonEntity, RelatedPerson> {
    @Override
    public RelatedPerson transform(RelatedPersonEntity ent) {
        var obj = RelatedPersonEntity.toRelatedPerson(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "RelatedPerson-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;        
    }

}
