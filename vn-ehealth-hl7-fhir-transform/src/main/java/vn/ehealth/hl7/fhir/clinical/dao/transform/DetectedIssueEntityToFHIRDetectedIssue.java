package vn.ehealth.hl7.fhir.clinical.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.DetectedIssue;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.clinical.entity.DetectedIssueEntity;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

/**
 * 
 * @author sonvt
 * @since 2019
 */
@Component
public class DetectedIssueEntityToFHIRDetectedIssue implements Transformer<DetectedIssueEntity, DetectedIssue> {
    @Override
    public DetectedIssue transform(DetectedIssueEntity ent) {
        var obj = DetectedIssueEntity.toDetectedIssue(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "DetectedIssue-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;
    }
}
