package vn.ehealth.hl7.fhir.term.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.ConceptMap;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.term.entity.ConceptMapEntity;

/**
 * @author sonvt
 * @since 2019
 * @version 1.0
 */
@Component
public class ConceptMapEntityToFHIRConceptMap implements Transformer<ConceptMapEntity, ConceptMap> {
    @Override
    public ConceptMap transform(ConceptMapEntity ent) {
        var obj = ConceptMapEntity.toConceptMap(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "ConceptMap-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;
    }
}
