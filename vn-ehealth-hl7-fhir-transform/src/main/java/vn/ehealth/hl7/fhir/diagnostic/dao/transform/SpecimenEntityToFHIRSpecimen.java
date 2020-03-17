package vn.ehealth.hl7.fhir.diagnostic.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Specimen;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.diagnostic.entity.SpecimenEntity;

@Component
public class SpecimenEntityToFHIRSpecimen implements Transformer<SpecimenEntity, Specimen> {
    @Override
    public Specimen transform(SpecimenEntity ent) {
        var obj = SpecimenEntity.toSpecimen(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Specimen-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;
    }
}
