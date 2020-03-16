package vn.ehealth.hl7.fhir.clinical.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.ClinicalImpression;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.clinical.entity.ClinicalImpressionEntity;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class ClinicalImpressionEntityToFHIRClinicalImpression
        implements Transformer<ClinicalImpressionEntity, ClinicalImpression> {
    @Override
    public ClinicalImpression transform(ClinicalImpressionEntity ent) {
        var obj = ClinicalImpressionEntity.toClinicalImpression(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "ClinicalImpression-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhir_id);
        return obj;
    }
}
