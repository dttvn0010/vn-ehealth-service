package vn.ehealth.hl7.fhir.core.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Meta;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

public class DataConvertUtil {
    public static <T, U> List<U> transform(List<T> lst, Function<T, U> func) {
        if(lst != null) {
            return lst.stream().map(x -> func.apply(x))
                    .collect(Collectors.toList());
        }
        return null;
    }
    
    public static Meta getMeta(BaseResource entity, String profile) {
        if(entity == null) return null;
        
        var meta = new Meta();
        if(entity.profile != null && entity.profile.size() > 0) {
            meta.setProfile(entity.profile);
        }else {
            meta = new Meta().addProfile(ConstantKeys.ENTITY_PROFILE_V1 + profile);
        }
        
        meta.setSecurity(entity.security);
        meta.setTag(entity.tag);
        
        if(entity.resDeleted != null) {
            meta.setLastUpdated(entity.resDeleted);
        }else if(entity.resUpdated != null) {
            meta.setLastUpdated(entity.resUpdated);
        }else if(entity.resCreated != null) {
            meta.setLastUpdated(entity.resCreated);
        }
        if(entity.version != null) {
            meta.setVersionId(String.valueOf(entity.version));
        }
        
        return meta;
    }
    
    public static void setMetaExt(DomainResource obj, BaseResource ent) {
        if(obj != null && ent != null) {
            if(obj.hasMeta()) {
                if (obj.getMeta().hasProfile()) {
                    ent.profile = obj.getMeta().getProfile();
                }
                if (obj.getMeta().hasSecurity()) {
                    ent.security = obj.getMeta().getSecurity();
                }
                if (obj.getMeta().hasTag()) {
                    ent.tag = obj.getMeta().getTag();
                }
            }
            
            if (obj.hasExtension()) {
                ent.extension = obj.getExtension();
            }
        }
    }
}