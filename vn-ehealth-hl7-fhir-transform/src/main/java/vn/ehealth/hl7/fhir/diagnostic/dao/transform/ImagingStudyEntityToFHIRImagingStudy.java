package vn.ehealth.hl7.fhir.diagnostic.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.ImagingStudy;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.diagnostic.entity.ImagingStudyEntity;

@Component
public class ImagingStudyEntityToFHIRImagingStudy implements Transformer<ImagingStudyEntity, ImagingStudy> {
    @Override
    public ImagingStudy transform(ImagingStudyEntity ent) {
        var obj = ImagingStudyEntity.toImagingStudy(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "ImagingStudy-v1.0"));
        obj.setExtension(ent.extension);
        obj.setId(ent.fhir_id);
        return obj;
    }
}
