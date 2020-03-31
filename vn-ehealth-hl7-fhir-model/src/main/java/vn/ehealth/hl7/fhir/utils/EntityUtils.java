package vn.ehealth.hl7.fhir.utils;

import java.util.List;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.ResourceType;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseExtension;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
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
    
    public static String idFromRef(BaseReference ref) {
        if((ref != null && ref.reference != null)) {
            var arr = ref.reference.split("/");
            if(arr.length == 2) {
                return arr[1];
            }
        }
        return null;
    }
    
    public static ResourceType getResourceType(BaseReference ref) {
        if(ref != null && ref.reference != null) {
            var arr = ref.reference.split("/");
            if(arr.length == 2) {
                return ResourceType.fromCode(arr[0]);
            }
        }
        return null;
    }
}
