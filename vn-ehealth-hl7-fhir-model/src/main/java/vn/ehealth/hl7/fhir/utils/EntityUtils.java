package vn.ehealth.hl7.fhir.utils;

import java.util.List;

import javax.annotation.Nonnull;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseExtension;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

public class EntityUtils {

    public static BaseExtension findExtensionByURL(List<BaseExtension> lst, @Nonnull String url) {
        return FPUtil.findFirst(lst, x -> url.equals(x.url));
    }
    
    public static BaseCodeableConcept findConceptBySystem(List<BaseCodeableConcept> concepts, @Nonnull String system) {
        if(concepts != null) {
            for(var concept : concepts) {
                if(FPUtil.anyMatch(concept.coding, x -> system.equals(x.system))) {
                    return concept;
                }
            }
        }
        return null;
    }
}
